package com.futech.coffeeshop.ui.address;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.futech.coffeeshop.R;
import com.futech.coffeeshop.obj.address.AddressData;
import com.futech.coffeeshop.utils.RegisterControl;
import com.futech.coffeeshop.utils.local_database.AddressLocalDatabase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class AddAddressActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final int LOCATION_REQUEST_CODE = 55;
    private MarkerOptions marker;
    private boolean selectLocation = false;
    private SlidingUpPanelLayout sliding;
    private GoogleMap googleMap;
    private boolean firstTime = true;

    private EditText editAddress;
    private EditText addressName;
    private Button addBtn;

    private Location lastLocation;

    private float selectedLat;
    private float selectedLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        sliding = findViewById(R.id.sliding_up);
        FloatingActionButton backBtn = findViewById(R.id.back_btn);
        addBtn = findViewById(R.id.add_btn);
        editAddress = findViewById(R.id.edit_address);
        addressName = findViewById(R.id.address_name_field);

        final FloatingActionButton locationBtn = findViewById(R.id.location_btn);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        backBtn.setOnClickListener(v -> finish());

        locationBtn.setOnClickListener(v -> goToLocation(lastLocation));

        addBtn.setOnClickListener(v -> addAddress());
    }

    private void addAddress() {
        AddressData addressData = new AddressData();
        if (getIntent().getExtras() != null) {
            addressData = (AddressData) getIntent().getExtras().getSerializable(getApplicationContext().getString(R.string.address_bundle_key));
            if (addressData == null) addressData = new AddressData();
        }
        addressData.setAddress(editAddress.getText().toString().trim());
        addressData.setName(addressName.getText().toString().trim());
        addressData.setUid(RegisterControl.getCurrentUserUid(this));
        addressData.setLat(selectedLat);
        addressData.setLng(selectedLng);

        AddressLocalDatabase db = new AddressLocalDatabase(this);
        if (addressData.getId() == -1)
            db.insert(addressData, new AddressLocalDatabase.AddressListener() {
                @Override
                public void onChange(AddressData addressData) {
                    Toast.makeText(AddAddressActivity.this, R.string.address_added, Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                }

                @Override
                public void onError(String msg) {
                    Toast.makeText(AddAddressActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            });
        else {
            db.update(addressData.getId(), addressData, new AddressLocalDatabase.AddressListener() {
                @Override
                public void onChange(AddressData addressData) {
                    Toast.makeText(AddAddressActivity.this, R.string.address_edited, Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                }

                @Override
                public void onError(String msg) {
                    Toast.makeText(AddAddressActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        locationSetMarker();
        googleMap.setOnMapLongClickListener(this::goToLocation);

        Bundle bundle;
        if ((bundle = getIntent().getExtras()) == null || getIntent().getExtras().getSerializable("address") == null)
            return;
        AddressData addressData = (AddressData) bundle.getSerializable("address");
        if (addressData == null) return;
        goToLocation(new LatLng(addressData.getLat(), addressData.getLng()));
        selectLocation = true;
        editAddress.setText(addressData.getAddress());
        addressName.setText(addressData.getName());
        addBtn.setText(R.string.edit_address_btn);
    }

    private void goToLocation(Location location) {
        LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        goToLocation(userLatLng);
    }

    private void goToLocation(LatLng latLng) {
        selectedLng = (float) latLng.longitude;
        selectedLat = (float) latLng.latitude;
        googleMap.clear();
        marker = new MarkerOptions().position(latLng);
        googleMap.addMarker(marker);
        selectLocation = true;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        sliding.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
    }

    private void locationSetMarker() {
        if (googleMap == null) return;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                lastLocation = location;
                if (!selectLocation) {
                    LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    marker = new MarkerOptions().position(userLatLng);
                    googleMap.addMarker(marker);
                    selectedLng = (float) userLatLng.longitude;
                    selectedLat = (float) userLatLng.latitude;
                    if (firstTime) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));
                        firstTime = false;
                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationSetMarker();
            }
        }
    }

}
