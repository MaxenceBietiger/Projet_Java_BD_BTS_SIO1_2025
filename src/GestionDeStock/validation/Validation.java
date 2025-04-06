package GestionDeStock.validation;

public class Validation {
    public static boolean isValidString(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean isValidNumber(double number) {
        return number > 0;
    }

    public static boolean isValidQuantity(int quantity) {
        return quantity >= 0;
    }

    public static boolean isValidDate(String date) {
        try {
            java.sql.Date.valueOf(date);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}