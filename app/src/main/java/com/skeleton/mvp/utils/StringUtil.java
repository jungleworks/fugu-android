package com.skeleton.mvp.utils;

/**
 * Developer: Saurabh Verma
 * Dated: 03-03-2017.
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
        char firstChar = inputString.charAt(0);
        char firstCharToUpperCase = Character.toUpperCase(firstChar);
        result = result + firstCharToUpperCase;
        for (int i = 1; i < inputString.length(); i++) {
            char currentChar = inputString.charAt(i);
            char previousChar = inputString.charAt(i - 1);
            if (previousChar == ' ') {
                char currentCharToUpperCase = Character.toUpperCase(currentChar);
                result = result + currentCharToUpperCase;
            } else {
                char currentCharToLowerCase = Character.toLowerCase(currentChar);
                result = result + currentCharToLowerCase;
            }
        }
        return result;
    }

}
