package utils;

public class PriceParser {

    public static double parsePrice(String priceText) {
        if (priceText == null || priceText.isEmpty()) {
            return 0.0;
        }
        String cleaned = priceText.replaceAll("[^0-9.]", "");
        if (cleaned.isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public static double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}