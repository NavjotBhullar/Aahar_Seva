package com.example.aahar100;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {
    private TextView textViewFullName, textViewEmail, textViewMobile;
    private String fullName, email, mobile;
    private ImageView imageView;
    private FirebaseAuth authProfile;
    private SwipeRefreshLayout swipeContainer;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        swipeToRefresh();
        textViewFullName = findViewById(R.id.textView_show_full_name);
        textViewEmail = findViewById(R.id.textView_show_email);
        textViewMobile = findViewById(R.id.textView_show_mobile);
        imageView = findViewById(R.id.imageView_Profile_dp);
        progressBar = findViewById(R.id.progressBar);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(UserProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }

        imageView.setOnClickListener(view -> {
            Intent intent = new Intent(UserProfileActivity.this, uploadProfilePicActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
    }

    private void swipeToRefresh() {
        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(() -> {
            showUserProfile(authProfile.getCurrentUser());
            swipeContainer.setRefreshing(false);
        });

        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
    }

    // ✅ Migrate data from Auth to Database (runs once per user)
    private void migrateUserData(FirebaseUser firebaseUser, String userID) {
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("Registered Users")
                .child(userID);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Add fullName if missing
                if (!snapshot.hasChild("fullName") && firebaseUser.getDisplayName() != null) {
                    userRef.child("fullName").setValue(firebaseUser.getDisplayName());
                }

                // Add email if missing
                if (!snapshot.hasChild("email") && firebaseUser.getEmail() != null) {
                    userRef.child("email").setValue(firebaseUser.getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        // ✅ Run migration first
        migrateUserData(firebaseUser, userID);

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance()
                .getReference("Registered Users");

        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null) {
                    // ✅ Try to get from database first, fallback to Auth
                    fullName = snapshot.hasChild("fullName") ?
                            snapshot.child("fullName").getValue(String.class) :
                            firebaseUser.getDisplayName();

                    email = snapshot.hasChild("email") ?
                            snapshot.child("email").getValue(String.class) :
                            firebaseUser.getEmail();

                    mobile = readUserDetails.mobile;

                    textViewFullName.setText(fullName != null ? fullName : "Not set");
                    textViewEmail.setText(email != null ? email : "Not set");
                    textViewMobile.setText(mobile != null ? mobile : "Not set");

                    // ✅ Load profile picture
                    ProfileHelper.loadProfilePicture(imageView, R.drawable.ic_launcher_foreground);

                } else {
                    Toast.makeText(UserProfileActivity.this, "Something went wrong!",
                            Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Something went wrong!",
                        Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        showUserProfile(authProfile.getCurrentUser());
    }
}