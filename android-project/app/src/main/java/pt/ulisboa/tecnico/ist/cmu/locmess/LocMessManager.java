package pt.ulisboa.tecnico.ist.cmu.locmess;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.ulisboa.tecnico.ist.cmu.locmess.BroadcastReceivers.BatteryScannerReceiver;
import pt.ulisboa.tecnico.ist.cmu.locmess.BroadcastReceivers.WifiScannerReceiver;
import pt.ulisboa.tecnico.ist.cmu.locmess.commands.AbstractCommand;
import pt.ulisboa.tecnico.ist.cmu.locmess.commands.GetLocationCommand;
import pt.ulisboa.tecnico.ist.cmu.locmess.commands.ListMessagesCommand;
import pt.ulisboa.tecnico.ist.cmu.locmess.commands.LoginUserCommand;
import pt.ulisboa.tecnico.ist.cmu.locmess.dto.MessageDto;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.CommandNotExecutedException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.DuplicateExecutionException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.LocMessHttpException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.NoLocationException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.NoNetworksAroundException;

/**
 * Created by jorge on 03/04/17.
 */

public class LocMessManager {



    //Callback will notify us when async task is finished
    public interface CompleteCallback{
        void OnComplete(boolean result, String message);
    }
    public static final int PICK_LOCATION_REQUEST=1;
    public static final int PICK_POLICY_REQUEST=2;
    private static LocMessManager _manager=null;
    private String _currentToken = "undefined";
    private String _username = "undefined";
//    private Location _lastKnown=null;
    private String _lastLocation="";
//    private LocationListener locationListener;
//    private LocationManager locationManager;
    public WifiManager mWifiManager = null;
//    public BatteryScannerReceiver _batteryReceiver;


    protected LocMessManager(){

    }
    public static LocMessManager getInstance(){
        if(_manager==null){
            _manager=new LocMessManager();
        }
        return _manager;
    }

    public void executeAsync(AbstractCommand abstractCommand, CompleteCallback callback){
        (new NetworkingCommand(callback)).execute(abstractCommand);
    }

    public class NetworkingCommand extends AsyncTask<AbstractCommand,Void,Boolean>{

        public boolean _completed=false;

        CompleteCallback callback;
        String message = "";

        AbstractCommand command;

        //Here we run our async task, handle errors and passing error messages via callback
        public NetworkingCommand(CompleteCallback callback){
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(AbstractCommand... abstractCommands) {
            boolean result = false;
            for( AbstractCommand c : abstractCommands){
                command = c;
                try {
                    c.execute();
                    result = c.successfulRequest();
                } catch (IOException e) {
                    e.printStackTrace();

                    message = "Can not complete request. Response code: " + c.getResponseCode();

                } catch (DuplicateExecutionException e) {
                    //FIXME: Handle this exception correctly
                    e.printStackTrace();
                } catch (CommandNotExecutedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result){
            System.out.println("RESULT IS: "+result);
            _completed = result;
            if(!result){
                try{
                    //Check reason why task is fail
                    String reason = command.getReason();
                    if(!TextUtils.isEmpty(reason)){
                        message = reason;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(TextUtils.isEmpty(message)){
                    message = "Fail";
                }
            }
            if(callback != null){
                callback.OnComplete(result, message);
            }
        }
    }

    public void startTermiteP2pSocketManager(Context c){
        SimWifiP2pSocketManager.Init(c);
    }

    public void setToken(String token){
        _currentToken=token;
    }
    public String getToken(){
        return _currentToken;
    }
    public void setUsername(String username) {
        _username = username;
    }
    public String getUsername() {
        return _username;
    }


    public String findLocation(Location location){
        final GetLocationCommand command = new GetLocationCommand(_currentToken,String.valueOf(location.getLatitude()),
                String.valueOf(location.getLongitude()));
        LocMessManager.getInstance().executeAsync(command, new LocMessManager.CompleteCallback() {
            @Override
            public void OnComplete(boolean result, String message) {
                if(result){
                    //Toast.makeText(MyMessagesMenuActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    try {
                        _lastLocation = command.getResult();
                    } catch (CommandNotExecutedException e) {
                        e.printStackTrace();
                    } catch (NoLocationException e) {
                        _lastLocation="";
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("LocMessManager", "result was false");
                }
            }
        });
        return _lastLocation;
    }

    public String findLocation(String ssid){
        final GetLocationCommand command = new GetLocationCommand(_currentToken,ssid);
        LocMessManager.getInstance().executeAsync(command, new LocMessManager.CompleteCallback() {
            @Override
            public void OnComplete(boolean result, String message) {
                if(result){
                    //Toast.makeText(MyMessagesMenuActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    try {
                        _lastLocation = command.getResult();
                    } catch (CommandNotExecutedException e) {
                        e.printStackTrace();
                    } catch (NoLocationException e) {
                        _lastLocation="";
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("LocMessManager", "result was false");
                }
            }
        });
        return _lastLocation;
    }

    public String findLocation(Location location,String ssid){
        final GetLocationCommand command = new GetLocationCommand(_currentToken,String.valueOf(location.getLatitude()),
                String.valueOf(location.getLongitude()),ssid);
        LocMessManager.getInstance().executeAsync(command, new LocMessManager.CompleteCallback() {
            @Override
            public void OnComplete(boolean result, String message) {
                if(result){
                    //Toast.makeText(MyMessagesMenuActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    try {
                        _lastLocation = command.getResult();
                    } catch (CommandNotExecutedException e) {
                        e.printStackTrace();
                    } catch (NoLocationException e) {
                        _lastLocation="";
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("LocMessManager", "result was false");
                }
            }
        });
        return _lastLocation;
    }

    public ScanResult getClosestSsid(WifiManager manager){
        ScanResult closest=null;
        if(manager.getScanResults().size()<=0){
            throw new NoNetworksAroundException();
        }
        for( ScanResult sr : manager.getScanResults()){
            if(closest==null){
                closest=sr;
            }else{
                if( sr.level > closest.level){
                    closest=sr;
                }
            }
        }
        return closest;
    }

    public void getSsids(){
        WifiScannerReceiver mWifiScanReceiver = new WifiScannerReceiver(mWifiManager);
    }



}
