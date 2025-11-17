package com.example.aahar100;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.example.aahar100.databinding.ActivityReceiveBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Receive extends FragmentActivity {

    private MapView mMap;
    private ActivityReceiveBinding binding;
    private FirebaseAuth authProfile;
    private ChildEventListener childEventListener;
    private Map<String, Marker> markerMap;
    private Map<String, DonationData> donationDataMap;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private Marker userMarker;
    private GeoPoint userLocation;

    // Data class to store donation info
    private static class DonationData {
        String donorName;
        String phoneNumber;
        String foodItem;
        double lat;
        double lng;
        String key;

        DonationData(String name, String phone, String food, double latitude, double longitude, String key) {
            this.donorName = name;
            this.phoneNumber = phone;
            this.foodItem = food;
            this.lat = latitude;
            this.lng = longitude;
            this.key = key;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE));

        binding = ActivityReceiveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        mMap = binding.map;
        setupMap();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        authProfile = FirebaseAuth.getInstance();
        markerMap = new HashMap<>();
        donationDataMap = new HashMap<>();

        loadDonations();
        checkLocationPermission();
    }

    private void setupMap() {
        mMap.setTileSource(TileSourceFactory.MAPNIK);
        mMap.setMultiTouchControls(true);
        mMap.getController().setZoom(15.0);
        mMap.getZoomController().setVisibility(
                org.osmdroid.views.CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT
        );
    }

    private void loadDonations() {
        DatabaseReference foodMapRef = FirebaseDatabase.getInstance().getReference("FoodMap");

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                try {
                    double lat = dataSnapshot.child("lat").getValue(Double.class);
                    double lng = dataSnapshot.child("lng").getValue(Double.class);
                    String donorName = dataSnapshot.child("name").getValue(String.class);
                    String phoneNumber = dataSnapshot.child("number").getValue(String.class);
                    String foodItem = dataSnapshot.child("food").getValue(String.class);
                    String key = dataSnapshot.getKey();

                    DonationData donation = new DonationData(donorName, phoneNumber, foodItem, lat, lng, key);
                    donationDataMap.put(key, donation);

                    GeoPoint position = new GeoPoint(lat, lng);
                    Marker marker = new Marker(mMap);
                    marker.setPosition(position);
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    marker.setIcon(getDrawable(R.drawable.marker_donator_style));

                    String title = "🍽️ " + foodItem + "\n👤 " + donorName;
                    marker.setTitle(title);

                    marker.setOnMarkerClickListener((clickedMarker, mapView) -> {
                        showDonationDetails(donation);
                        return true;
                    });

                    mMap.getOverlays().add(marker);
                    markerMap.put(key, marker);
                    mMap.invalidate();

                } catch (Exception e) {
                    Toast.makeText(Receive.this, "Error loading donation", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String childKey = dataSnapshot.getKey();
                Marker marker = markerMap.get(childKey);
                if (marker != null) {
                    mMap.getOverlays().remove(marker);
                    markerMap.remove(childKey);
                    donationDataMap.remove(childKey);
                    mMap.invalidate();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Receive.this, "Failed to load donations", Toast.LENGTH_SHORT).show();
            }
        };

        foodMapRef.addChildEventListener(childEventListener);
    }

    // ✅ NO XML NEEDED - Pure code dialog
    private void showDonationDetails(DonationData donation) {
        String distanceText = "Calculating...";
        if (userLocation != null) {
            float distance = calculateDistance(
                    userLocation.getLatitude(), userLocation.getLongitude(),
                    donation.lat, donation.lng
            );
            distanceText = String.format(Locale.getDefault(), "%.2f km away", distance);
        }

        String message = "🍽️ Food: " + donation.foodItem + "\n\n" +
                "👤 Donor: " + donation.donorName + "\n\n" +
                "📍 Distance: " + distanceText + "\n\n";

        if (!donation.phoneNumber.equals("not a number flag")) {
            message += "📞 Contact: " + donation.phoneNumber;
        } else {
            message += "📞 Contact: Not Available";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Donation Details");
        builder.setMessage(message);

        if (!donation.phoneNumber.equals("not a number flag")) {
            builder.setPositiveButton("📞 Call", (dialog, which) -> {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + donation.phoneNumber));
                startActivity(callIntent);
            });
        }

        builder.setNeutralButton("🧭 Navigate", (dialog, which) -> {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + donation.lat + "," + donation.lng);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Uri uri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination="
                        + donation.lat + "," + donation.lng);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(browserIntent);
            }
        });

        builder.setNegativeButton("Close", null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            if (dialog.getButton(AlertDialog.BUTTON_POSITIVE) != null) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(getResources().getColor(R.color.light_blue));
            }
            if (dialog.getButton(AlertDialog.BUTTON_NEUTRAL) != null) {
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                        .setTextColor(getResources().getColor(R.color.light_blue));
            }
            if (dialog.getButton(AlertDialog.BUTTON_NEGATIVE) != null) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(getResources().getColor(R.color.light_blue));
            }
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.button_bg1);
            }
        });

        dialog.show();
    }

    private float calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0] / 1000;
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getUserLocation();
        }
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            userLocation = new GeoPoint(location.getLatitude(), location.getLongitude());

                            if (userMarker != null) {
                                mMap.getOverlays().remove(userMarker);
                            }

                            userMarker = new Marker(mMap);
                            userMarker.setPosition(userLocation);
                            userMarker.setTitle("📍 Your Location");
                            userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                            userMarker.setIcon(getDrawable(R.drawable.framereceiver_two));
                            mMap.getOverlays().add(userMarker);

                            mMap.getController().setZoom(15.0);
                            mMap.getController().animateTo(userLocation);
                            mMap.invalidate();
                        } else {
                            Toast.makeText(Receive.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMap.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMap.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseReference foodMapRef = FirebaseDatabase.getInstance().getReference("FoodMap");
        if (childEventListener != null) {
            foodMapRef.removeEventListener(childEventListener);
        }
    }
}