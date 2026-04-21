package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import java.util.List;
import java.util.Random;

public class SearchPage extends BasePage {
    private final Random random = new Random();

    @FindBy(id = "filter_keyword")
    private WebElement searchField;

    @FindBy(css = "div.button-in-search")
    private WebElement searchButton;

    @FindBy(id = "sort")
    private WebElement sortDropdown;

    @FindBy(css = "a.prdocutname")
    private List<WebElement> productNames;

    @FindBy(id = "product_quantity")
    private WebElement productQuantityField;

    @FindBy(css = "a.cart")
    private WebElement addToCartButton;

    private final By productNamesBy = By.cssSelector("a.prdocutname");
    private final By productQuantityBy = By.id("product_quantity");
    private final By addToCartBy = By.cssSelector("a.cart");
    private final By cartConfirmationBy = By.cssSelector("div.cart-info tbody tr, a.cart");
    private final By searchResultsBy = By.cssSelector("div.product-grid, div.thumbnails, div.searchresults");

    public SearchPage(WebDriver driver) {
        super(driver);
    }

    @Step("Поиск товара: {keyword}")
    public void search(String keyword) {
        helper.waitForVisibility(searchField);
        searchField.clear();
        searchField.sendKeys(keyword);
        searchButton.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(searchResultsBy));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(productNamesBy));
    }

    @Step("Сортировка по: {visibleText}")
    public void selectSortBy(String visibleText) {
        helper.waitForVisibility(sortDropdown);
        new Select(sortDropdown).selectByVisibleText(visibleText);
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(productNamesBy));
    }

    @Step("Генерация случайного количества от 1 до 5")
    public int getRandomQuantity() {
        return random.nextInt(5) + 1;
    }

    @Step("Получение количества найденных товаров")
    public int getProductsCount() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(productNamesBy));
        return productNames.size();
    }

    @Step("Проверка, что найдено достаточно товаров (минимум {minCount})")
    public boolean hasEnoughProducts(int minCount) {
        return getProductsCount() >= minCount;
    }

    @Step("Клик по товару с номером {productNumber}")
    public void clickOnProductByNumber(int productNumber) {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(productNamesBy));
        int index = productNumber - 1;
        if (index < productNames.size()) {
            WebElement productLink = productNames.get(index);
            helper.scrollAndWaitForClickable(productLink);
            productLink.click();
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
        wait.until(ExpectedConditions.presenceOfElementLocated(cartConfirmationBy));
    }

    @Step("Добавление товара №{productNumber} в количестве {quantity}")
    public void addToCart(int productNumber, int quantity) {
        clickOnProductByNumber(productNumber);
        setProductQuantity(quantity);
        addToCart();
    }
}