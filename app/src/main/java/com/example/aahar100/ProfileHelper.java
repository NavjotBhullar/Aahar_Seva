package com.example.aahar100;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileHelper {

    /**
     * ✅ Loads user profile picture from Firebase Realtime Database
     * @param imageView The ImageView to load image into
     * @param defaultImage Resource ID for default image
     */
    public static void loadProfilePicture(ImageView imageView, int defaultImage) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("Registered Users")
                .child(userId);

        userRef.child("photoUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String photoUrl = snapshot.getValue(String.class);
                    if (photoUrl != null && !photoUrl.isEmpty()) {
                        // ✅ Load image from URL
                        Picasso.get()
                                .load(photoUrl)
                                .placeholder(defaultImage)
                                .error(defaultImage)
                                .into(imageView);
                    } else {
                        // No URL, show default
                        imageView.setImageResource(defaultImage);
                    }
                } else {
                    // Field doesn't exist, show default
                    imageView.setImageResource(defaultImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // On error, show default
                imageView.setImageResource(defaultImage);
            }
        });
    }
}