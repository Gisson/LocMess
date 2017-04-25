package pt.ulisboa.tecnico.ist.cmu.locmess;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

import pt.ulisboa.tecnico.ist.cmu.locmess.commands.AbstractCommand;
import pt.ulisboa.tecnico.ist.cmu.locmess.commands.RegisterUserCommand;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.DuplicateExecutionException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.CommandNotExecutedException;

public class RegisterActivity extends AppCompatActivity {

    private RegisterUserCommand command;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        setContentView(R.layout.activity_register);
    }

    public void register(View v){
        String username=((EditText)findViewById(R.id.username_input)).getText().toString();
        String passwd=((EditText)findViewById(R.id.password_input)).getText().toString();

        command=new RegisterUserCommand(username,passwd);

        (new RegisterAsync()).execute(command);
    }

    public void register(){
        try {
            if(command.successfulRequest()){
                Toast.makeText(getApplicationContext(),"Registration Sucessful", Toast.LENGTH_SHORT);
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
            }else{
                Toast.makeText(getApplicationContext(),"Registration Failed", Toast.LENGTH_SHORT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DuplicateExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (CommandNotExecutedException e) {
            e.printStackTrace();
        }

    }



    private class RegisterAsync extends AsyncTask<RegisterUserCommand,Void,Boolean> {

        @Override
        protected Boolean doInBackground(RegisterUserCommand... registerCommand) {
            for( AbstractCommand c : registerCommand){
                try {
                    c.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DuplicateExecutionException e) {
                    //FIXME: Handle this exception correctly
                    e.printStackTrace();
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            register();
        }
    }
}
