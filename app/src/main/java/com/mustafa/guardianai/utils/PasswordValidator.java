package com.mustafa.guardianai.utils;

import java.util.regex.Pattern;

/**
 * Password validation utility
 * Enforces strong password requirements using regex
 */
public class PasswordValidator {
    // Password requirements:
    // - At least 8 characters
    // - At least one uppercase letter
    // - At least one lowercase letter
    // - At least one digit
    // - At least one special character (!@#$%^&*()_+-=[]{}|;:,.<>?)
    private static final int MIN_LENGTH = 8;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?]).{8,}$"
    );

    /**
     * Validate password strength
     * @param password Password to validate
     * @return ValidationResult with success status and error message if invalid
     */
    public static ValidationResult validate(String password) {
        if (password == null || password.length() < MIN_LENGTH) {
            return new ValidationResult(
                    false,
                    "Password must be at least " + MIN_LENGTH + " characters long"
            );
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return new ValidationResult(
                    false,
                    "Password must contain:\n" +
                            "• At least one uppercase letter\n" +
                            "• At least one lowercase letter\n" +
                            "• At least one digit\n" +
                            "• At least one special character (!@#$%^&*()_+-=[]{}|;:,.<>?)"
            );
        }

        return new ValidationResult(true);
    }

    /**
     * Get password strength indicator
     */
    public static PasswordStrength getPasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            return PasswordStrength.WEAK;
        }
        if (password.length() < 8 || !PASSWORD_PATTERN.matcher(password).matches()) {
            return PasswordStrength.MEDIUM;
        }
        if (password.length() >= 12 && PASSWORD_PATTERN.matcher(password).matches()) {
            return PasswordStrength.STRONG;
        }
        return PasswordStrength.MEDIUM;
    }

    public static class ValidationResult {
        private final boolean isValid;
        private final String errorMessage;

        public ValidationResult(boolean isValid) {
            this(isValid, null);
        }

        public ValidationResult(boolean isValid, String errorMessage) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() {
            return isValid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    public enum PasswordStrength {
        WEAK,
        MEDIUM,
        STRONG
    }
}


