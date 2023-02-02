package com.qr.pos.amper.utils

import java.util.regex.Pattern

object CustomPatterns {
    val HAS_UPPER_CASE = Pattern.compile("(?=.*?[A-Z])")
    val HAS_LOWER_CASE = Pattern.compile("(?=.*?[a-z])")
    val HAS_DIGITS = Pattern.compile("(?=.*?[0-9])")
    val HAS_SPECIAL_CHAR = Pattern.compile("(?=.*?[#?!@\$%^&*-])")
}