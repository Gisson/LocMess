package pt.ulisboa.tecnico.ist.cmu.locmess;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import pt.ulisboa.tecnico.ist.cmu.locmess.BroadcastReceivers.WifiScannerReceiver;
import pt.ulisboa.tecnico.ist.cmu.locmess.Listeners.LocMessLocationListener;
import pt.ulisboa.tecnico.ist.cmu.locmess.commands.AbstractCommand;
import pt.ulisboa.tecnico.ist.cmu.locmess.commands.ListKeysCommand;
import pt.ulisboa.tecnico.ist.cmu.locmess.commands.LoginUserCommand;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.DuplicateExecutionException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.LoginFailedException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.CommandNotExecutedException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.NoNetworksAroundException;
import pt.ulisboa.tecnico.ist.cmu.locmess.wifiDirect.WifiP2PHandler;

public class MainActivity extends AppCompatActivity {

    public static final int RUNNING=0;
    public static final int COMPLETED=1;
    private static final String TAG = "MAINACTIVITY";
    private LoginUserCommand command;
    private LocMessManager manager = LocMessManager.getInstance();

    public Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        setContentView(R.layout.activity_main);






    }

    public void register(View v){
        Intent i = new Intent(this,RegisterActivity.class);
        startActivity(i);
    }

    public void login(View v){

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                Toast.makeText(getApplicationContext(),Integer.toString(message.what), Toast.LENGTH_SHORT).show();
            }
        };

        String username=((EditText)findViewById(R.id.username_input)).getText().toString();
        String passwd=((EditText)findViewById(R.id.password_input)).getText().toString();
        command=new LoginUserCommand(username, passwd);
        LocMessManager.getInstance().executeAsync(command, new LocMessManager.CompleteCallback() {
            @Override
            public void OnPreExecute(){Toast.makeText(MainActivity.this,"Authenticating",Toast.LENGTH_SHORT);}

            @Override
            public void OnComplete(boolean result, String message) {
                if(result){
                    login();
                } else if(!TextUtils.isEmpty(message)){
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });

//        (new LoginAsync()).execute(command);

    }

    public void login(){
        try {
            String token = command.getToken();
            manager.setToken(token);
            String username = command.getUsername();
            manager.setUsername(username);
            // FIXME storing the token and username should be done by the manager somehow
            Toast.makeText(getApplicationContext(),"Login successful", Toast.LENGTH_SHORT).show();

            // Acquire a reference to the system Location Manager

            Intent i = new Intent(this, MyMessagesMenuActivity.class);
            LocMessLocationListener listener = new LocMessLocationListener();
           // i.putExtra("nearby",true);
            startActivity(i);
            this.finish();
        }catch(LoginFailedException e){
            Toast.makeText(getApplicationContext(),"Login failed", Toast.LENGTH_SHORT).show();
        } catch (CommandNotExecutedException e) {
            e.printStackTrace();
        }
    }

//    private class LoginAsync extends AsyncTask<LoginUserCommand,Void,Boolean>{
//
//        @Override
//        protected Boolean doInBackground(LoginUserCommand... loginCommand) {
//
//            for( AbstractCommand c : loginCommand){
//                try {
//                    c.execute();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (AlreadyRequestedException e) {
//                    //FIXME: Handle this exception correctly
//                    e.printStackTrace();
//                }
//            }
//
//            return true;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean aBoolean) {
////            login();
//
//            Intent i = new Intent(MainActivity.this, LocationsMenuActivity.class);
//            startActivity(i);
//        }
//    }
}
