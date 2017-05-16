package pt.ulisboa.tecnico.ist.cmu.locmess.wifiDirect;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.ulisboa.tecnico.ist.cmu.locmess.LocMessActivityInterfaces.LocMessActivity;
import pt.ulisboa.tecnico.ist.cmu.locmess.dto.LocationDto;
import pt.ulisboa.tecnico.ist.cmu.locmess.dto.MessageDto;
import pt.ulisboa.tecnico.ist.cmu.locmess.dto.PolicyDto;

/**
 * Created by jorge on 16/05/17.
 */

public class WifiP2PHandler implements SimWifiP2pManager.PeerListListener, SimWifiP2pManager.GroupInfoListener {

    private static final String TAG="WifiP2PHandler";

    private Activity _activity;
    private SimWifiP2pBroadcastReceiver _receiver;
    private IntentFilter _filter;
    private SimWifiP2pManager.Channel _channel = null;
    private Messenger _service = null;
    private boolean _bound = false;
    private SimWifiP2pManager _manager = null;
    private SimWifiP2pSocketServer _servSocket = null;
    private SimWifiP2pSocket _cliSocket = null;
    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            _service = new Messenger(service);
            _manager = new SimWifiP2pManager(_service);
            _channel = _manager.initialize(_activity.getApplication(), _activity.getMainLooper(), null);
            _bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            _service = null;
            _manager = null;
            _channel = null;
            _bound = false;
        }
    };

    public WifiP2PHandler(Activity activity){
        _activity = activity;
    }




    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        StringBuilder peersStr = new StringBuilder();

        // compile list of devices in range
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            String devstr = "" + device.deviceName + " (" + device.getVirtIp() + ")\n";
            peersStr.append(devstr);
        }

        // display list of devices in range
        new AlertDialog.Builder(_activity)
                .setTitle("Devices in WiFi Range")
                .setMessage(peersStr.toString())
                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList devices,
                                     SimWifiP2pInfo groupInfo) {

        // compile list of network members
        StringBuilder peersStr = new StringBuilder();
        for (String deviceName : groupInfo.getDevicesInNetwork()) {
            SimWifiP2pDevice device = devices.getByName(deviceName);
            String devstr = "" + deviceName + " (" +
                    ((device == null)?"??":device.getVirtIp()) + ")\n";
            peersStr.append(devstr);
        }

        // display list of network members
        new AlertDialog.Builder(_activity)
                .setTitle("Devices in WiFi Network")
                .setMessage(peersStr.toString())
                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }


    // -----------------------  START OF ASYNCTASKS SECTION----------------------------------

        private class SendCommTask extends AsyncTask<String, String, Void> {

            @Override
            protected Void doInBackground(String... msg) {
                try {
                    Log.d(TAG,"Sending message! ");
                    _cliSocket= new SimWifiP2pSocket(msg[0],10001);
                    if(_cliSocket == null){
                        Log.w(TAG,"No such user!");
                    }else{
                        _cliSocket.getOutputStream().write((msg[1] + "\n").getBytes());
                        BufferedReader sockIn = new BufferedReader(
                                new InputStreamReader(_cliSocket.getInputStream()));
                        sockIn.readLine();
                        _cliSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                _cliSocket = null;
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                Log.d(TAG, "Disconnected...");
            }
        }

        private class OutgoingCommTask extends AsyncTask<String, Void, String> {


            @Override
            protected void onPreExecute() {
                Log.d(TAG, "Connecting...");
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    _cliSocket = new SimWifiP2pSocket(params[0], 10001);
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
                    Log.d(TAG, "Disconnected...");
                } else {
                    Log.d(TAG, "Connnected with success! ");
                }
            }
        }

        private class IncommingCommTask extends AsyncTask<Void, String, Void> {

            private final String TAG=WifiP2PHandler.TAG+"IncComm";

            @Override
            protected Void doInBackground(Void... params) {

                Log.d(TAG, "IncomingCommTask started (" + this.hashCode() + ").");



                try {
                    if( _servSocket == null ){
                        _servSocket = new SimWifiP2pSocketServer(10001);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        SimWifiP2pSocket sock = _servSocket.accept();
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
                    Log.d(TAG,"Received message!");

            }
        }

    // -----------------------  END OF ASYNCTASKS SECTION----------------------------------

    public void requestPeers(SimWifiP2pManager.PeerListListener listener){
        if(_bound){
            _manager.requestPeers(_channel,listener);
        }
        else{
            Log.w(TAG,"Service not bound!");
        }
    }

    public void requestGroupInfo(){
        if(_bound){
            _manager.requestPeers(_channel,WifiP2PHandler.this);
        }
        else{
            Log.w(TAG,"Service not bound!");
        }

    }

    //Author is the actual author of the message, userFrom is the IP from where the message comes

    public void sendMessage(String locationName, String content, String title, PolicyDto policy
                            ,String deliveryMode, String duration, String userTo, String author,String sendTo){
        WifiDirectMessageDto message = new WifiDirectMessageDto(author,content,title,locationName,policy,userTo);
        new SendCommTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,sendTo,message.toJson());

    }


}