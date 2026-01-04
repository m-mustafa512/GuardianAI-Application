package com.mustafa.guardianai.ui.parent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import com.mustafa.guardianai.R;
import com.mustafa.guardianai.databinding.FragmentSettingsBinding;
import com.mustafa.guardianai.network.AuthService;
import com.mustafa.guardianai.ui.auth.LoginActivity;
import com.mustafa.guardianai.ui.base.BaseFragment;
import com.mustafa.guardianai.utils.BiometricHelper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Settings Fragment
 * Profile management with full backend functionality
 * Includes profile picture upload
 * Uses BaseFragment from Shared Foundation
 */
public class SettingsFragment extends BaseFragment {
    private FragmentSettingsBinding binding;
    private final AuthService authService = new AuthService();
    private static final String PROFILE_PICTURE_KEY = "parent_profile_picture";
    
    // Use an array to hold the URI so it can be effectively final for lambda
    private final Uri[] cameraImageUriHolder = new Uri[1];

    // Activity result launcher for image picker
    private final ActivityResultLauncher<String> imagePickerLauncher = 
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    loadImageFromUri(uri);
                }
            });

    // Activity result launcher for camera
    private final ActivityResultLauncher<Uri> cameraLauncher = 
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && cameraImageUriHolder[0] != null) {
                    loadImageFromUri(cameraImageUriHolder[0]);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUserProfile();
        loadProfilePicture();
    }

    @Override
    protected void setupUI() {
        if (!isFragmentAttached()) return;
        // Change profile picture button
        binding.btnChangeProfilePicture.setOnClickListener(v -> showImageSourceDialog());
        
        // Profile picture click also opens image picker
        binding.ivProfilePicture.setOnClickListener(v -> showImageSourceDialog());
        
        // Edit profile - make name/email clickable to edit
        binding.tvUserName.setOnClickListener(v -> showEditProfileDialog());
        binding.tvUserEmail.setOnClickListener(v -> showEditProfileDialog());
        
        // Manage children
        binding.btnManageChildren.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ChildProfileListActivity.class);
            startActivity(intent);
        });
        
        // Change Password
        binding.btnChangePassword.setOnClickListener(v -> {
            showToast("Change password - coming soon");
        });

        // Biometric Login switch
        binding.switchBiometric.setChecked(BiometricHelper.isBiometricEnabled(requireContext()));
        binding.switchBiometric.setOnCheckedChangeListener((buttonView, isChecked) -> {
            BiometricHelper.setBiometricEnabled(requireContext(), isChecked);
            showToast(isChecked ? "Biometric login enabled" : "Biometric login disabled");
        });

        // Dark Mode switch (placeholder)
        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showToast("Dark mode - coming soon");
        });

        // Notifications switch (placeholder)
        binding.switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showToast(isChecked ? "Notifications enabled" : "Notifications disabled");
        });

        // Help Center
        binding.btnHelpCenter.setOnClickListener(v -> {
            showToast("Help Center - coming soon");
        });

        // Privacy Policy
        binding.btnPrivacyPolicy.setOnClickListener(v -> {
            showToast("Privacy Policy - coming soon");
        });

        // Logout
        binding.btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Logout", (dialog, which) -> {
                        authService.logout();
                        Intent intent = new Intent(requireContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        requireActivity().finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void showImageSourceDialog() {
        String[] options = {"Camera", "Gallery", "Remove Picture"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Change Profile Picture")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // Camera
                        openCamera();
                    } else if (which == 1) {
                        // Gallery
                        imagePickerLauncher.launch("image/*");
                    } else if (which == 2) {
                        // Remove picture
                        removeProfilePicture();
                    }
                })
                .show();
    }

    private void openCamera() {
        try {
            // Create a temporary file for the camera image
            java.io.File imageFile = new java.io.File(requireContext().getCacheDir(), "profile_picture_temp.jpg");
            // Ensure parent directory exists
            imageFile.getParentFile().mkdirs();
            cameraImageUriHolder[0] = androidx.core.content.FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".fileprovider",
                    imageFile
            );
            cameraLauncher.launch(cameraImageUriHolder[0]);
        } catch (Exception e) {
            showError("Failed to open camera: " + e.getMessage());
        }
    }

    private void loadImageFromUri(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (inputStream != null) {
                inputStream.close();
            }

            if (bitmap != null) {
                // Resize bitmap to reduce memory usage
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
                
                // Save to SharedPreferences as base64 (simple approach for FYP)
                saveProfilePicture(resizedBitmap);
                
                // Display the image
                binding.ivProfilePicture.setImageBitmap(resizedBitmap);
                
                showToast("Profile picture updated");
            }
        } catch (IOException e) {
            showError("Failed to load image: " + e.getMessage());
        }
    }

    private void saveProfilePicture(Bitmap bitmap) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] imageBytes = baos.toByteArray();
            String base64Image = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);
            
            android.content.SharedPreferences prefs = requireContext().getSharedPreferences("GuardianAI", android.content.Context.MODE_PRIVATE);
            prefs.edit().putString(PROFILE_PICTURE_KEY, base64Image).apply();
        } catch (Exception e) {
            showError("Failed to save picture: " + e.getMessage());
        }
    }

    private void loadProfilePicture() {
        try {
            android.content.SharedPreferences prefs = requireContext().getSharedPreferences("GuardianAI", android.content.Context.MODE_PRIVATE);
            String base64Image = prefs.getString(PROFILE_PICTURE_KEY, null);
            
            if (base64Image != null && !base64Image.isEmpty()) {
                byte[] imageBytes = android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                if (bitmap != null) {
                    binding.ivProfilePicture.setImageBitmap(bitmap);
                }
            }
        } catch (Exception e) {
            // If loading fails, use default image
        }
    }

    private void removeProfilePicture() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Remove Profile Picture")
                .setMessage("Are you sure you want to remove your profile picture?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    android.content.SharedPreferences prefs = requireContext().getSharedPreferences("GuardianAI", android.content.Context.MODE_PRIVATE);
                    prefs.edit().remove(PROFILE_PICTURE_KEY).apply();
                    binding.ivProfilePicture.setImageResource(android.R.drawable.ic_menu_gallery);
                    showToast("Profile picture removed");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadUserProfile() {
        var user = authService.getCurrentUser();
        if (user != null) {
            if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
                binding.tvUserName.setText(user.getDisplayName());
            } else {
                binding.tvUserName.setText("Parent User");
            }
            
            if (user.getEmail() != null) {
                binding.tvUserEmail.setText(user.getEmail());
            }
        }
    }

    private void showEditProfileDialog() {
        android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        TextInputEditText etName = dialogView.findViewById(R.id.etName);
        TextInputEditText etEmail = dialogView.findViewById(R.id.etEmail);
        
        var user = authService.getCurrentUser();
        if (user != null) {
            if (user.getDisplayName() != null) {
                etName.setText(user.getDisplayName());
            }
            if (user.getEmail() != null) {
                etEmail.setText(user.getEmail());
                etEmail.setEnabled(false);
            }
        }
        
        new AlertDialog.Builder(requireContext())
                .setTitle("Edit Profile")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = etName.getText() != null ? etName.getText().toString().trim() : "";
                    if (!name.isEmpty()) {
                        updateUserProfile(name);
                    } else {
                        showToast("Name cannot be empty");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateUserProfile(String name) {
        var user = authService.getCurrentUser();
        if (user != null) {
            com.google.firebase.auth.UserProfileChangeRequest profileUpdates =
                    new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();
            
            user.updateProfile(profileUpdates)
                    .addOnSuccessListener(aVoid -> {
                        showToast("Profile updated successfully");
                        loadUserProfile();
                    })
                    .addOnFailureListener(e -> {
                        showError("Failed to update profile: " + e.getMessage());
                    });
        }
    }
}
