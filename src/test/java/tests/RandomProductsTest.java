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

import java.util.HashSet;
import java.util.Set;

@Epic("Интернет-магазин AutomationTestStore")
@Feature("Корзина")
public class RandomProductsTest {
    private WebDriver driver;
    private HomePage homePage;
    private CartPage cartPage;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        homePage = new HomePage(driver);
        cartPage = new CartPage(driver);
    }

    @Test
    @Story("Удаление чётных товаров из корзины")
    @Description("Добавление 5 случайных уникальных товаров со случайным количеством, удаление чётных по порядку и проверка уменьшения суммы")
    @Severity(SeverityLevel.NORMAL)
    public void testRandomProductsAndRemoveEven() {
        Set<String> addedProducts = new HashSet<>();
        int requiredProducts = 5;

        while (addedProducts.size() < requiredProducts) {
            homePage.open();
            driver.navigate().refresh();
            String addedProduct = homePage.addRandomProductToCart(addedProducts);
            if (addedProduct != null) {
                addedProducts.add(addedProduct);
            }
        }

        homePage.open();
        driver.navigate().refresh();
        cartPage.goToCart();

        double totalBefore = cartPage.calculateExpectedTotal();
        cartPage.removeEvenItems();
        double totalAfter = cartPage.calculateExpectedTotal();

        Assert.assertTrue(totalAfter < totalBefore);
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}