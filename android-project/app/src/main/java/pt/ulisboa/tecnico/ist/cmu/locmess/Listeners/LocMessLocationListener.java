package pt.ulisboa.tecnico.ist.cmu.locmess.Listeners;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by jorge on 11/05/17.
 */

public class LocMessLocationListener implements LocationListener {

    private final static String TAG="LocMessLocationListener";

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG,"Location changed");
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
