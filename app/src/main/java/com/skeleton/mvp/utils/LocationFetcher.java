package com.skeleton.mvp.utils;

import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import androidx.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.skeleton.mvp.activity.ChatActivity;
import com.skeleton.mvp.util.Log;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class LocationFetcher {
    // location last updated time
    private String mLastUpdateTime;

    // location update interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 500;

    // fastest update interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 500;

    private static final int REQUEST_CHECK_SETTINGS = 100;

    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private static final String TAG = LocationFetcher.class.getSimpleName();
    private ChatActivity chatActivity;
    private GoogleApiClient googleApiClient;
    private int count = 0;
    private boolean isMatched = false;

    @SuppressLint("RestrictedApi")
    public void init(ChatActivity chatActivity) {
        this.chatActivity = chatActivity;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(chatActivity);
        mSettingsClient = LocationServices.getSettingsClient(chatActivity);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                if (mCurrentLocation != null) {
                    Calendar locationTime = Calendar.getInstance();
                    Calendar currentTime = Calendar.getInstance();
                    currentTime.setTimeInMillis(System.currentTimeMillis());
                    locationTime.setTimeInMillis(mCurrentLocation.getTime());

                    int mCurrentHour = currentTime.get(Calendar.HOUR);
                    int mCurrentMinute = currentTime.get(Calendar.MINUTE);
                    int mCurrentDay = currentTime.get(Calendar.DAY_OF_MONTH);


                    int mLocationHour = locationTime.get(Calendar.HOUR);
                    int mLocationMinute = locationTime.get(Calendar.MINUTE);
                    int mLocationDay = locationTime.get(Calendar.DAY_OF_MONTH);

                    count += 1;
                    if (mCurrentDay == mLocationDay && (mCurrentHour + ":" + mCurrentMinute).equals(mLocationHour + ":" + mLocationMinute) && !isMatched) {
                        Log.e("Locationfetcher", "Matched");
                        chatActivity.OnLocationFetchingComplete(mCurrentLocation);
                        isMatched = true;
                    } else if (count >= 5 && !isMatched) {
                        chatActivity.OnLocationFetchingComplete(mCurrentLocation);
                        Log.e("Locationfetcher", "Not Matched");
                    }
                    if (count >= 5) {
                        isMatched = false;
                    }
                }

            }
        };

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setNumUpdates(5);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
        googleApiClient = new GoogleApiClient.Builder(chatActivity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(onConnectionFailedListener).build();
        googleApiClient.connect();
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(result1 -> {
            android.util.Log.e("result", "=" + result1.getStatus());
            final Status status = result1.getStatus();
            final LocationSettingsStates state = result1.getLocationSettingsStates();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    startLocationUpdates();
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(chatActivity, 1006);
                    } catch (IntentSender.SendIntentException e) {
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    break;
            }
            googleApiClient.unregisterConnectionCallbacks(connectionCallbacks);
            googleApiClient.unregisterConnectionFailedListener(onConnectionFailedListener);
            googleApiClient = null;
        });
    }

    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(chatActivity, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        Toast.makeText(chatActivity, "Fetching your location!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
                        if (mCurrentLocation != null) {
                            chatActivity.OnLocationFetchingComplete(mCurrentLocation);
                        }

                    }
                })
                .addOnFailureListener(chatActivity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(chatActivity, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(chatActivity, errorMessage, Toast.LENGTH_LONG).show();
                        }


                    }
                });
    }

    public interface GetCurrentLocation {
        void OnLocationFetchingComplete(Location location);
    }

    private static GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {

        }

        @Override
        public void onConnectionSuspended(int i) {

        }
    };
    private static GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener = connectionResult -> {

    };
}
