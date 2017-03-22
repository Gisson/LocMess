package pt.ulisboa.tecnico.ist.cmu.locmess;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
    }

    public void register(View v){
        Intent i = new Intent(this,RegisterActivity.class);
        startActivity(i);
    }

    public void login(View v){
        EditText text = (EditText)findViewById(R.id.username_input);
        System.out.println(((EditText)findViewById(R.id.username_input)).getText().toString());
        /*This should just actually do some checks with the server in an assyncthread but for now it's k babe*/
        if(((EditText)findViewById(R.id.username_input)).getText().toString().equals("Donald") &&
        ((EditText)findViewById(R.id.password_input)).getText().toString().equals("America" )){
            Intent i=new Intent(this,LocationsMenuActivity.class);
            startActivity(i);
        }
    }
}
