package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class HomePage extends BasePage {
    private final Random random = new Random();

    @FindBy(css = "a.prdocutname")
    private List<WebElement> productNames;

    private final By productQuantityLocator = By.id("product_quantity");
    private final By addToCartButtonLocator = By.cssSelector("a.cart");
    private final By productListLocator = By.cssSelector("a.prdocutname");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    @Step("Открытие главной страницы")
    public void open() {
        driver.get("https://automationteststore.com/");
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(productListLocator));
    }

    @Step("Обновление страницы")
    public void refresh() {
        driver.navigate().refresh();
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(productListLocator));
    }

    @Step("Получение списка всех названий товаров")
    public List<String> getAllProductNames() {
        List<String> allProductNames = new ArrayList<>();
        for (WebElement product : productNames) {
            allProductNames.add(product.getText());
        }
        return allProductNames;
    }

    @Step("Получение списка доступных для добавления товаров (не добавленных ранее)")
    public List<String> getAvailableProducts(Set<String> alreadyAdded) {
        List<String> allProductNames = getAllProductNames();
        List<String> availableProducts = new ArrayList<>();
        for (String name : allProductNames) {
            if (!alreadyAdded.contains(name)) {
                availableProducts.add(name);
            }
        }
        return availableProducts;
    }

    @Step("Выбор случайного товара из доступных")
    public String selectRandomProduct(List<String> availableProducts) {
        if (availableProducts.isEmpty()) {
            return null;
        }
        return availableProducts.get(random.nextInt(availableProducts.size()));
    }

    @Step("Клик по товару с названием {productName}")
    public void clickOnProduct(String productName) {
        for (WebElement product : productNames) {
            if (product.getText().equals(productName)) {
                helper.scrollAndWaitForClickable(product);
                product.click();
                break;
            }
        }
    }

    @Step("Установка количества товара: {quantity}")
    public void setProductQuantity(int quantity) {
        wait.until(ExpectedConditions.presenceOfElementLocated(productQuantityLocator));
        WebElement quantityField = driver.findElement(productQuantityLocator);
        quantityField.clear();
        quantityField.sendKeys(String.valueOf(quantity));
    }

    @Step("Добавление товара в корзину")
    public void addToCart() {
        WebElement addButton = driver.findElement(addToCartButtonLocator);
        addButton.click();
    }

    @Step("Генерация случайного количества от 1 до 5")
    public int getRandomQuantity() {
        return random.nextInt(5) + 1;
    }

    @Step("Добавление случайного уникального товара в корзину")
    public String addRandomUniqueProductToCart(Set<String> alreadyAdded) {
        List<String> availableProducts = getAvailableProducts(alreadyAdded);
        String productName = selectRandomProduct(availableProducts);

        if (productName == null) {
            return null;
        }

        int quantity = getRandomQuantity();
        clickOnProduct(productName);
        setProductQuantity(quantity);
        addToCart();

        return productName;
    }

    @Step("Добавление {count} случайных уникальных товаров в корзину")
    public Set<String> addMultipleRandomProductsToCart(int count) {
        Set<String> addedProducts = new java.util.HashSet<>();

        for (int i = 0; i < count * 2 && addedProducts.size() < count; i++) {
            open();
            refresh();
            String addedProduct = addRandomUniqueProductToCart(addedProducts);
            if (addedProduct != null) {
                addedProducts.add(addedProduct);
            }
        }

        return addedProducts;
    }
}