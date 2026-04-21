package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.PriceParser;
import java.util.List;

public class CartPage extends BasePage {

    @FindBy(css = "ul.nav.topcart a")
    private WebElement cartLink;

    @FindBy(css = "div.cart-info tbody tr:has(input[name*='quantity'])")
    private List<WebElement> cartRows;

    @FindBy(id = "cart_update")
    private WebElement updateButton;

    @FindBy(css = "#totals_table tr:last-child td:last-child")
    private WebElement totalAmount;

    private final By cartLinkBy = By.cssSelector("ul.nav.topcart a");
    private final By cartRowsBy = By.cssSelector("div.cart-info tbody tr:has(input[name*='quantity'])");
    private final By cartInfoBy = By.cssSelector("div.cart-info");
    private final By updateButtonBy = By.id("cart_update");
    private final By totalAmountBy = By.cssSelector("#totals_table tr:last-child td:last-child");
    private final By priceElementBy = By.cssSelector("td.align_right");
    private final By quantityFieldBy = By.cssSelector("input[name*='quantity']");
    private final By removeButtonBy = By.cssSelector("a[href*='remove']");
    private final By shippingCostBy = By.xpath("//tr[td[contains(text(),'Flat Shipping Rate')]]/td[last()]");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    @Step("Переход в корзину")
    public void goToCart() {
        wait.until(ExpectedConditions.presenceOfElementLocated(cartLinkBy));
        WebElement link = driver.findElement(cartLinkBy);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", link);
        wait.until(ExpectedConditions.elementToBeClickable(link));
        link.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(cartInfoBy));
    }

    @Step("Получение актуального списка строк корзины")
    public void refreshCartRows() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(cartRowsBy));
    }

    @Step("Получение количества товаров в корзине")
    public int getCartItemsCount() {
        refreshCartRows();
        return cartRows.size();
    }

    @Step("Проверка, есть ли товары в корзине")
    public boolean hasItems() {
        refreshCartRows();
        return !cartRows.isEmpty();
    }

    private double getRowPrice(WebElement row) {
        WebElement priceElement = row.findElement(priceElementBy);
        return PriceParser.parsePrice(priceElement.getText());
    }

    private int getRowQuantity(WebElement row) {
        WebElement quantityField = row.findElement(quantityFieldBy);
        String value = quantityField.getAttribute("value");
        return value.isEmpty() ? 0 : Integer.parseInt(value);
    }

    private double getShippingCost() {
        List<WebElement> shippingElements = driver.findElements(shippingCostBy);
        if (!shippingElements.isEmpty()) {
            String text = shippingElements.get(0).getText().trim();
            return PriceParser.parsePrice(text);
        }
        return 2.0;
    }

    @Step("Удаление товара по индексу {index}")
    public void removeItemByIndex(int index) {
        refreshCartRows();
        if (index < cartRows.size()) {
            WebElement row = cartRows.get(index);
            WebElement removeButton = row.findElement(removeButtonBy);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", removeButton);
            wait.until(ExpectedConditions.elementToBeClickable(removeButton));
            removeButton.click();
            wait.until(ExpectedConditions.stalenessOf(row));
            wait.until(ExpectedConditions.presenceOfElementLocated(updateButtonBy));
        }
    }

    @Step("Удаление всех чётных по порядку товаров")
    public void removeEvenItems() {
        refreshCartRows();
        for (int i = cartRows.size() - 1; i >= 0; i--) {
            if (i % 2 != 0) {
                removeItemByIndex(i);
            }
        }
    }

    @Step("Поиск индекса самого дешёвого товара")
    public int findCheapestItemIndex() {
        refreshCartRows();
        double minPrice = Double.MAX_VALUE;
        int minIndex = -1;
        for (int i = 0; i < cartRows.size(); i++) {
            try {
                double price = getRowPrice(cartRows.get(i));
                if (price < minPrice) {
                    minPrice = price;
                    minIndex = i;
                }
            } catch (Exception ignored) {
            }
        }
        return minIndex;
    }

    @Step("Удвоение количества самого дешёвого товара")
    public void doubleCheapestItemQuantity() {
        int minIndex = findCheapestItemIndex();
        if (minIndex != -1) {
            refreshCartRows();
            WebElement cheapestRow = cartRows.get(minIndex);
            WebElement quantityField = cheapestRow.findElement(quantityFieldBy);
            int currentQuantity = getRowQuantity(cheapestRow);
            int newQuantity = currentQuantity * 2;
            quantityField.clear();
            quantityField.sendKeys(String.valueOf(newQuantity));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", updateButton);
            wait.until(ExpectedConditions.elementToBeClickable(updateButton));
            updateButton.click();
            wait.until(ExpectedConditions.presenceOfElementLocated(updateButtonBy));
        }
    }

    @Step("Расчёт ожидаемой суммы корзины")
    public double calculateExpectedTotal() {
        refreshCartRows();
        double subtotal = 0;
        for (WebElement row : cartRows) {
            try {
                double price = getRowPrice(row);
                int quantity = getRowQuantity(row);
                subtotal += price * quantity;
            } catch (Exception ignored) {
            }
        }
        double shipping = getShippingCost();
        return subtotal + shipping;
    }

    @Step("Получение фактической итоговой суммы")
    public double getTotalAmount() {
        wait.until(ExpectedConditions.presenceOfElementLocated(totalAmountBy));
        String cleanedText = totalAmount.getText().replaceAll("[^0-9.]", "");
        if (cleanedText.isEmpty()) {
            return 0.0;
        }
        return Double.parseDouble(cleanedText);
    }
}