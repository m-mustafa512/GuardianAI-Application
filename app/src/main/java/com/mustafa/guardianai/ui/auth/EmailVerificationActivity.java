package com.mustafa.guardianai.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.mustafa.guardianai.databinding.ActivityEmailVerificationBinding;
import com.mustafa.guardianai.network.AuthService;
import com.mustafa.guardianai.ui.parent.ParentDashboardActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Email Verification Activity
 * Handles 6-digit verification code input
 */
public class EmailVerificationActivity extends AppCompatActivity {
    private ActivityEmailVerificationBinding binding;
    private final AuthService authService = new AuthService();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private Timer resendTimer;
    private int resendSeconds = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmailVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupUI();
        startResendTimer();
    }

    private void setupUI() {
        // Back button
        binding.btnBack.setOnClickListener(v -> finish());

        // Set email display
        com.google.firebase.auth.FirebaseUser user = auth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            String email = user.getEmail();
            // Mask email for privacy
            if (email.length() > 10) {
                String masked = email.substring(0, 7) + "***" + email.substring(email.indexOf("@"));
                binding.tvEmail.setText(masked);
            } else {
                binding.tvEmail.setText(email);
            }
        }

        // Setup code input fields
        setupCodeInputs();

        // Verify Code button
        binding.btnVerifyCode.setOnClickListener(v -> handleVerifyCode());

        // Resend Code link
        binding.tvResendCode.setOnClickListener(v -> resendVerificationCode());
    }

    private void setupCodeInputs() {
        EditText[] codeInputs = {
                binding.etCode1, binding.etCode2, binding.etCode3,
                binding.etCode4, binding.etCode5, binding.etCode6
        };

        for (int i = 0; i < codeInputs.length; i++) {
            final int index = i;
            codeInputs[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < codeInputs.length - 1) {
                        codeInputs[index + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void handleVerifyCode() {
        String code = binding.etCode1.getText().toString() +
                binding.etCode2.getText().toString() +
                binding.etCode3.getText().toString() +
                binding.etCode4.getText().toString() +
                binding.etCode5.getText().toString() +
                binding.etCode6.getText().toString();

        if (code.length() != 6) {
            Toast.makeText(this, "Please enter the complete 6-digit code", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnVerifyCode.setEnabled(false);

        // Reload user to check verification status
        authService.reloadUser(new AuthService.SimpleCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnVerifyCode.setEnabled(true);

                    if (authService.isEmailVerified()) {
                        // Navigate to login success
                        startActivity(new Intent(EmailVerificationActivity.this, LoginSuccessActivity.class));
                        finish();
                    } else {
                        Toast.makeText(EmailVerificationActivity.this,
                                "Verification failed. Please check the code and try again.",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(Exception exception) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnVerifyCode.setEnabled(true);
                    Toast.makeText(EmailVerificationActivity.this,
                            "Verification check failed: " + exception.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void resendVerificationCode() {
        authService.sendEmailVerification(new AuthService.SimpleCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(EmailVerificationActivity.this,
                            "Verification email resent! Please check your inbox.",
                            Toast.LENGTH_LONG).show();
                    startResendTimer();
                });
            }

            @Override
            public void onFailure(Exception exception) {
                runOnUiThread(() -> Toast.makeText(EmailVerificationActivity.this,
                        "Failed to resend: " + exception.getMessage(),
                        Toast.LENGTH_LONG).show());
            }
        });
    }

    private void startResendTimer() {
        if (resendTimer != null) {
            resendTimer.cancel();
        }
        resendSeconds = 60;
        binding.btnResendTimer.setEnabled(false);
        binding.tvResendCode.setVisibility(View.GONE);

        resendTimer = new Timer();
        resendTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    if (resendSeconds > 0) {
                        int minutes = resendSeconds / 60;
                        int seconds = resendSeconds % 60;
                        binding.btnResendTimer.setText(
                                String.format("Resend code in %02d:%02d", minutes, seconds));
                        resendSeconds--;
                    } else {
                        binding.btnResendTimer.setEnabled(true);
                        binding.btnResendTimer.setText("Resend code");
                        binding.tvResendCode.setVisibility(View.VISIBLE);
                        resendTimer.cancel();
                    }
                });
            }
        }, 0, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (resendTimer != null) {
            resendTimer.cancel();
        }
    }
}

