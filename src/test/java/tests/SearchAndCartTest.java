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
import pages.CartPage;
import pages.HomePage;
import pages.SearchPage;
import java.time.Duration;

@Epic("Интернет-магазин AutomationTestStore")
@Feature("Корзина и поиск")
public class SearchAndCartTest {
    private WebDriver driver;
    private SearchPage searchPage;
    private CartPage cartPage;
    private HomePage homePage;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.get("https://automationteststore.com/");
        searchPage = new SearchPage(driver);
        cartPage = new CartPage(driver);
        homePage = new HomePage(driver);
    }

    @Test
    @Story("Поиск товара и добавление в корзину")
    @Description("Проверка поиска 'shirt', добавления 2-го и 3-го товара со случайным количеством, удвоения самого дешёвого товара и итоговой суммы")
    @Severity(SeverityLevel.CRITICAL)
    public void testSearchAddToCartAndVerifyTotal() {
        homePage.open();
        searchPage.search("shirt");
        searchPage.selectSortBy("Name A - Z");
        int qty2 = searchPage.getRandomQuantity();
        searchPage.addToCart(2, qty2);

        homePage.open();
        searchPage.search("shirt");
        searchPage.selectSortBy("Name A - Z");
        int qty3 = searchPage.getRandomQuantity();
        searchPage.addToCart(3, qty3);

        cartPage.goToCart();
        cartPage.doubleCheapestItemQuantity();

        double expectedTotal = cartPage.calculateExpectedTotal();
        double actualTotal = cartPage.getTotalAmount();

        double roundedExpected = Math.round(expectedTotal * 100.0) / 100.0;
        double roundedActual = Math.round(actualTotal * 100.0) / 100.0;

        Assert.assertEquals(roundedActual, roundedExpected, 0.01, "Итоговая сумма не совпадает");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}