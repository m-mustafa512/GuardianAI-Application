package com.mustafa.guardianai.ui.auth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.mustafa.guardianai.databinding.ActivityQrscanBinding;
import com.mustafa.guardianai.data.model.QRPairingData;
import com.mustafa.guardianai.network.AuthService;
import com.mustafa.guardianai.network.QRPairingService;
import com.mustafa.guardianai.ui.child.ChildDashboardActivity;

/**
 * QR Scan Activity
 * Handles QR code scanning for child device enrollment
 */
public class QRScanActivity extends AppCompatActivity {
    private ActivityQrscanBinding binding;
    private final AuthService authService = new AuthService();
    private final QRPairingService qrPairingService = new QRPairingService();

    private final androidx.activity.result.ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startQRScan();
                } else {
                    Toast.makeText(this,
                            "Camera permission is required for QR scanning",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrscanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupUI();
        checkCameraPermission();
    }

    private void setupUI() {
        binding.btnCancel.setOnClickListener(v -> finish());
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startQRScan();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void startQRScan() {
        // For FYP: QR scanning will be implemented when library is properly configured
        // For now, show a message that QR scanning is being set up
        Toast.makeText(this, 
                "QR scanner is being configured. For now, please use manual pairing.", 
                Toast.LENGTH_LONG).show();
        
        // TODO: Implement QR scanning when zxing-android-embedded is properly configured
        // The library requires additional setup and may need version 4.3.0+
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // QR scanning result handling will be implemented when scanner is set up
    }

    private void handleQRCodeScanned(String qrContent) {
        binding.progressBar.setVisibility(android.view.View.VISIBLE);
        binding.btnCancel.setEnabled(false);

        // Parse QR data
        QRPairingData qrData = QRPairingData.fromJson(qrContent);
        if (qrData == null) {
            binding.progressBar.setVisibility(android.view.View.GONE);
            binding.btnCancel.setEnabled(true);
            Toast.makeText(this, "Invalid QR code format", Toast.LENGTH_LONG).show();
            return;
        }

        // Check if user is logged in (for child, they should be logged in first)
        if (!authService.isAuthenticated()) {
            binding.progressBar.setVisibility(android.view.View.GONE);
            binding.btnCancel.setEnabled(true);
            Toast.makeText(this,
                    "Please login first before pairing",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        qrPairingService.processPairing(qrData, new QRPairingService.SimpleCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(android.view.View.GONE);
                    binding.btnCancel.setEnabled(true);
                    Toast.makeText(QRScanActivity.this,
                            "Device paired successfully!",
                            Toast.LENGTH_LONG).show();
                    // Navigate to child dashboard
                    startActivity(new Intent(QRScanActivity.this, ChildDashboardActivity.class));
                    finish();
                });
            }

            @Override
            public void onFailure(Exception exception) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(android.view.View.GONE);
                    binding.btnCancel.setEnabled(true);
                    Toast.makeText(QRScanActivity.this,
                            "Pairing failed: " + exception.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}

