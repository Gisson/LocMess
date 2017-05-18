package pt.ulisboa.tecnico.ist.cmu.locmess.services;

import android.app.FragmentController;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import pt.inesc.termite.wifidirect.util.AsyncService;
import pt.ulisboa.tecnico.ist.cmu.locmess.Handlers.IncomingMessageHandler;
import pt.ulisboa.tecnico.ist.cmu.locmess.R;

/**
 * Created by jorge on 18/05/17.
 */

public class MessageNotificationService extends Service {

    protected Messenger _mMessenger;
    private IncomingHandler _handler;
    private NotificationManager _nM;

    private final int SERVICE_TAG=1;

    public static final class MessageCodes{
        public static final int MSG_NEW_MESSAGE=1;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Service","Service is running");
        _handler=new IncomingHandler();
        _mMessenger = new Messenger(_handler);
        _nM= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public void onDestroy(){
        _nM.cancel(SERVICE_TAG);
        Log.wtf("Service","Service stopped");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return _mMessenger.getBinder();
    }


    public void showNotification(Object message){
        Bundle b = (Bundle) message;
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, FragmentController.class), 0);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_my_messages)  // the status icon
                .setTicker("Message from "+b.getString("author"))  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle("New message from "+b.getString("author"))  // the label of the entry
                .setContentText(b.getString("title"))  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();

        // Send the notification.
        // We use a string id because it is a unique number.  We use it later to cancel.
        _nM.notify(SERVICE_TAG, notification);

    }

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessageCodes.MSG_NEW_MESSAGE:
                    showNotification(msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }


}
