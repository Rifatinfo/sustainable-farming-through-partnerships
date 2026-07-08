package service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ValidationService {

    private ValidationService() {}

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    public static boolean isValidEmail(String email) {
        return email != null && email.matches(EMAIL_REGEX);
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isPositiveMoney(double amount) {
        return amount > 0;
    }

    public static boolean isNotNegativeMoney(double amount) {
        return amount >= 0;
    }

    public static boolean isValidPercentage(double percentage) {
        return percentage >= 0 && percentage <= 100;
    }

    public static boolean isValidDateRange(String startDate, String endDate) {
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate start = LocalDate.parse(startDate, fmt);
            LocalDate end = LocalDate.parse(endDate, fmt);
            return !end.isBefore(start);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isValidDate(String date) {
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(date, fmt);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isValidInvestmentAmount(double amount, double remaining) {
        return amount > 0 && amount <= remaining;
    }
}
