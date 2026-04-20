package utils;

public class PriceParser {
    private PriceParser() {}

    public static double parsePrice(String text) {
        return Double.parseDouble(text.replace("$", "").replace(",", "").trim());
    }

    public static double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}