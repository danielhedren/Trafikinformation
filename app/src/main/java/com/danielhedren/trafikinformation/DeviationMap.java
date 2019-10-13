package com.danielhedren.trafikinformation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class DeviationMap extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private LatLng target;
    private Location location;
    private TextView detailsText;
    private ArrayList<DeviationData> deviations = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deviation_map);

        // Initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        detailsText = findViewById(R.id.detailsText);

        Bundle extras = getIntent().getExtras();

        if (extras.containsKey("deviations")) {
            deviations = (ArrayList<DeviationData>)extras.getSerializable("deviations");
            detailsText.setVisibility(View.GONE);

            target = new LatLng(deviations.get(0).latitude, deviations.get(0).longitude);
        } else {
            target = new LatLng(extras.getDouble("latitude"), extras.getDouble("longitude"));

            String title = extras.getString("messageType");
            if (extras.getString("roadNumber") != null) title += " - " + extras.getString("roadNumber");
            getSupportActionBar().setTitle(title);

            detailsText.setText(extras.getString("message") + extras.get("locationDescriptor") + ". " + extras.get("severityText") + ".");
        }

        if (map == null) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        BitmapDescriptor icon_severe = BitmapDescriptorFactory.fromResource(R.drawable.ic_warning_severe);
        BitmapDescriptor icon_high = BitmapDescriptorFactory.fromResource(R.drawable.ic_warning_high);
        BitmapDescriptor icon_med = BitmapDescriptorFactory.fromResource(R.drawable.ic_warning_med);
        BitmapDescriptor icon_low = BitmapDescriptorFactory.fromResource(R.drawable.ic_warning_low);

        if (deviations != null) {
            for (DeviationData d : deviations) {
                BitmapDescriptor icon = null;
                if (d.severity == 5) icon = icon_severe;
                else if (d.severity == 4) icon = icon_high;
                else if (d.severity == 2) icon = icon_med;
                else if (d.severity == 1) icon = icon_low;

                map.addMarker(new MarkerOptions()
                        .position(new LatLng(d.latitude, d.longitude))
                        .title(d.severityText + " - " + d.locationDescriptor)
                        .snippet(d.message)
                        .icon(icon));
            }

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 10));
        } else {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 13));

            map.addMarker(new MarkerOptions().position(target));
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        }
    }
}
