package pt.ulisboa.tecnico.ist.cmu.locmess;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import pt.ulisboa.tecnico.ist.cmu.locmess.commands.AbstractCommand;
import pt.ulisboa.tecnico.ist.cmu.locmess.commands.PostMessageCommand;


public class NewMessageActivity extends AppCompatActivity {

    private AbstractCommand command;

    private String modes[] = new String[]{
            "Centralized",
            "Decentralized",
            "Mode"
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setTitle("New Message");
        setContentView(R.layout.activity_new_message);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, modes) {

            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }

                return v;
            }

            @Override
            public int getCount() {
                return super.getCount()-1; // you dont display last item. It is used as hint.
            }

        };

        AppCompatSpinner spinner = (AppCompatSpinner) findViewById(R.id.mode_spinner);
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getCount());
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position > 0){

                } else {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void selectPolicy(View v){
        Intent i = new Intent(this, SelectPolicyActivity.class);
        startActivity(i);
    }

    public void selectLocation(View v){
        Intent i = new Intent(this, LocationsMenuActivity.class);
        startActivity(i);
    }

    public void postMesage(View v){
        TextView location = (TextView)findViewById(R.id.location_name);
 /*       if(location.getText().toString().equals(getResources().getString(R.string.select_location))){
            Toast.makeText(this,"Please select location.",Toast.LENGTH_SHORT).show();
            return;
        }*/
 /*       TextView policy = (TextView)findViewById(R.id.select_policy);
        if(policy.getText().toString().equals(getResources().getString(R.string.policy))){
            Toast.makeText(this,"Please select policy.",Toast.LENGTH_SHORT).show();
            return;
        }*/
        AppCompatSpinner mode = (AppCompatSpinner) findViewById(R.id.mode_spinner);
        /*if(policy.getText().equals(modes.length-1)){
            Toast.makeText(this,"Please select policy.",Toast.LENGTH_SHORT).show();
            return;
        } TODO: check if works*/
        EditText content = (EditText) findViewById(R.id.message_content);
        EditText title = (EditText) findViewById(R.id.message_title_et);
        EditText lifespan = (EditText) findViewById(R.id.message_lifespan);
        command = new PostMessageCommand(LocMessManager.getInstance().getToken(),"RNL"/*location.getText().toString().trim()*/,
                                            content.getText().toString().trim(),title.getText().toString(),
                                            mode.getSelectedItem().toString(),new ArrayList<String>(),
                                            lifespan.getText().toString().trim());//Mock stuff

        //Run in Manager and catch errors
        LocMessManager.getInstance().executeAsync(command, new LocMessManager.CompleteCallback() {
            @Override
            public void OnComplete(boolean result, String message) {
                if(result){
                    Toast.makeText(NewMessageActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(NewMessageActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
