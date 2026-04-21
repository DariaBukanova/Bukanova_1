package tests;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.HomePage;

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
        cartPage = new CartPage(driver);

        homePage.addMultipleRandomProductsToCart(5);

        homePage.open();
        homePage.refresh();

        cartPage.goToCart();

        double totalBefore = cartPage.calculateExpectedTotal();
        cartPage.removeEvenItems();
        double totalAfter = cartPage.calculateExpectedTotal();

        Assert.assertTrue(totalAfter < totalBefore, "Сумма после удаления чётных товаров должна быть меньше");
    }
}