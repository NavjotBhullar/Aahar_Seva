package com.example.aahar100;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class uploadProfilePicActivity extends AppCompatActivity {

    private EditText editTextImageUrl;
    private AppCompatButton buttonPreview, buttonSave;
    private ImageView imageView_Profile_dp;
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private String imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_pic);

        // ✅ Initialize views
        editTextImageUrl = findViewById(R.id.editText_image_url);
        buttonPreview = findViewById(R.id.button_preview);
        buttonSave = findViewById(R.id.upload_pic_btn);
        imageView_Profile_dp = findViewById(R.id.imageView_Profile_dp);
        progressBar = findViewById(R.id.progressBar);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        // ✅ Load existing photo
        loadExistingPhoto();

        // ✅ Preview button - shows image from URL
        buttonPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUrl = editTextImageUrl.getText().toString().trim();

                if (imageUrl.isEmpty()) {
                    Toast.makeText(uploadProfilePicActivity.this,
                            "Please enter image URL", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!imageUrl.startsWith("http")) {
                    Toast.makeText(uploadProfilePicActivity.this,
                            "URL must start with http:// or https://", Toast.LENGTH_SHORT).show();
                    return;
                }

                // ✅ Load preview
                Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_background)
                        .into(imageView_Profile_dp);
            }
        });

        // ✅ Save button - saves URL to database
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUrl = editTextImageUrl.getText().toString().trim();

                if (imageUrl.isEmpty()) {
                    Toast.makeText(uploadProfilePicActivity.this,
                            "Please enter image URL", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!imageUrl.startsWith("http")) {
                    Toast.makeText(uploadProfilePicActivity.this,
                            "URL must start with http:// or https://", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                saveImageUrlToDatabase();
            }
        });
    }

    // ✅ Load existing photo from database
    private void loadExistingPhoto() {
        String userId = firebaseUser.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("Registered Users")
                .child(userId);

        userRef.child("photoUrl").get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                String photoUrl = dataSnapshot.getValue(String.class);
                if (photoUrl != null && !photoUrl.isEmpty()) {
                    editTextImageUrl.setText(photoUrl);
                    Picasso.get()
                            .load(photoUrl)
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .error(R.drawable.ic_launcher_background)
                            .into(imageView_Profile_dp);
                }
            }
        });
    }

    // ✅ Save image URL to Firebase Realtime Database
    private void saveImageUrlToDatabase() {
        String userId = firebaseUser.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("Registered Users")
                .child(userId);

        // ✅ Store URL as text
        userRef.child("photoUrl").setValue(imageUrl)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(uploadProfilePicActivity.this,
                                "Photo URL saved!", Toast.LENGTH_SHORT).show();

                        // Return to profile
                        Intent intent = new Intent(uploadProfilePicActivity.this,
                                UserProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(uploadProfilePicActivity.this,
                                "Failed to save: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}