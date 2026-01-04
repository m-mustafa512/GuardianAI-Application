package com.mustafa.guardianai.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.mustafa.guardianai.databinding.ActivityChildLoginInfoBinding;

/**
 * Child Login Info Activity
 * Explains that child login is done via QR code scanning
 */
public class ChildLoginInfoActivity extends AppCompatActivity {
    private ActivityChildLoginInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChildLoginInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupUI();
    }

    private void setupUI() {
        // Back button
        binding.btnBack.setOnClickListener(v -> finish());

        // Scan QR Code button - opens camera for QR scanning
        binding.btnScanQR.setOnClickListener(v -> {
            Intent intent = new Intent(this, QRScanActivity.class);
            startActivity(intent);
            finish();
        });
    }
}










