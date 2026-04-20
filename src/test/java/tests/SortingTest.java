package tests;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CategoryPage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        List<String> namesAZ = categoryPage.getProductNamesList();
        List<String> sortedNamesAZ = new ArrayList<>(namesAZ);
        Collections.sort(sortedNamesAZ);
        Assert.assertEquals(namesAZ, sortedNamesAZ);

        categoryPage.selectSortBy("Name Z - A");
        List<String> namesZA = categoryPage.getProductNamesList();
        List<String> sortedNamesZA = new ArrayList<>(namesZA);
        sortedNamesZA.sort(Collections.reverseOrder());
        Assert.assertEquals(namesZA, sortedNamesZA);

        categoryPage.selectSortBy("Price Low > High");
        List<Double> pricesLowHigh = categoryPage.getProductPricesList();
        List<Double> sortedPricesLowHigh = new ArrayList<>(pricesLowHigh);
        Collections.sort(sortedPricesLowHigh);
        Assert.assertEquals(pricesLowHigh, sortedPricesLowHigh);

        categoryPage.selectSortBy("Price High > Low");
        List<Double> pricesHighLow = categoryPage.getProductPricesList();
        List<Double> sortedPricesHighLow = new ArrayList<>(pricesHighLow);
        sortedPricesHighLow.sort(Collections.reverseOrder());
        Assert.assertEquals(pricesHighLow, sortedPricesHighLow);
    }
}