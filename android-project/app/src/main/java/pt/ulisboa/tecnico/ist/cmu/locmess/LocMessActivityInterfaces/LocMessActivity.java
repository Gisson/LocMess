package pt.ulisboa.tecnico.ist.cmu.locmess.LocMessActivityInterfaces;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.ulisboa.tecnico.ist.cmu.locmess.LocMessManager;
import pt.ulisboa.tecnico.ist.cmu.locmess.wifiDirect.SimWifiP2pBroadcastReceiver;
import pt.ulisboa.tecnico.ist.cmu.locmess.wifiDirect.WifiDirectMessageDto;
import pt.ulisboa.tecnico.ist.cmu.locmess.wifiDirect.WifiP2PHandler;

/**
 * Created by jorge on 11/05/17.
 */

public abstract class LocMessActivity extends AppCompatActivity {

    private final static String TAG = "LocMessActivity";




    @Override
    protected void onCreate(Bundle savedInstances){
        super.onCreate(savedInstances);
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
    @Override
    protected void onResume(){
        super.onResume();
    }
    @Override
    protected void onStart(){
        super.onStart();
    }



}
