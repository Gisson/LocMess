package pt.ulisboa.tecnico.ist.cmu.locmess.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import pt.ulisboa.tecnico.ist.cmu.locmess.LocMessManager;
import pt.ulisboa.tecnico.ist.cmu.locmess.R;
import pt.ulisboa.tecnico.ist.cmu.locmess.commands.GetLocationCommand;
import pt.ulisboa.tecnico.ist.cmu.locmess.commands.GetLocationsAroundCommand;
import pt.ulisboa.tecnico.ist.cmu.locmess.commands.ListKeysCommand;
import pt.ulisboa.tecnico.ist.cmu.locmess.dto.LocationDto;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.CommandNotExecutedException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.NoLocationException;
import pt.ulisboa.tecnico.ist.cmu.locmess.wifiDirect.WifiDirectMessageDto;

/**
 * Created by jorge on 10/05/17.
 */

public class GpsService extends Service {

    private static final String TAG = "GpsService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;

    private class LocationListener implements android.location.LocationListener{
        Location mLastLocation;
        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
            handleNewLocations(mLastLocation);

        }
        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            handleNewLocations(mLastLocation);
        }
        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }
        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }
    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };
    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }
    @Override
    public void onCreate()
    {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }
    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }
    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().
                    getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public void handleNewLocations(final Location l){
        final GetLocationCommand auxComm =
                new GetLocationCommand(LocMessManager.getInstance().getToken(),
                        ""+l.getLatitude(),""+l.getLongitude());
        LocMessManager.getInstance().executeAsync(auxComm, new LocMessManager.CompleteCallback() {

            @Override
            public void OnPreExecute(){}
            @Override
            public void OnComplete(boolean result, String message) {
                if (result) {
                    try {
                        LocationDto foundLocation = auxComm.getFullResult();
                        for(WifiDirectMessageDto muleMessage : LocMessManager.getInstance().
                                getAllFromLocation(foundLocation.getName())){
                            LocMessManager.getInstance().getWifiHandler().sendMessage(muleMessage);
                        }
                        LocMessManager.getInstance().pushLocation(foundLocation);
                        final GetLocationsAroundCommand getLocations =
                                new GetLocationsAroundCommand(foundLocation.getName());
                        LocMessManager.getInstance().executeAsync(
                                getLocations,new LocMessManager.CompleteCallback(){
                                    @Override
                                    public void OnPreExecute(){}
                                    @Override
                                    public void OnComplete(boolean result, String message) {
                                        LocMessManager.getInstance().setAroundLocations(
                                                getLocations.getResults());
                                    }
                                });
                    } catch (CommandNotExecutedException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NoLocationException e) {
                        final GetLocationsAroundCommand getLocations =
                                new GetLocationsAroundCommand(""+l.getLatitude(),
                                        ""+l.getLongitude(),"10000" );
                        LocMessManager.getInstance().executeAsync(
                                getLocations,new LocMessManager.CompleteCallback(){
                                    @Override
                                    public void OnPreExecute(){}
                                    @Override
                                    public void OnComplete(boolean result, String message) {
                                        LocMessManager.getInstance().setAroundLocations(
                                                getLocations.getResults());
                                    }
                                });
                    }
                }
            }
        });
    }


}
