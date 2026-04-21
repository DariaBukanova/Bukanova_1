package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import utils.PriceParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CategoryPage extends BasePage {

    @FindBy(id = "sort")
    private WebElement sortDropdown;

    private final By productNamesBy = By.cssSelector("a.prdocutname");
    private final By productPricesBy = By.cssSelector("div.pricetag span.oneprice, div.pricetag span.pricenew, div.product-price span, div.oneprice, div.pricenew, span.oneprice, span.pricenew");
    private final By pageHeaderBy = By.cssSelector("header, nav, div.container-fluid");

    public CategoryPage(WebDriver driver) {
        super(driver);
    }

    @Step("Открытие категории по URL: {url}")
    public void openCategory(String url) {
        driver.get(url);
        wait.until(ExpectedConditions.presenceOfElementLocated(pageHeaderBy));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(productNamesBy));
    }

    @Step("Выбор сортировки: {visibleText}")
    public void selectSortBy(String visibleText) {
        helper.waitForVisibility(sortDropdown);
        Select select = new Select(sortDropdown);
        select.selectByVisibleText(visibleText);
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(productNamesBy));
    }

    @Step("Получение списка названий товаров")
    public List<String> getProductNamesList() {
        List<WebElement> elements = driver.findElements(productNamesBy);
        List<String> names = new ArrayList<>();
        for (WebElement name : elements) {
            String text = name.getText().trim();
            if (!text.isEmpty()) {
                names.add(text);
            }
        }
        return names;
    }

    @Step("Получение списка цен товаров")
    public List<Double> getProductPricesList() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(productPricesBy));
        List<WebElement> priceElements = driver.findElements(productPricesBy);
        List<Double> prices = new ArrayList<>();
        for (WebElement price : priceElements) {
            String priceText = price.getText().trim();
            if (!priceText.isEmpty()) {
                double parsedPrice = PriceParser.parsePrice(priceText);
                if (parsedPrice > 0) {
                    prices.add(parsedPrice);
                }
            }
        }
        return prices;
    }

    @Step("Проверка сортировки по имени A-Z")
    public boolean isSortedByNameAZ() {
        List<String> names = getProductNamesList();
        List<String> sortedNames = new ArrayList<>(names);
        Collections.sort(sortedNames);
        return names.equals(sortedNames);
    }

    @Step("Проверка сортировки по имени Z-A")
    public boolean isSortedByNameZA() {
        List<String> names = getProductNamesList();
        List<String> sortedNames = new ArrayList<>(names);
        sortedNames.sort(Collections.reverseOrder());
        return names.equals(sortedNames);
    }

    @Step("Проверка сортировки по цене Low > High")
    public boolean isSortedByPriceLowHigh() {
        List<Double> prices = getProductPricesList();
        List<Double> sortedPrices = new ArrayList<>(prices);
        Collections.sort(sortedPrices);
        return prices.equals(sortedPrices);
    }

    @Step("Проверка сортировки по цене High > Low")
    public boolean isSortedByPriceHighLow() {
        List<Double> prices = getProductPricesList();
        List<Double> sortedPrices = new ArrayList<>(prices);
        sortedPrices.sort(Collections.reverseOrder());
        return prices.equals(sortedPrices);
    }
}