package com.mustafa.guardianai.ui.parent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.mustafa.guardianai.databinding.ActivityParentDashboardBinding;
import com.mustafa.guardianai.network.AuthService;
import com.mustafa.guardianai.network.QRPairingService;
import com.mustafa.guardianai.ui.auth.LoginActivity;
import com.mustafa.guardianai.utils.BiometricHelper;
import com.mustafa.guardianai.utils.QRCodeGenerator;

/**
 * Parent Dashboard Activity
 * Main dashboard for parent users with biometric protection
 */
public class ParentDashboardActivity extends AppCompatActivity {
    private ActivityParentDashboardBinding binding;
    private final AuthService authService = new AuthService();
    private BiometricHelper biometricHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityParentDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        biometricHelper = new BiometricHelper(this);
        setupUI();
        checkBiometricAndAuthenticate();
    }

    private void setupUI() {
        // Get current user info
        var user = authService.getCurrentUser();
        binding.tvWelcome.setText("Welcome, " + (user != null && user.getEmail() != null ? user.getEmail() : "Parent"));

        // Logout button
        binding.btnLogout.setOnClickListener(v -> {
            authService.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // Generate QR for pairing
        binding.btnGenerateQR.setOnClickListener(v -> generatePairingQR());
    }

    private void checkBiometricAndAuthenticate() {
        BiometricHelper.BiometricStatus status = biometricHelper.getBiometricStatus();
        switch (status) {
            case AVAILABLE:
                // Show biometric prompt
                biometricHelper.showBiometricPrompt(
                        this,
                        "Unlock Guardian AI",
                        "Use your fingerprint or face to access the dashboard",
                        new BiometricHelper.BiometricCallback() {
                            @Override
                            public void onSuccess() {
                                runOnUiThread(() -> Toast.makeText(ParentDashboardActivity.this,
                                        "Authentication successful",
                                        Toast.LENGTH_SHORT).show());
                            }

                            @Override
                            public void onError(String error) {
                                runOnUiThread(() -> Toast.makeText(ParentDashboardActivity.this,
                                        error,
                                        Toast.LENGTH_LONG).show());
                            }
                        }
                );
                break;
            case NOT_ENROLLED:
                Toast.makeText(this,
                        "No biometric enrolled. Please set up fingerprint or face unlock in device settings.",
                        Toast.LENGTH_LONG).show();
                break;
            default:
                // Biometric not available, allow access anyway for FYP
                Toast.makeText(this,
                        "Biometric authentication not available on this device",
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void generatePairingQR() {
        var user = authService.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(android.view.View.VISIBLE);
        binding.btnGenerateQR.setEnabled(false);

        QRPairingService qrPairingService = new QRPairingService();
        qrPairingService.generatePairingQR(
                user.getUid(),
                user.getEmail() != null ? user.getEmail() : "",
                new QRPairingService.QRPairingCallback() {
                    @Override
                    public void onSuccess(com.mustafa.guardianai.data.model.QRPairingData qrData) {
                        runOnUiThread(() -> {
                            binding.progressBar.setVisibility(android.view.View.GONE);
                            binding.btnGenerateQR.setEnabled(true);
                            
                            // Generate QR code bitmap
                            String qrJson = qrData.toJson();
                            android.graphics.Bitmap qrBitmap = QRCodeGenerator.generateQRCode(qrJson, 400, 400);
                            
                            if (qrBitmap != null) {
                                // Display QR code image
                                binding.ivQRCode.setImageBitmap(qrBitmap);
                                binding.ivQRCode.setVisibility(android.view.View.VISIBLE);
                                binding.tvQRCodeText.setVisibility(android.view.View.GONE);
                            } else {
                                // Fallback: show JSON text if bitmap generation fails
                                binding.ivQRCode.setVisibility(android.view.View.GONE);
                                binding.tvQRCodeText.setText("QR Code Data:\n" + qrJson);
                                binding.tvQRCodeText.setVisibility(android.view.View.VISIBLE);
                            }
                            
                            Toast.makeText(ParentDashboardActivity.this,
                                    "QR code generated. Show this to child device to scan.",
                                    Toast.LENGTH_LONG).show();
                        });
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        runOnUiThread(() -> {
                            binding.progressBar.setVisibility(android.view.View.GONE);
                            binding.btnGenerateQR.setEnabled(true);
                            Toast.makeText(ParentDashboardActivity.this,
                                    "Failed to generate QR: " + exception.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        });
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Re-authenticate with biometric when returning to app
        if (biometricHelper.isBiometricAvailable()) {
            checkBiometricAndAuthenticate();
        }
    }
}

