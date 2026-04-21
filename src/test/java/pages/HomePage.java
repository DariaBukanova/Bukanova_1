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

    @FindBy(id = "product_quantity")
    private WebElement productQuantityField;

    @FindBy(css = "a.cart")
    private WebElement addToCartButton;

    private final By productNamesBy = By.cssSelector("a.prdocutname");
    private final By productQuantityBy = By.id("product_quantity");
    private final By addToCartBy = By.cssSelector("a.cart");
    private final By pageHeaderBy = By.cssSelector("header, nav, div.container-fluid");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    @Step("Открытие главной страницы")
    public void open() {
        driver.get("https://automationteststore.com/");
        wait.until(ExpectedConditions.presenceOfElementLocated(pageHeaderBy));
    }

    @Step("Получение списка всех названий товаров")
    public List<String> getAllProductNames() {
        List<WebElement> elements = driver.findElements(productNamesBy);
        List<String> allProductNames = new ArrayList<>();
        for (WebElement product : elements) {
            String text = product.getText().trim();
            if (!text.isEmpty()) {
                allProductNames.add(text);
            }
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
        List<WebElement> elements = driver.findElements(productNamesBy);
        for (WebElement product : elements) {
            if (product.getText().trim().equals(productName)) {
                helper.scrollAndWaitForClickable(product);
                product.click();
                break;
            }
        }
    }

    @Step("Установка количества товара: {quantity}")
    public void setProductQuantity(int quantity) {
        wait.until(ExpectedConditions.presenceOfElementLocated(productQuantityBy));
        productQuantityField.clear();
        productQuantityField.sendKeys(String.valueOf(quantity));
    }

    @Step("Добавление товара в корзину")
    public void addToCart() {
        wait.until(ExpectedConditions.elementToBeClickable(addToCartBy));
        addToCartButton.click();
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

        for (int i = 0; i < count * 3 && addedProducts.size() < count; i++) {
            open();
            String addedProduct = addRandomUniqueProductToCart(addedProducts);
            if (addedProduct != null) {
                addedProducts.add(addedProduct);
            }
        }

        return addedProducts;
    }
}