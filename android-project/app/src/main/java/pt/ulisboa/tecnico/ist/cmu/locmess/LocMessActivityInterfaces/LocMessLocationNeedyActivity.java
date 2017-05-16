package pt.ulisboa.tecnico.ist.cmu.locmess.LocMessActivityInterfaces;

import android.content.Context;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import pt.ulisboa.tecnico.ist.cmu.locmess.BroadcastReceivers.WifiScannerReceiver;
import pt.ulisboa.tecnico.ist.cmu.locmess.Listeners.LocMessLocationListener;
import pt.ulisboa.tecnico.ist.cmu.locmess.LocMessManager;

/**
 * Created by jorge on 11/05/17.
 */

public abstract class LocMessLocationNeedyActivity extends LocMessActivity {

    private LocMessLocationListener _listener;
    private LocationManager _lManager;
    private WifiScannerReceiver _wifiReceiver;
    private WifiManager _wifiManager;
    private static final String TAG="LocMessLocationNeedyActivity";

    @Override
    protected void onCreate(Bundle savedInstances){
        super.onCreate(savedInstances);

        //GPS LOCATION
        _listener=new LocMessLocationListener();
        _lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        _lManager.requestLocationUpdates(_lManager.getBestProvider(buildCriteria(),false), 0, 0, _listener);
        Log.d(TAG,""+_lManager.getLastKnownLocation(_lManager.getBestProvider(buildCriteria(),false)).getLatitude());
        Log.d(TAG,""+_lManager.getLastKnownLocation(_lManager.getBestProvider(buildCriteria(),false)).getLongitude());

        //WIFI SCAN
        _wifiManager =(WifiManager) getSystemService(Context.WIFI_SERVICE);
        _wifiReceiver = new WifiScannerReceiver(_wifiManager);
        registerReceiver(_wifiReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        _wifiManager.startScan();
    }

    @Override
    protected void onPause(){
        super.onPause();
        _lManager.removeUpdates(_listener);
        unregisterReceiver(_wifiReceiver);
    }

    @Override
    protected void onStop(){
        super.onStop();
        _lManager.removeUpdates(_listener);
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        _lManager.removeUpdates(_listener);
    }
    @Override
    protected void onResume(){
        super.onResume();
        _lManager.requestLocationUpdates(_lManager.getBestProvider(buildCriteria(),true), 0, 0, _listener);
        registerReceiver(_wifiReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        _wifiManager.startScan();
    }
    @Override
    protected void onStart(){
        super.onStart();
        _lManager.requestLocationUpdates(_lManager.getBestProvider(buildCriteria(),true), 0, 0, _listener);
        registerReceiver(_wifiReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        _wifiManager.startScan();
    }

    private Criteria buildCriteria(){
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);
        c.setAltitudeRequired(false);
        c.setBearingRequired(false);
        c.setSpeedRequired(false);
        c.setPowerRequirement(Criteria.POWER_HIGH);
        return c;
    }

    protected LocationManager getLocationManager(){
        return _lManager;
    }
    protected LocMessLocationListener getLocationListener(){
        return _listener;
    }

    protected Location getLastLocation(){
        return _lManager.getLastKnownLocation(_lManager.getBestProvider(buildCriteria(),false));
    }

    protected WifiManager getWifiManager(){
        return _wifiManager;
    }

}
