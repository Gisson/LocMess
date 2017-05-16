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

/**
 * Created by jorge on 11/05/17.
 */

public abstract class LocMessActivity extends AppCompatActivity {

    private final static String TAG = "LocMessActivity";

    private SimWifiP2pBroadcastReceiver _receiver;
    private IntentFilter _filter;
    private SimWifiP2pManager.Channel mChannel = null;
    private Messenger mService = null;
    private boolean mBound = false;
    private SimWifiP2pManager mManager = null;
    private SimWifiP2pSocketServer mSrvSocket = null;
    private SimWifiP2pSocket mCliSocket = null;
    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mManager = new SimWifiP2pManager(mService);
            mChannel = mManager.initialize(getApplication(), getMainLooper(), null);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
            mManager = null;
            mChannel = null;
            mBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstances){
        super.onCreate(savedInstances);
        SimWifiP2pSocketManager.Init(getApplicationContext());
        _filter = new IntentFilter();
        _filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        _filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        _filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        _filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        _receiver = new SimWifiP2pBroadcastReceiver(this);
        registerReceiver(_receiver,_filter);
        bindService(new Intent(getApplicationContext(), SimWifiP2pService.class),mConnection, Context.BIND_AUTO_CREATE);
        mBound=true;


        new IncommingCommTask().executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR);

        new OutgoingCommTask().executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR,"192.168.0.3");

//        new SendCommTask().executeOnExecutor(
//                AsyncTask.THREAD_POOL_EXECUTOR,"SENDING STUFF!!");
    }

    @Override
    protected void onPause(){
        super.onPause();
        unregisterReceiver(_receiver);
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
        registerReceiver(_receiver,_filter);
    }
    @Override
    protected void onStart(){
        super.onStart();
    }


    public class IncommingCommTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Log.d(TAG, "IncommingCommTask started (" + this.hashCode() + ").");

            try {
                mSrvSocket = new SimWifiP2pSocketServer(10001);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    SimWifiP2pSocket sock = mSrvSocket.accept();
                    try {
                        BufferedReader sockIn = new BufferedReader(
                                new InputStreamReader(sock.getInputStream()));
                        String st = sockIn.readLine();
                        publishProgress(st);
                        sock.getOutputStream().write(("\n").getBytes());
                    } catch (IOException e) {
                        Log.d("Error reading socket:", e.getMessage());
                    } finally {
                        sock.close();
                    }
                } catch (IOException e) {
                    Log.d("Error socket:", e.getMessage());
                    break;
                    //e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {

            try {
                JSONObject jsonobject = new JSONObject(values[0]);
                logMesage(jsonobject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d(TAG, values[0]);
        }

        private void logMesage(JSONObject object){
            try {
                Log.d(TAG,object.getString(WifiDirectMessageDto.JsonAtributes.USERTO));
                Log.d(TAG,object.getString(WifiDirectMessageDto.JsonAtributes.MESSAGE));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class OutgoingCommTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            Log.d(TAG,"Connecting...");
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                mCliSocket = new SimWifiP2pSocket(params[0],10001);
            } catch (UnknownHostException e) {
                return "Unknown Host:" + e.getMessage();
            } catch (IOException e) {
                return "IO error:" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Log.d(TAG,"Disconnected...");
            } else {
                        new SendCommTask().executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR,"SENDING STUFF!!");
                Log.d(TAG,"Not disconnected...");
            }
        }
    }



    private class SendCommTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... msg) {
            try {
                mCliSocket.getOutputStream().write((msg[0] + "\n").getBytes());
                BufferedReader sockIn = new BufferedReader(
                        new InputStreamReader(mCliSocket.getInputStream()));
                sockIn.readLine();
                mCliSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCliSocket = null;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d(TAG,"Disconnected...");
        }
    }


}
