package tests;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.HomePage;

import java.util.HashSet;
import java.util.Set;

@Epic("Интернет-магазин AutomationTestStore")
@Feature("Корзина")
public class RandomProductsTest extends BaseTest {
    private HomePage homePage;
    private CartPage cartPage;

    @Test
    @Story("Удаление чётных товаров из корзины")
    @Description("Добавление 5 случайных уникальных товаров со случайным количеством, удаление чётных по порядку и проверка уменьшения суммы")
    @Severity(SeverityLevel.NORMAL)
    public void testRandomProductsAndRemoveEven() {
        homePage = new HomePage(driver);
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
        cartPage = new CartPage(driver);
        cartPage.goToCart();

        double totalBefore = cartPage.calculateExpectedTotal();
        cartPage.removeEvenItems();
        double totalAfter = cartPage.calculateExpectedTotal();

        Assert.assertTrue(totalAfter < totalBefore);
    }
}