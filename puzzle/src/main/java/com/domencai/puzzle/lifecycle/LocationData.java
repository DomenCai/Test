package com.domencai.puzzle.lifecycle;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.MainThread;

/**
 * Created by Domen、on 2017/9/5.
 */

public class LocationData extends LiveData<Location> {

    private static LocationData sInstance;
    private LocationManager locationManager;

    @MainThread
    public static LocationData get(Context context) {
        if (sInstance == null) {
            sInstance = new LocationData(context.getApplicationContext());
        }
        return sInstance;
    }

    private LocationListener mListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            setValue(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    private LocationData(Context context) {
        locationManager = (LocationManager) context.getSystemService(
                Context.LOCATION_SERVICE);
    }

    @Override
    protected void onActive() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mListener);
    }

    @Override
    protected void onInactive() {
        locationManager.removeUpdates(mListener);
    }
}
