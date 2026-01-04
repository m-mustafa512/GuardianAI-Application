package com.mustafa.guardianai.ui.auth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.mustafa.guardianai.databinding.ActivityQrscanBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private boolean isProcessing = false; // Flag to prevent multiple pairing attempts

    private final ActivityResultLauncher<String> requestPermissionLauncher =
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
                if (result == null || result.getContents() == null || result.getContents().trim().isEmpty()) {
                    // User cancelled or scan failed
                    android.util.Log.d("QRScanActivity", "QR scan cancelled or failed");
                    Toast.makeText(this, "QR scan cancelled or failed. Please try again.", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                if (isProcessing) {
                    android.util.Log.d("QRScanActivity", "Already processing a scan, ignoring new scan");
                    return;
                }

                String scannedContent = result.getContents().trim();
                android.util.Log.d("QRScanActivity", "QR code scanned successfully, length: " + scannedContent.length());
                handleQRCodeScanned(scannedContent);
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
        android.util.Log.d("QRScanActivity", "=== QR Code scanned ===");
        android.util.Log.d("QRScanActivity", "Content length: " + (qrContent != null ? qrContent.length() : 0));
        android.util.Log.d("QRScanActivity", "Content preview: " + (qrContent != null && qrContent.length() > 100 ? qrContent.substring(0, 100) + "..." : qrContent));
        
        isProcessing = true; // Prevent further scans
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnCancel.setEnabled(false);

        // Parse QR data - only pairToken is required
        QRPairingData qrData = QRPairingData.fromJson(qrContent);
        if (qrData == null || qrData.getPairToken() == null || qrData.getPairToken().isEmpty()) {
            android.util.Log.e("QRScanActivity", "Invalid QR code format or missing pairToken");
            android.util.Log.e("QRScanActivity", "QRData is null: " + (qrData == null));
            if (qrData != null) {
                android.util.Log.e("QRScanActivity", "PairToken is null or empty: " + (qrData.getPairToken() == null));
            }
            isProcessing = false; // Allow scanning again
            binding.progressBar.setVisibility(View.GONE);
            binding.btnCancel.setEnabled(true);
            Toast.makeText(this, "Invalid QR code format. Please try scanning again.", Toast.LENGTH_LONG).show();
            return;
        }

        android.util.Log.d("QRScanActivity", "QR data parsed successfully");
        android.util.Log.d("QRScanActivity", "Token: " + qrData.getPairToken());

        // Ensure child device is authenticated using anonymous sign-in
        android.util.Log.d("QRScanActivity", "Ensuring anonymous authentication...");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        
        if (currentUser != null && !currentUser.isAnonymous()) {
            // Sign out non-anonymous user
            android.util.Log.d("QRScanActivity", "Signing out existing non-anonymous user");
            auth.signOut();
            currentUser = null;
        }
        
        if (currentUser == null) {
            // Sign in anonymously
            auth.signInAnonymously()
                    .addOnSuccessListener(authResult -> {
                        android.util.Log.d("QRScanActivity", "Anonymous authentication successful");
                        processPairingAfterAuth(qrData);
                    })
                    .addOnFailureListener(e -> {
                        android.util.Log.e("QRScanActivity", "Anonymous authentication failed: " + e.getMessage(), e);
                        isProcessing = false;
                        binding.progressBar.setVisibility(View.GONE);
                        binding.btnCancel.setEnabled(true);
                        Toast.makeText(QRScanActivity.this, 
                                "Failed to authenticate: " + e.getMessage(), 
                                Toast.LENGTH_LONG).show();
                    });
        } else {
            // Already authenticated anonymously
            android.util.Log.d("QRScanActivity", "Already authenticated anonymously");
            processPairingAfterAuth(qrData);
        }
    }
    
    private void processPairingAfterAuth(QRPairingData qrData) {
        // Process pairing after authentication succeeds
        android.util.Log.d("QRScanActivity", "Starting pairing process...");
        qrPairingService.processPairing(qrData, new QRPairingService.SimpleCallback() {
            @Override
            public void onSuccess() {
                android.util.Log.d("QRScanActivity", "Pairing succeeded! Navigating to child dashboard...");
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
                android.util.Log.e("QRScanActivity", "=== Pairing failed ===");
                android.util.Log.e("QRScanActivity", "Error message: " + (exception != null ? exception.getMessage() : "null"));
                if (exception != null) {
                    android.util.Log.e("QRScanActivity", "Exception class: " + exception.getClass().getName());
                    android.util.Log.e("QRScanActivity", "Stack trace:", exception);
                }
                
                runOnUiThread(() -> {
                    isProcessing = false; // Allow scanning again
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnCancel.setEnabled(true);
                    
                    String errorMessage = "Pairing failed. Please try scanning again.";
                    if (exception != null && exception.getMessage() != null && !exception.getMessage().isEmpty()) {
                        errorMessage = exception.getMessage();
                    }
                    
                    Toast.makeText(QRScanActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    android.util.Log.d("QRScanActivity", "User can try scanning again");
                });
            }
        });
    }
}
