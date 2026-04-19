package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.Helper;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class CategoryPage {
    private final WebDriver driver;
    private final Helper helper;

    @FindBy(id = "sort")
    WebElement sortDropdown;

    public CategoryPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        this.helper = new Helper(driver);
    }

    @Step("Открытие категории по URL: {url}")
    public void openCategory(String url) {
        driver.get(url);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("a.prdocutname")));
    }

    @Step("Выбор сортировки: {visibleText}")
    public void selectSortBy(String visibleText) {
        helper.waitForVisibility(sortDropdown);
        Select select = new Select(sortDropdown);
        select.selectByVisibleText(visibleText);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("a.prdocutname")));
    }

    @Step("Получение списка названий товаров")
    public List<String> getProductNamesList() {
        List<WebElement> productNames = driver.findElements(By.cssSelector("a.prdocutname"));
        if (productNames.isEmpty()) {
            productNames = driver.findElements(By.cssSelector("div.product-name a"));
        }

        List<String> names = new ArrayList<>();
        for (WebElement name : productNames) {
            String text = name.getText().trim();
            if (!text.isEmpty()) {
                names.add(text);
            }
        }
        return names;
    }

    @Step("Получение списка цен товаров")
    public List<Double> getProductPricesList() {
        List<WebElement> productPrices = driver.findElements(By.cssSelector("div.pricetag span.oneprice, div.pricetag span.pricenew, div.product-price span"));

        List<Double> prices = new ArrayList<>();
        for (WebElement price : productPrices) {
            String priceText = String.valueOf(Helper.parsePrice(price.getText()));
            if (!priceText.isEmpty()) {
                try {
                    prices.add(Double.parseDouble(priceText));
                } catch (NumberFormatException ignored) {

                }
            }
        }
        return prices;
    }
}