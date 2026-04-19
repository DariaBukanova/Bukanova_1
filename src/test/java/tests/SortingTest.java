package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.CategoryPage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Epic("Интернет-магазин AutomationTestStore")
@Feature("Каталог и сортировка")
public class SortingTest {
    WebDriver driver;
    CategoryPage categoryPage;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        categoryPage = new CategoryPage(driver);
    }

    @Test
    @Story("Сортировка товаров в категории")
    @Description("Проверка сортировки по имени и цене в обоих направлениях в категории Shoes")
    @Severity(SeverityLevel.NORMAL)
    public void testSortingByNameAndPrice() {
        categoryPage.openCategory("https://automationteststore.com/index.php?rt=product/category&path=68");

        categoryPage.selectSortBy("Name A - Z");
        List<String> namesAZ = categoryPage.getProductNamesList();
        List<String> sortedNamesAZ = new ArrayList<>(namesAZ);
        Collections.sort(sortedNamesAZ);
        Assert.assertEquals(namesAZ, sortedNamesAZ, "Сортировка по имени A-Z работает неверно");

        categoryPage.selectSortBy("Name Z - A");
        List<String> namesZA = categoryPage.getProductNamesList();
        List<String> sortedNamesZA = new ArrayList<>(namesZA);
        sortedNamesZA.sort(Collections.reverseOrder());
        Assert.assertEquals(namesZA, sortedNamesZA, "Сортировка по имени Z-A работает неверно");

        categoryPage.selectSortBy("Price Low > High");
        List<Double> pricesLowHigh = categoryPage.getProductPricesList();
        List<Double> sortedPricesLowHigh = new ArrayList<>(pricesLowHigh);
        Collections.sort(sortedPricesLowHigh);
        Assert.assertEquals(pricesLowHigh, sortedPricesLowHigh, "Сортировка по цене Low > High работает неверно");

        categoryPage.selectSortBy("Price High > Low");
        List<Double> pricesHighLow = categoryPage.getProductPricesList();
        List<Double> sortedPricesHighLow = new ArrayList<>(pricesHighLow);
        sortedPricesHighLow.sort(Collections.reverseOrder());
        Assert.assertEquals(pricesHighLow, sortedPricesHighLow, "Сортировка по цене High > Low работает неверно");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}