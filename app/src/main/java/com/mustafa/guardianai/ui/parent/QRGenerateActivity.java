package com.mustafa.guardianai.ui.parent;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
        binding.imgQRCode.setVisibility(View.GONE);
        binding.tvInstructions.setVisibility(View.GONE);

        // Generate QR pairing data
        qrPairingService.generatePairingQR(parentUid, parentEmail, new QRPairingService.QRPairingCallback() {
            @Override
            public void onSuccess(QRPairingData pairingData) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    
                    // Convert pairing data to JSON string
                    String qrDataJson = pairingData.toJson();
                    
                    // Generate QR code bitmap


                   // int qrSizePx = 900; // fixed large size for reliable scanning
                    //android.graphics.Bitmap qrBitmap = QRCodeGenerator.generateQRCode(qrPayload, qrSizePx, qrSizePx);

                    //ImageView imgQrCode = findViewById(R.id.imgQrCode);
                    ImageView imgQrCode = binding.imgQRCode;

                    imgQrCode.post(() -> {

                        int width = imgQrCode.getWidth();
                        int height = imgQrCode.getHeight();

                        // ✅ Fallback for real devices
                        if (width <= 0 || height <= 0) {
                            int fallbackDp = 260; // safe QR size
                            float density = getResources().getDisplayMetrics().density;
                            width = height = (int) (fallbackDp * density);
                        }
                        String qrPayload = "PAIR:" + pairingData.getPairToken();


                        Bitmap qrBitmap = QRCodeGenerator.generateQRCode(
                                qrPayload,
                                width,
                                height
                        );

                        imgQrCode.setImageBitmap(qrBitmap);

                        if (qrBitmap != null) {
                            binding.imgQRCode.setImageBitmap(qrBitmap);
                            binding.imgQRCode.setVisibility(View.VISIBLE);
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

