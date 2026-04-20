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

    public SearchPage(WebDriver driver) {
        super(driver);
    }

    @Step("Поиск товара: {keyword}")
    public void search(String keyword) {
        helper.waitForVisibility(searchField);
        searchField.clear();
        searchField.sendKeys(keyword);
        searchButton.click();
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("a.prdocutname")));
    }

    @Step("Сортировка по: {visibleText}")
    public void selectSortBy(String visibleText) {
        helper.waitForVisibility(sortDropdown);
        new Select(sortDropdown).selectByVisibleText(visibleText);
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("a.prdocutname")));
    }

    @Step("Добавление товара №{productNumber} в количестве {quantity}")
    public void addToCart(int productNumber, int quantity) {
        int index = productNumber - 1;
        if (index >= productNames.size()) {
            return;
        }
        WebElement productLink = productNames.get(index);
        helper.scrollAndWaitForClickable(productLink);
        productLink.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("product_quantity")));

        WebElement quantityField = driver.findElement(By.id("product_quantity"));
        quantityField.clear();
        quantityField.sendKeys(String.valueOf(quantity));

        WebElement addButton = driver.findElement(By.cssSelector("a.cart"));
        addButton.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.cart-info tbody tr, a.cart")));
    }

    @Step("Генерация случайного количества")
    public int getRandomQuantity() {
        return random.nextInt(5) + 1;
    }
}