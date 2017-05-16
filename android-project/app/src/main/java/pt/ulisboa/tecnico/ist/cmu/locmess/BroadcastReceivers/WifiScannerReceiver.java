package pt.ulisboa.tecnico.ist.cmu.locmess.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.CursorJoiner;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.List;

/**
 * Created by jorge on 11/05/17.
 */

public class WifiScannerReceiver extends BroadcastReceiver {

    private WifiManager _wifiManager;
    private List<ScanResult> _scanResults;

    public WifiScannerReceiver(WifiManager wifiManager){
        _wifiManager=wifiManager;
    }

    @Override
    public void onReceive(Context c, Intent intent) {
        if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
            _scanResults = _wifiManager.getScanResults();
        }
    }

    public List<ScanResult> getScanResults() {
        return _scanResults;
    }
}
