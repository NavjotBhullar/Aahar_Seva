package com.example.aahar100;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class about extends AppCompatActivity {

    private static final String TAG = "AboutActivity";

    CardView cardInstagram, cardFacebook, cardTwitter, cardVersion;
    TextView tvVersionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        cardInstagram = findViewById(R.id.instagram);
        cardFacebook = findViewById(R.id.facebook);
        cardTwitter = findViewById(R.id.twitter);
        cardVersion = findViewById(R.id.version);
        tvVersionText = findViewById(R.id.tvVersionText);

        // 1) Instagram
        cardInstagram.setOnClickListener(v -> {
            String igUrl = getString(R.string.instagram_url);
            String igUser = getString(R.string.instagram_user); // username without @
            Intent intent;
            try {
                // prefer opening in Instagram app if available
                getPackageManager().getPackageInfo("com.instagram.android", 0);
                Uri uri = Uri.parse("http://instagram.com/_u/" + igUser);
                intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.instagram.android");
            } catch (PackageManager.NameNotFoundException e) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(igUrl)); // browser fallback
            }
            safeStartActivity(intent);
        });

        // 2) Facebook
        cardFacebook.setOnClickListener(v -> {
            String fbUrl = getString(R.string.facebook_url);
            Intent intent;
            try {
                getPackageManager().getPackageInfo("com.facebook.katana", 0);
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fbUrl));
                intent.setPackage("com.facebook.katana");
            } catch (PackageManager.NameNotFoundException e) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fbUrl));
            }
            safeStartActivity(intent);
        });

        // 3) Twitter
        cardTwitter.setOnClickListener(v -> {
            String twUrl = getString(R.string.twitter_url);
            String twHandle = getString(R.string.twitter_handle); // without @
            Intent intent;
            try {
                getPackageManager().getPackageInfo("com.twitter.android", 0);
                Uri uri = Uri.parse("twitter://user?screen_name=" + twHandle);
                intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.twitter.android");
            } catch (PackageManager.NameNotFoundException e) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twUrl));
            }
            safeStartActivity(intent);
        });

        // 4) Version card - display versionName and optionally show a dialog on click
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            tvVersionText.setText("v " + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Version name not found", e);
            tvVersionText.setText(getString(R.string.Version));
        }

        cardVersion.setOnClickListener(v -> {
            try {
                String version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                new AlertDialog.Builder(this)
                        .setTitle("App version")
                        .setMessage("Version: " + version)
                        .setPositiveButton("OK", null)
                        .show();
            } catch (PackageManager.NameNotFoundException ignored) {
                Toast.makeText(this, "Version info not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Start activity with ActivityNotFoundException handling */
    private void safeStartActivity(Intent intent) {
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Log.w(TAG, "Activity not found to handle intent: " + intent, ex);
            Toast.makeText(this, "No application can handle this action", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Log.e(TAG, "Unexpected error launching intent", ex);
            Toast.makeText(this, "Unable to open link", Toast.LENGTH_SHORT).show();
        }
    }
}
