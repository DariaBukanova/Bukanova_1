package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class HomePage extends BasePage {
    private final Random random = new Random();

    @FindBy(css = "a.prdocutname")
    private List<WebElement> productNames;

    public HomePage(WebDriver driver) {
        super(driver);
    }

    @Step("Открытие главной страницы")
    public void open() {
        driver.get("https://automationteststore.com/");
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("a.prdocutname")));
    }

    @Step("Добавление случайного товара в корзину")
    public String addRandomProductToCart(Set<String> alreadyAdded) {
        List<String> allProductNames = new ArrayList<>();
        for (WebElement product : productNames) {
            allProductNames.add(product.getText());
        }

        List<String> availableProducts = new ArrayList<>();
        for (String name : allProductNames) {
            if (!alreadyAdded.contains(name)) {
                availableProducts.add(name);
            }
        }

        if (availableProducts.isEmpty()) {
            return null;
        }

        String productName = availableProducts.get(random.nextInt(availableProducts.size()));
        int quantity = random.nextInt(5) + 1;

        for (WebElement product : productNames) {
            if (product.getText().equals(productName)) {
                helper.scrollAndWaitForClickable(product);
                product.click();
                break;
            }
        }

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("product_quantity")));

        WebElement quantityField = driver.findElement(By.id("product_quantity"));
        quantityField.clear();
        quantityField.sendKeys(String.valueOf(quantity));

        WebElement addButton = driver.findElement(By.cssSelector("a.cart"));
        addButton.click();

        return productName;
    }
}