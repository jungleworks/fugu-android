package com.skeleton.mvp.util

import java.util.regex.Pattern

class FormatStringUtil {
    object FormatString {
        fun getFormattedString(message: String?): ArrayList<String> {
            var messageString=message

            if (messageString==null){
                messageString=""
            }
            val boldRegex = "(\\B\\*)(.*?)(\\* )"
            val italicRegex = "(\\b\\_)(.*?)(\\_ )"
            val boldItalicRegex = "(\\*\\_)(.*?)(\\_\\* )"
            val italicBoldRegex = "(\\_\\*)(.*?)(\\*\\_ )"
            var string = messageString!!.replace("&nbsp;", " ")
            string = string.replace("  <span class=\"new-text>\"", " ")
            string = string.replace("</span>", " ")
            string = "$string "
            var formattedString = "$messageString "
            string = string.replace("\n", " <br>")
            formattedString = formattedString.replace("\n", " <br>")
            string = string.replace("<br>", " <br>")
            formattedString = formattedString.replace("<br>", " <br>")

            val boldItalicPattern = Pattern.compile(boldItalicRegex, Pattern.MULTILINE)
            val boldItalicMatcher = boldItalicPattern.matcher(string)

            while (boldItalicMatcher.find()) {
                if (boldItalicMatcher.group(2).trim().isNotEmpty() && boldItalicMatcher.group(2).trim().equals(boldItalicMatcher.group(2))) {
                    string = string.replace(boldItalicMatcher.group(0), "<b><i>" + boldItalicMatcher.group(2) + "</b></i> ")
                    formattedString = formattedString.replace(boldItalicMatcher.group(0), "" + boldItalicMatcher.group(2) + " ")
                }
            }

            val italicBoldPattern = Pattern.compile(italicBoldRegex, Pattern.MULTILINE)
            val italicBoldMatcher = italicBoldPattern.matcher(string)
            while (italicBoldMatcher.find()) {
                if (italicBoldMatcher.group(2).trim().isNotEmpty() && italicBoldMatcher.group(2).trim().equals(italicBoldMatcher.group(2))) {
                    string = string.replace(italicBoldMatcher.group(0), "<i><b>" + italicBoldMatcher.group(2) + "</i></b> ")
                    formattedString = formattedString.replace(italicBoldMatcher.group(0), "" + italicBoldMatcher.group(2) + " ")
                }
            }
            val boldPattern = Pattern.compile(boldRegex, Pattern.MULTILINE)
            val boldMatcher = boldPattern.matcher(string)
            while (boldMatcher.find()) {
                if (boldMatcher.group(2).trim().isNotEmpty() && boldMatcher.group(2).trim().equals(boldMatcher.group(2))) {
                    string = string.replace(boldMatcher.group(0), "<b>" + boldMatcher.group(2) + "</b> ")
                    formattedString = formattedString.replace(boldMatcher.group(0), "" + boldMatcher.group(2) + " ")
                }
            }

            val italicPattern = Pattern.compile(italicRegex, Pattern.MULTILINE)
            val italicMatcher = italicPattern.matcher(string)
            while (italicMatcher.find()) {
                if (italicMatcher.group(2).trim().isNotEmpty() && italicMatcher.group(2).trim().equals(italicMatcher.group(2))) {
                    string = string.replace(italicMatcher.group(0), "<i>" + italicMatcher.group(2) + "</i> ")
                    formattedString = formattedString.replace(italicMatcher.group(0), "" + italicMatcher.group(2) + " ")
                }
            }
            val formattedStrings = ArrayList<String>()
            formattedStrings.add(string.trim())
            formattedStrings.add(formattedString.trim())
            return formattedStrings
        }
    }
}