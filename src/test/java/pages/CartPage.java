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
    @FindBy(css = "div.cart-info tbody tr:has(input[name*='quantity'])")
    private List<WebElement> cartRows;

    @FindBy(id = "cart_update")
    private WebElement updateButton;

    @FindBy(css = "#totals_table tr:last-child td:last-child")
    private WebElement totalAmount;

    public CartPage(WebDriver driver) {
        super(driver);
    }

    @Step("Переход в корзину")
    public void goToCart() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[href*='checkout/cart']")));
        WebElement cartLink = driver.findElement(By.cssSelector("a[href*='checkout/cart']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", cartLink);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.cart-info tbody tr")));
    }

    private double getRowPrice(WebElement row) {
        WebElement priceElement = row.findElement(By.cssSelector("td.align_right"));
        return PriceParser.parsePrice(priceElement.getText());
    }

    private int getRowQuantity(WebElement row) {
        WebElement quantityField = row.findElement(By.cssSelector("input[name*='quantity']"));
        return Integer.parseInt(quantityField.getAttribute("value"));
    }

    private double getShippingCost() {
        try {
            WebElement shippingRow = driver.findElement(By.xpath("//tr[td/span[contains(text(),'Flat Shipping Rate')]]"));
            WebElement shippingValue = shippingRow.findElement(By.cssSelector("td:last-child span"));
            return PriceParser.parsePrice(shippingValue.getText());
        } catch (Exception e) {
            return 0.0;
        }
    }

    @Step("Удаление товара по индексу {index}")
    public void removeItemByIndex(int index) {
        if (index < cartRows.size()) {
            WebElement row = cartRows.get(index);
            WebElement removeButton = row.findElement(By.cssSelector("a[href*='remove']"));
            helper.scrollAndWaitForClickable(removeButton);
            removeButton.click();
            wait.until(ExpectedConditions.stalenessOf(row));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("cart_update")));
        }
    }

    @Step("Удаление всех чётных по порядку товаров")
    public void removeEvenItems() {
        for (int i = cartRows.size() - 1; i >= 0; i--) {
            if (i % 2 != 0) {
                removeItemByIndex(i);
            }
        }
    }

    @Step("Удвоение количества самого дешёвого товара")
    public void doubleCheapestItemQuantity() {
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
        if (minIndex != -1) {
            WebElement cheapestRow = cartRows.get(minIndex);
            WebElement quantityField = cheapestRow.findElement(By.cssSelector("input[name*='quantity']"));
            int currentQuantity = getRowQuantity(cheapestRow);
            int newQuantity = currentQuantity * 2;
            quantityField.clear();
            quantityField.sendKeys(String.valueOf(newQuantity));
            helper.scrollAndWaitForClickable(updateButton);
            updateButton.click();
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("cart_update")));
        }
    }

    @Step("Расчёт ожидаемой суммы корзины")
    public double calculateExpectedTotal() {
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
        wait.until(ExpectedConditions.visibilityOf(totalAmount));
        String cleanedText = totalAmount.getText().replaceAll("[^0-9.]", "");
        if (cleanedText.isEmpty()) {
            return 0.0;
        }
        return Double.parseDouble(cleanedText);
    }
}