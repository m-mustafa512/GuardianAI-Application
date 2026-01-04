package com.mustafa.guardianai.ui.parent;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mustafa.guardianai.data.model.QRPairingData;
import com.mustafa.guardianai.databinding.ActivityQrgenerateBinding;
import com.mustafa.guardianai.network.QRPairingService;
import com.mustafa.guardianai.utils.QRCodeGenerator;

/**
 * QR Code Generation Activity
 * Generates and displays QR code for pairing child devices
 */
public class QRGenerateActivity extends AppCompatActivity {
    private ActivityQrgenerateBinding binding;
    private QRPairingService qrPairingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrgenerateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        qrPairingService = new QRPairingService();
        setupUI();
        generateQRCode();
    }

    private void setupUI() {
        // Back button
        binding.btnBack.setOnClickListener(v -> finish());

        // Close button
        binding.btnClose.setOnClickListener(v -> finish());

        // Refresh QR code button
        binding.btnRefresh.setOnClickListener(v -> generateQRCode());
    }

    private void generateQRCode() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to generate QR code", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String parentUid = currentUser.getUid();
        String parentEmail = currentUser.getEmail() != null ? currentUser.getEmail() : "";

        // Show loading
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.ivQRCode.setVisibility(View.GONE);
        binding.tvInstructions.setVisibility(View.GONE);

        // Generate QR pairing data
        qrPairingService.generatePairingQR(parentUid, parentEmail, new QRPairingService.QRPairingCallback() {
            @Override
            public void onSuccess(QRPairingData pairingData) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    
                    // Convert pairing data to JSON string
                    String qrDataJson = pairingData.toJson();
                    android.util.Log.d("QRGenerateActivity", "Generated QR JSON: " + qrDataJson);
                    
                    // Generate QR code bitmap
                    int qrSize = (int) (getResources().getDisplayMetrics().density * 300); // 300dp
                    android.graphics.Bitmap qrBitmap = QRCodeGenerator.generateQRCode(qrDataJson, qrSize, qrSize);
                    
                    if (qrBitmap != null) {
                        binding.ivQRCode.setImageBitmap(qrBitmap);
                        binding.ivQRCode.setVisibility(View.VISIBLE);
                        binding.tvInstructions.setVisibility(View.VISIBLE);
                        
                        // Calculate expiry time remaining
                        long timeRemaining = (pairingData.getExpiresAt() - System.currentTimeMillis()) / 1000 / 60; // minutes
                        binding.tvExpiryTime.setText("Valid for " + timeRemaining + " minutes");
                    } else {
                        Toast.makeText(QRGenerateActivity.this, 
                                "Failed to generate QR code", 
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(Exception exception) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(QRGenerateActivity.this, 
                            "Failed to generate QR code: " + exception.getMessage(), 
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}

