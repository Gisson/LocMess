package pt.ulisboa.tecnico.ist.cmu.locmess.wifiDirect;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.ulisboa.tecnico.ist.cmu.locmess.LocMessActivityInterfaces.LocMessActivity;
import pt.ulisboa.tecnico.ist.cmu.locmess.LocMessManager;
import pt.ulisboa.tecnico.ist.cmu.locmess.dto.LocationDto;
import pt.ulisboa.tecnico.ist.cmu.locmess.dto.MessageDto;
import pt.ulisboa.tecnico.ist.cmu.locmess.dto.PolicyDto;
import pt.ulisboa.tecnico.ist.cmu.locmess.dto.TopicDto;

/**
 * Created by jorge on 16/05/17.
 */

public class WifiP2PHandler implements SimWifiP2pManager.PeerListListener,
        SimWifiP2pManager.GroupInfoListener {

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
    private HashMap<String,String> _connectedDevices;
    private List<String> _nearbyAvailable;
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

    public WifiP2PHandler(Activity activity,IntentFilter intent){
        _activity = activity;
        SimWifiP2pSocketManager.Init(_activity.getApplicationContext());
        _filter=intent;
        _filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        _filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        _filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        _filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        _receiver = new SimWifiP2pBroadcastReceiver(_activity,this);
        _activity.registerReceiver(_receiver,_filter);
        _connectedDevices=new HashMap<>();
        _nearbyAvailable=new ArrayList<>();
    }




    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        Log.d(TAG,"Triggered peers ");
        _nearbyAvailable.clear();
        // compile list of devices in range
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            _nearbyAvailable.add(device.getVirtIp());
            Log.d(TAG,"Adding nearby "+device.getVirtIp());
        }

    }

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList devices,
                                     SimWifiP2pInfo groupInfo) {
        Log.d(TAG,"Triggered groupinfo ");
        // compile list of network members
        StringBuilder peersStr = new StringBuilder();
        for (String deviceName : groupInfo.getDevicesInNetwork()) {
            SimWifiP2pDevice device = devices.getByName(deviceName);
            String devstr = "" + deviceName + " (" +
                    ((device == null)?"??":device.getVirtIp()) + ")\n";
            peersStr.append(devstr);
            _connectedDevices.put(deviceName,device.getVirtIp());
            Log.d(TAG,"Adding connected "+device.getVirtIp());
        }

    }


    // -----------------------  START OF ASYNCTASKS SECTION----------------------------------

        private class SendCommTask extends AsyncTask<String, String, Void> {

            @Override
            protected Void doInBackground(String... msg) {
                try {
                    Log.d(TAG,"Sending message to "+msg[0]);
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
                try {
                    JSONObject jsonObject = new JSONObject(values[0]);
                    Log.d(TAG,"Message came from: "+jsonObject.getString(
                            WifiDirectMessageDto.JsonAtributes.AUTHOR));
                    Log.d(TAG,"TTL: "+jsonObject.getInt(
                            WifiDirectMessageDto.JsonAtributes.TTL));
                    Log.d(TAG,"Policy: "+ new JSONObject(values[0].substring(values[0].indexOf("{"),
                            values[0].lastIndexOf("}")+1)));
    //                Log.d(TAG,"Topics: "+""+jsonObject.getJSONObject(
      //                      WifiDirectMessageDto.JsonAtributes.POLICY).
        //                    getJSONArray(PolicyDto.JsonAtributes.TOPICS));
                    JSONArray arr= jsonObject.getJSONObject(
                            WifiDirectMessageDto.JsonAtributes.POLICY).
                            getJSONArray(PolicyDto.JsonAtributes.TOPICS);
                    List<TopicDto> topics=new ArrayList<>();
                    String type=jsonObject.getJSONObject(WifiDirectMessageDto.JsonAtributes.POLICY).
                            getString(PolicyDto.JsonAtributes.TYPE);
                    String location=jsonObject.getString(WifiDirectMessageDto.JsonAtributes.LOCATION);
                    for(int i=0;i<arr.length();i++){
                        topics.add(new TopicDto(arr.getJSONObject(i).getString(
                                TopicDto.JsonAtributes.KEY),arr.getJSONObject(i).getString(
                                TopicDto.JsonAtributes.VALUE)));
                    }
                    String author =jsonObject.getString(WifiDirectMessageDto.JsonAtributes.AUTHOR);
                    String message=jsonObject.getString(WifiDirectMessageDto.JsonAtributes.MESSAGE);
                    String title=jsonObject.getString(WifiDirectMessageDto.JsonAtributes.TITLE);
                    if(LocMessManager.getInstance().getLastLocation().equals(location)) {

                        if (LocMessManager.getInstance().isMessageForMe(new PolicyDto(type, topics))) {
                            Log.d(TAG, "MESSAGE IS FOR ME!");
                            WifiDirectMessageDto messageDto = new WifiDirectMessageDto(author,message,title,location,
                            new PolicyDto(type,topics));
                            LocMessManager.getInstance().pushMyMessage(messageDto);
                            LocMessManager.getInstance().getMessageHandler().receiveMessage(messageDto);
                        } else {
                            Log.d(TAG, "Its not for me :(");
                        }
                    }else{
                        Log.d(TAG,"Not from here");
                        if(jsonObject.getInt(WifiDirectMessageDto.JsonAtributes.TTL)<=1){
                            Log.d(TAG,"End of the line for this message...");
                            return;
                        }
                        LocMessManager.getInstance().pushMuleMessage(
                                new WifiDirectMessageDto(author,message,title,location,
                                        new PolicyDto(type,topics)));

                    }

                    Log.d(TAG,"The message must go on!");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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

    public void sendMessage(MessageDto message,String userTo, int ttl){
        if(_bound) {
            WifiDirectMessageDto wifimessage = new WifiDirectMessageDto(message, userTo, ttl);
            for(String ip : _connectedDevices.values()){
                new SendCommTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        ip, wifimessage.toJson());
            }
        }else{
            Log.w(TAG,"Service not bound");
        }
    }
    public void sendMessage(WifiDirectMessageDto message){
        if(_bound) {
            for(String ip : _nearbyAvailable){
                Log.d(TAG,"Message to "+ip);
                new SendCommTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        ip, message.toJson());
                Log.i(TAG,"Sent: "+message.toJson());
            }
        }else{
            Log.w(TAG,"Service not bound");
        }
    }



    public void wifiOn() {
        if (_bound)
            return;
        Intent intent = new Intent(_activity, SimWifiP2pService.class);
        _activity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        _bound = true;

        new IncommingCommTask().executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void wifiOff() {
        if (_bound) {
            _activity.unbindService(mConnection);
            _bound = false;
        }
    }

    public void peersChanged(){
        Toast.makeText(_activity,"New peers available",Toast.LENGTH_SHORT).show();
        LocMessManager.getInstance().getWifiHandler().requestPeers(this);
    }


}
