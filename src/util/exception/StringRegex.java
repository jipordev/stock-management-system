package util.exception;

public class StringRegex {
    public static boolean validateString(String input, String regexPattern) {
        return input.matches(regexPattern);
    }
}
