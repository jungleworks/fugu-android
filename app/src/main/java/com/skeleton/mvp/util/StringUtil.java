package com.skeleton.mvp.util;


/**
 * Generic reusable methods for string manipulation
 */
public final class StringUtil {

    /**
     * Empty Constructor
     * not called
     */
    private StringUtil() {
    }

    /**
     * Method to convert string into camel case string
     *
     * @param inputString string value that need to convert into camel case
     * @return converted camel cased string
     */
    public static String toCamelCase(final String inputString) {
        String result = "";
        if (inputString == null || inputString.isEmpty()) {
            return result;
        }
        final char firstChar = inputString.charAt(0);
        final char firstCharToUpperCase = Character.toUpperCase(firstChar);
        result = result + firstCharToUpperCase;
        for (int i = 1; i < inputString.length(); i++) {
            final char currentChar = inputString.charAt(i);
            final char previousChar = inputString.charAt(i - 1);
            if (previousChar == ' ') {
                final char currentCharToUpperCase = Character.toUpperCase(currentChar);
                result = result + currentCharToUpperCase;
            } else {
                final char currentCharToLowerCase = Character.toLowerCase(currentChar);
                result = result + currentCharToLowerCase;
            }
        }
        return result;
    }

}
