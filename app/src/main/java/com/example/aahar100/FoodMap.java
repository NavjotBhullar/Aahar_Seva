package com.example.aahar100;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.aahar100.databinding.ActivityFoodMapBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// ✅ OSMDroid Imports (replacing Google Maps)
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

import java.util.HashMap;
import java.util.Map;

public class FoodMap extends FragmentActivity {

    private MapView mMap; // ✅ Changed from GoogleMap to MapView
    private ActivityFoodMapBinding binding;
    private FirebaseAuth authProfile;
    private ChildEventListener childEventListener;
    private Map<String, Marker> markerMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Initialize OSMDroid configuration BEFORE setContentView
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE));

        binding = ActivityFoodMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Full screen map
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        // ✅ Initialize MapView (get reference from binding)
        mMap = binding.map; // or findViewById(R.id.map)
        setupMap();

        authProfile = FirebaseAuth.getInstance();
        markerMap = new HashMap<>();

        DatabaseReference foodMapRef = FirebaseDatabase.getInstance().getReference("FoodMap");
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                double a = dataSnapshot.child("lat").getValue(double.class);
                double b = dataSnapshot.child("lng").getValue(double.class);
                String c = dataSnapshot.child("name").getValue(String.class);
                String d = dataSnapshot.child("food").getValue(String.class);

                // ✅ Changed from LatLng to GeoPoint
                GeoPoint userLocation = new GeoPoint(a, b);

                // ✅ OSMDroid Marker creation
                Marker marker = new Marker(mMap);
                marker.setPosition(userLocation);
                marker.setTitle(c + "||" + d);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                marker.setIcon(getDrawable(R.drawable.marker_donator_style));
                mMap.getOverlays().add(marker);

                String childKey = dataSnapshot.getKey();
                markerMap.put(childKey, marker);

                mMap.invalidate(); // ✅ Refresh map
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                Toast.makeText(FoodMap.this, "T1", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String childKey = dataSnapshot.getKey();
                Marker marker = markerMap.get(childKey);
                if (marker != null) {
                    mMap.getOverlays().remove(marker); // ✅ Remove from overlays
                    markerMap.remove(childKey);
                    mMap.invalidate();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                Toast.makeText(FoodMap.this, "T3", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FoodMap.this, "T4", Toast.LENGTH_SHORT).show();
            }
        };

        foodMapRef.addChildEventListener(childEventListener);
    }

    // ✅ Setup OSMDroid Map
    private void setupMap() {
        mMap.setTileSource(TileSourceFactory.MAPNIK); // Free OpenStreetMap tiles
        mMap.setMultiTouchControls(true); // Enable zoom gestures
        mMap.getController().setZoom(5.0); // Initial zoom

        // ✅ Center on India
        GeoPoint indiaCenter = new GeoPoint(20.5937, 78.9629);
        mMap.getController().setCenter(indiaCenter);

        // Enable zoom controls
        mMap.getZoomController().setVisibility(
                org.osmdroid.views.CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMap.onResume(); // ✅ Important for OSMDroid
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMap.onPause(); // ✅ Important for OSMDroid
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseReference foodMapRef = FirebaseDatabase.getInstance().getReference("FoodMap");
        foodMapRef.removeEventListener(childEventListener);
    }
}