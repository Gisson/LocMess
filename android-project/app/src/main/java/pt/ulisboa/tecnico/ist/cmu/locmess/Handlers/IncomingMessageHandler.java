package pt.ulisboa.tecnico.ist.cmu.locmess.Handlers;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import pt.ulisboa.tecnico.ist.cmu.locmess.dto.MessageDto;
import pt.ulisboa.tecnico.ist.cmu.locmess.services.MessageNotificationService;

/**
 * Created by jorge on 18/05/17.
 */

public class IncomingMessageHandler extends Handler {

    private final String TAG="IncomingMessageHandler";

    private Messenger _serviceMesseger = null;

    private Activity _activity = null;
    private boolean _bound=false;
    //private Activity _activity;


    public IncomingMessageHandler(Activity activity){
        _activity=activity;
    }

    @Override
    public void handleMessage(Message message){
        Log.d(TAG,"Handling message");
        switch (message.what){
            case MessageNotificationService.MessageCodes.MSG_NEW_MESSAGE:
                break;
            default:
                super.handleMessage(message);
                break;
        }
    }


    private ServiceConnection _connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            _serviceMesseger= new Messenger(iBinder);
            Log.d(TAG,"Service Attached");
            _bound=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            _serviceMesseger=null;
            Log.wtf(TAG,"Service disconnected");
            _bound=false;
        }

    };

    public void doBindService(){
        if(_bound)
            return;
        Intent intent = new Intent(_activity, MessageNotificationService.class);
        _activity.bindService(intent, _connection, Context.BIND_AUTO_CREATE);
        _bound=true;
    }

    public void doUnbindService(){
        if(_bound){
            _activity.unbindService(_connection);
        }
    }

    public void receiveMessage(MessageDto message){
        if(_bound) {
            (new ReceiveMessageAsync()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, message);
        }
    }

    private class ReceiveMessageAsync extends AsyncTask<MessageDto,Void,Boolean>{

        @Override
        protected void onPreExecute(){
            Log.d(TAG,"Preparing notification");
        }

        @Override
        protected Boolean doInBackground(MessageDto... messageDtos) {
            try {
                Message  msg = Message.obtain(null, MessageNotificationService.MessageCodes.MSG_NEW_MESSAGE);
                Bundle b = new Bundle();
                b.putString("author",messageDtos[0].getAuthor());
                b.putString("message",messageDtos[0].getMessage());
                b.putString("title",messageDtos[0].getTitle());
                b.putString("location",messageDtos[0].getLocation());
                msg.obj=b;
                _serviceMesseger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return true;
        }
    }



}
