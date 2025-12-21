package com.mustafa.guardianai.utils;

import java.util.regex.Pattern;

/**
 * Email validation utility
 * Validates email format using regex
 */
public class EmailValidator {
    // RFC 5322 compliant email regex (simplified version)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"
    );

    /**
     * Validate email format
     */
    public static PasswordValidator.ValidationResult validate(String email) {
        if (email == null || email.trim().isEmpty()) {
            return new PasswordValidator.ValidationResult(
                    false,
                    "Email cannot be empty"
            );
        }

        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return new PasswordValidator.ValidationResult(
                    false,
                    "Please enter a valid email address"
            );
        }

        return new PasswordValidator.ValidationResult(true);
    }
}


