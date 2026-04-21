package tests;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CategoryPage;

@Epic("Интернет-магазин AutomationTestStore")
@Feature("Каталог и сортировка")
public class SortingTest extends BaseTest {

    private CategoryPage categoryPage;

    @Test
    @Story("Сортировка товаров в категории")
    @Description("Проверка сортировки по имени и цене в обоих направлениях в категории Shoes")
    @Severity(SeverityLevel.NORMAL)
    public void testSortingByNameAndPrice() {
        categoryPage = new CategoryPage(driver);
        categoryPage.openCategory("https://automationteststore.com/index.php?rt=product/category&path=68");

        categoryPage.selectSortBy("Name A - Z");
        Assert.assertTrue(categoryPage.isSortedByNameAZ(), "Сортировка по имени A-Z не работает");

        categoryPage.selectSortBy("Name Z - A");
        Assert.assertTrue(categoryPage.isSortedByNameZA(), "Сортировка по имени Z-A не работает");

        categoryPage.selectSortBy("Price Low > High");
        Assert.assertTrue(categoryPage.isSortedByPriceLowHigh(), "Сортировка по цене Low > High не работает");

        categoryPage.selectSortBy("Price High > Low");
        Assert.assertTrue(categoryPage.isSortedByPriceHighLow(), "Сортировка по цене High > Low не работает");
    }
}