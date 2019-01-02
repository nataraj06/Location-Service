package com.android.locationservices;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.android.locationservices.helper.LocationHelper;
import com.google.android.gms.common.api.ResolvableApiException;

import static com.android.locationservices.helper.LocationHelper.REQUEST_CODE_RESOLVABLE_API;

public class MainActivity extends AppCompatActivity implements LocationHelper.OnLocationCompleteListener {

    private TextView txtLatitude;
    private TextView txtLongitude;

    private LocationHelper locationHelper;

    private final int REQUEST_CODE_ASK_PERMISSIONS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtLatitude = findViewById(R.id.txt_latitude);
        txtLongitude = findViewById(R.id.txt_longitude);
        setUpLocationServices();
    }


    //Method used for checking permissions and initializing location service
    private void setUpLocationServices() {
        int hasGetLocationPermission = ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasGetLocationPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            initializeLocationHelper();
        }
    }

    private void initializeLocationHelper() {
        locationHelper = new LocationHelper(this, this);
        locationHelper.startLocationUpdates();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_RESOLVABLE_API) {
            locationHelper.onActivityResult(requestCode, resultCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != REQUEST_CODE_ASK_PERMISSIONS) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializeLocationHelper();
        }
    }

    @Override
    public void getLocationUpdate(Location location) {
        txtLatitude.setText("Latitude: " + location.getLatitude());
        txtLongitude.setText("Longitude: " + location.getLongitude());
        //it is always good to stop location updates once job
        locationHelper.stopLocationUpdates();
    }

    @Override
    public void onError(ResolvableApiException resolvableApiException, String error) {
        try {
            // Show the dialog by calling startResolutionForResult(),
            // and check the result in onActivityResult().
            resolvableApiException.startResolutionForResult(
                    this,
                    REQUEST_CODE_RESOLVABLE_API);
        } catch (IntentSender.SendIntentException e) {
            // Ignore the error.
        }
    }

    @Override
    public void onResolvableApiResponseFailure() {

    }
}
