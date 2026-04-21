package tests;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.HomePage;
import pages.SearchPage;
import utils.PriceParser;

@Epic("Интернет-магазин AutomationTestStore")
@Feature("Корзина и поиск")
public class SearchAndCartTest extends BaseTest {

    private SearchPage searchPage;
    private CartPage cartPage;
    private HomePage homePage;

    @Test
    @Story("Поиск товара и добавление в корзину")
    @Description("Проверка поиска 'shirt', добавления 2-го и 3-го товара со случайным количеством, удвоения самого дешёвого товара и итоговой суммы")
    @Severity(SeverityLevel.CRITICAL)
    public void testSearchAddToCartAndVerifyTotal() {
        homePage = new HomePage(driver);
        searchPage = new SearchPage(driver);
        cartPage = new CartPage(driver);

        homePage.open();
        searchPage.search("shirt");
        searchPage.selectSortBy("Name A - Z");
        searchPage.addToCart(2, searchPage.getRandomQuantity());

        homePage.open();
        searchPage.search("shirt");
        searchPage.selectSortBy("Name A - Z");
        searchPage.addToCart(3, searchPage.getRandomQuantity());

        cartPage.goToCart();
        cartPage.doubleCheapestItemQuantity();

        double expectedTotal = cartPage.calculateExpectedTotal();
        double actualTotal = cartPage.getTotalAmount();

        Assert.assertEquals(actualTotal, expectedTotal, 0.01, "Фактическая сумма не соответствует ожидаемой");
    }
}