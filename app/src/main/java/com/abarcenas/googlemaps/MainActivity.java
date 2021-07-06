package com.abarcenas.googlemaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import android.os.PersistableBundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    boolean isPermissionGranter;
    GoogleMap googleMap;
    ImageView imageViewSearch;
    EditText inputLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageViewSearch=findViewById(R.id.iv_search);
        inputLocation=findViewById(R.id.ed_input_location);


        checkPermission();

        if (isPermissionGranter) {
            if (checkGooglePlayServices()) {
                SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
                getSupportFragmentManager().beginTransaction().add(R.id.contaner, supportMapFragment).commit();
                supportMapFragment.getMapAsync(this);

            } else {
                Toast.makeText(this, "Google Playservices Not Available", Toast.LENGTH_SHORT).show();
            }
        }

        imageViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = inputLocation.getText().toString();
                if (location == null)
                {
                    Toast.makeText(MainActivity.this, "Tye any location name", Toast.LENGTH_SHORT).show();
                }else{

                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                    try {
                        List<Address>listAddress = geocoder.getFromLocationName(location, 1);
                    if (listAddress.size()>0)
                    {
                        LatLng latLng = new LatLng(listAddress.get(0).getLatitude(),listAddress.get(0).getLongitude());

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.title("My Search position");
                        markerOptions.position(latLng);
                        googleMap.addMarker(markerOptions);
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
                        googleMap.animateCamera(cameraUpdate);

                    }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }

    private boolean checkGooglePlayServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (result == ConnectionResult.SUCCESS) {
            return true;
        } else if (googleApiAvailability.isUserResolvableError(result)) {
            Dialog dialog = googleApiAvailability.getErrorDialog(this, result, 201, new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    Toast.makeText(MainActivity.this, "User Cancelked Dialoge", Toast.LENGTH_SHORT).show();

                }
            });
            dialog.show();
        }

        return false;
    }

    private void checkPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                isPermissionGranter = true;
                Toast.makeText(MainActivity.this, "Permission Granter", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), "");
                intent.setData(uri);
                startActivity(intent);
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        LatLng latLng = new LatLng(45.26, -104.46);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("My position");
        markerOptions.position(latLng);
        googleMap.addMarker(markerOptions);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
        googleMap.animateCamera(cameraUpdate);

     //   googleMap.getUiSettings().setZoomControlsEnabled(true);
        //  googleMap.getUiSettings().setCompassEnabled(true);
        //  googleMap.getUiSettings().setZoomGesturesEnabled(true);
        //  googleMap.getUiSettings().setScrollGesturesEnabled(true);
        //  googleMap.getUiSettings().setRotateGesturesEnabled(true);
        //  if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
              // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
    // googleMap.setMyLocationEnabled(true);


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.noneMap)
        {
            googleMap.setMapType(googleMap.MAP_TYPE_NONE);
        }
        if (item.getItemId()==R.id.normalMAp)
        {
            googleMap.setMapType(googleMap.MAP_TYPE_NORMAL);
        }
        if (item.getItemId()==R.id.satellite)
        {
            googleMap.setMapType(googleMap.MAP_TYPE_SATELLITE);
        }
        if (item.getItemId()==R.id.MapHyBirdMap)
        {
            googleMap.setMapType(googleMap.MAP_TYPE_HYBRID);
        }
        if (item.getItemId()==R.id.TerrariMap)
        {
            googleMap.setMapType(googleMap.MAP_TYPE_TERRAIN);
        }
        return super.onOptionsItemSelected(item);
    }
 }

