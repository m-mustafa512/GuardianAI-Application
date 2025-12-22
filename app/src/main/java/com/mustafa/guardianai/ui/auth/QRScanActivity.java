package com.mustafa.guardianai.ui.auth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import androidx.activity.result.ActivityResultLauncher;
import com.mustafa.guardianai.databinding.ActivityQrscanBinding;
import com.mustafa.guardianai.data.model.QRPairingData;
import com.mustafa.guardianai.network.QRPairingService;
import com.mustafa.guardianai.ui.child.ChildDashboardActivity;

/**
 * QR Scan Activity
 * Handles QR code scanning for child device enrollment
 * Automatically creates child account when QR is scanned
 */
public class QRScanActivity extends AppCompatActivity {
    private ActivityQrscanBinding binding;
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

    // QR Scanner launcher using ScanContract
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result == null || result.getContents() == null) {
                    // User cancelled
                    Toast.makeText(this, "QR scan cancelled", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    handleQRCodeScanned(result.getContents());
                }
            }
    );

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
        // Use ZXing ScanContract to launch QR scanner
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan the QR code from parent device");
        options.setCameraId(0); // Use back camera
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(false);
        options.setOrientationLocked(false);
        barcodeLauncher.launch(options);
    }

    private void handleQRCodeScanned(String qrContent) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnCancel.setEnabled(false);

        // Parse QR data
        QRPairingData qrData = QRPairingData.fromJson(qrContent);
        if (qrData == null) {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnCancel.setEnabled(true);
            Toast.makeText(this, "Invalid QR code format", Toast.LENGTH_LONG).show();
            return;
        }

        // Process pairing - this will automatically create child account
        qrPairingService.processPairing(qrData, new QRPairingService.SimpleCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnCancel.setEnabled(true);
                    Toast.makeText(QRScanActivity.this,
                            "Child account created and paired successfully!",
                            Toast.LENGTH_LONG).show();
                    // Navigate to child dashboard
                    Intent intent = new Intent(QRScanActivity.this, ChildDashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onFailure(Exception exception) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnCancel.setEnabled(true);
                    Toast.makeText(QRScanActivity.this,
                            "Pairing failed: " + exception.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}

