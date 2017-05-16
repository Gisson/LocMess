package pt.ulisboa.tecnico.ist.cmu.locmess.wifiDirect;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;

/**
 * Created by jorge on 09/05/17.
 */

public class WifiHandler implements SimWifiP2pManager.PeerListListener, SimWifiP2pManager.GroupInfoListener {

    private SimWifiP2pBroadcastReceiver _mreceiver;
    private Context _context;
    private boolean _mBound = false;
    public SimWifiP2pManager _mManager = null;
    private SimWifiP2pManager.Channel _mChannel = null;
    private SimWifiP2pSocketServer _mSrvSocket = null;
    public HashMap<String, String> connected;
    public ArrayList<String> nearbyAvailable;


    public WifiHandler(Context context, SimWifiP2pBroadcastReceiver mreceiver) {

        SimWifiP2pSocketManager.Init(context);
        IntentFilter filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        connected = new HashMap<String, String>();
        nearbyAvailable = new ArrayList<String>();
        _mreceiver = mreceiver;
        _context = context;
        _context.registerReceiver(_mreceiver, filter);
    }

    public void requestPeers(SimWifiP2pManager manager, SimWifiP2pManager.PeerListListener listener) {
        if (_mBound) {
            _mManager.requestPeers(_mChannel, listener);
        } else {
            Log.d("WIFI_MANAGER", "Service not bound.");
        }
    }


    public void requestGroupInfo() {
        if (_mBound) {
            _mManager.requestGroupInfo(_mChannel, WifiHandler.this);
        } else {
            Log.d("WIFI_MANAGER", "Service not bound.");
        }

    }

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList simWifiP2pDeviceList, SimWifiP2pInfo simWifiP2pInfo) {
        StringBuilder peersStr = new StringBuilder();
        for (String deviceName : simWifiP2pInfo.getDevicesInNetwork()) {
            SimWifiP2pDevice device = simWifiP2pDeviceList.getByName(deviceName);
            String devstr = "" + deviceName + " (" +
                    ((device == null) ? "??" : device.getVirtIp()) + ")\n";
            peersStr.append(devstr);
            connected.put(deviceName, device.getVirtIp());
        }
    }

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList simWifiP2pDeviceList) {
        nearbyAvailable.clear();
        for (SimWifiP2pDevice device : simWifiP2pDeviceList.getDeviceList()) {
            nearbyAvailable.add(device.getVirtIp());
        }
    }


    public void wifiOn() {
        if (_mBound)
            return;
        Intent intent = new Intent(_context, SimWifiP2pService.class);
        _context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        _mBound = true;

        new IncommingCommTask().executeOnExecutor(
               AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void wifiOff() {
        if (_mBound) {
            _context.unbindService(mConnection);
            _mBound = false;
        }
    }


    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            _mManager = new SimWifiP2pManager(new Messenger(service));
            _mChannel = _mManager.initialize(_context, _context.getMainLooper(), null);
            _mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            _mManager = null;
            _mChannel = null;
            _mBound = false;
        }
    };

    public class IncommingCommTask extends AsyncTask<Void, String, Void> {
        private String TAG = "IncommingCommTask";

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (_mSrvSocket == null)
                    _mSrvSocket = new SimWifiP2pSocketServer(10001);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    SimWifiP2pSocket sock = _mSrvSocket.accept();
                    try {
                        Log.d(TAG, "received a message");
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
    }

    public class SendCommTask extends AsyncTask<String, String, Void> {
        SimWifiP2pSocket mCliSocket;

        @Override
        protected Void doInBackground(String... msg) {
            try {
                Log.d("TEST", "Sending message " + msg[0] + msg[1]);
                mCliSocket = new SimWifiP2pSocket(msg[0], 10001);
                if (mCliSocket == null) {
                    Log.d("TAG", "no such user");
                } else {
                    mCliSocket.getOutputStream().write((msg[1] + "\n").getBytes());
                    BufferedReader sockIn = new BufferedReader(
                            new InputStreamReader(mCliSocket.getInputStream()));
                    sockIn.readLine();
                    mCliSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCliSocket = null;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //clean ui
        }
    }


    public void wifiEnabled(boolean state) {
        String statePrint;
        if (state)
            statePrint = "WiFi Direct enabled";
        else
            statePrint = "WiFi Direct disabled";

        Log.d("WifiEnabled",statePrint);
    }

    public void peersChanged() {
        Log.d("peersChanged","peer list changed");
    }


    public void membChanged() {
        Log.d("membChanged","Network membership changed");
    }


    public void ownerChanged() {
        Log.d("ownerChanged","Group ownership changed");
    }
}
