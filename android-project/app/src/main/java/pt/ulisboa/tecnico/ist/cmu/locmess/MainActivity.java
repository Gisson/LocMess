package pt.ulisboa.tecnico.ist.cmu.locmess;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import pt.ulisboa.tecnico.ist.cmu.locmess.commands.AbstractCommand;
import pt.ulisboa.tecnico.ist.cmu.locmess.commands.LoginUserCommand;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.AlreadyRequestedException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.LoginFailedException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.NotYetRequestedException;

public class MainActivity extends AppCompatActivity {

    public static final int RUNNING=0;
    public static final int COMPLETED=1;
    private LoginUserCommand command;

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


        (new LoginAsync()).execute(command);

    }

    public void login(){
        try {
            String token = command.getToken();
            LocMessManager.getInstance().setToken(token);
            Toast.makeText(getApplicationContext(),"Login successful", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, LocationsMenuActivity.class);
            startActivity(i);
        }catch(LoginFailedException e){
            Toast.makeText(getApplicationContext(),"Login failed", Toast.LENGTH_SHORT).show();
        } catch (NotYetRequestedException  e) {
            e.printStackTrace();
        }
    }

    private class LoginAsync extends AsyncTask<LoginUserCommand,Void,Boolean>{

        @Override
        protected Boolean doInBackground(LoginUserCommand... loginCommand) {
            for( AbstractCommand c : loginCommand){
                try {
                    c.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (AlreadyRequestedException e) {
                    //FIXME: Handle this exception correctly
                    e.printStackTrace();
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            login();
        }
    }
}
