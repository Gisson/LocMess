package pt.ulisboa.tecnico.ist.cmu.locmess.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by jorge on 09/05/17.
 */

public class NewMessageService extends IntentService {

    private final String TAG="NewMessageService";

    public NewMessageService() {
        super("NewMessageService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String title = intent.getStringExtra("title");
        Log.d(TAG,"New message with title: "+title);
    }


}
