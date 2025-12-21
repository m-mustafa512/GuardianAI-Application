package com.mustafa.guardianai.utils;

import android.content.Context;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

/**
 * Biometric authentication helper
 * Handles fingerprint and face ID authentication
 */
public class BiometricHelper {
    private final Context context;

    public BiometricHelper(Context context) {
        this.context = context;
    }

    /**
     * Check if biometric authentication is available
     */
    public boolean isBiometricAvailable() {
        BiometricManager biometricManager = BiometricManager.from(context);
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                == BiometricManager.BIOMETRIC_SUCCESS;
    }

    /**
     * Get biometric availability status
     */
    public BiometricStatus getBiometricStatus() {
        BiometricManager biometricManager = BiometricManager.from(context);
        int status = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);
        
        switch (status) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                return BiometricStatus.AVAILABLE;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                return BiometricStatus.NO_HARDWARE;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                return BiometricStatus.HARDWARE_UNAVAILABLE;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                return BiometricStatus.NOT_ENROLLED;
            default:
                return BiometricStatus.UNAVAILABLE;
        }
    }

    /**
     * Show biometric prompt for authentication
     */
    public void showBiometricPrompt(
            FragmentActivity activity,
            String title,
            String subtitle,
            BiometricCallback callback
    ) {
        if (title == null) title = "Biometric Authentication";
        if (subtitle == null) subtitle = "Use your fingerprint or face to unlock";

        BiometricPrompt.AuthenticationCallback authCallback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                callback.onSuccess();
            }

            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                String error;
                if (errorCode == BiometricPrompt.ERROR_USER_CANCELED || 
                    errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    error = "Authentication canceled";
                } else {
                    error = "Authentication error: " + errString;
                }
                callback.onError(error);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                callback.onError("Authentication failed. Please try again.");
            }
        };

        BiometricPrompt biometricPrompt = new BiometricPrompt(
                activity,
                ContextCompat.getMainExecutor(context),
                authCallback
        );

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setNegativeButtonText("Cancel")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    public interface BiometricCallback {
        void onSuccess();
        void onError(String error);
    }

    public enum BiometricStatus {
        AVAILABLE,
        NO_HARDWARE,
        HARDWARE_UNAVAILABLE,
        NOT_ENROLLED,
        UNAVAILABLE
    }
}


