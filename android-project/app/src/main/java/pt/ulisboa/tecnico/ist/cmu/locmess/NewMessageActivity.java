package pt.ulisboa.tecnico.ist.cmu.locmess;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.ist.cmu.locmess.commands.AbstractCommand;
import pt.ulisboa.tecnico.ist.cmu.locmess.commands.PostMessageCommand;
import pt.ulisboa.tecnico.ist.cmu.locmess.dto.PolicyDto;
import pt.ulisboa.tecnico.ist.cmu.locmess.dto.TopicDto;


public class NewMessageActivity extends AppCompatActivity {

    private AbstractCommand command;
    private String TAG="NewMesageActivity";
    private PolicyDto _policy=null;
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
        Intent i = new Intent(this, EditPolicyActivity.class);
        startActivityForResult(i, LocMessManager.PICK_POLICY_REQUEST);
    }

    public void selectLocation(View v){
        Intent i = new Intent(this, LocationsMenuActivity.class);
        i.putExtra(LocationsMenuActivity.CHOOSE_LOCATION,true);
        startActivityForResult(i, LocMessManager.PICK_LOCATION_REQUEST);
        //startActivity(i);
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
        if(_policy==null){
            Toast.makeText(this,"Please select policy.",Toast.LENGTH_SHORT).show();
            return;
        }
        EditText content = (EditText) findViewById(R.id.message_content);
        EditText title = (EditText) findViewById(R.id.message_title_et);
        EditText lifespan = (EditText) findViewById(R.id.message_lifespan);
        command = new PostMessageCommand(LocMessManager.getInstance().getToken(),location.getText().toString().trim(),
                                            content.getText().toString().trim(),title.getText().toString(),
                                            mode.getSelectedItem().toString(),_policy,
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d(TAG,"Activity Result inc! The code is "+requestCode);
        if(requestCode == LocMessManager.PICK_LOCATION_REQUEST){
            if( resultCode ==  RESULT_OK){
                Log.d(TAG,"AND IT WAS SUCESSFUL!!!");
                ((TextView) findViewById(R.id.location_name)).setText(data.getStringExtra("locationChoice"));
            }
        }
        if(requestCode == LocMessManager.PICK_POLICY_REQUEST){
            if(resultCode == RESULT_OK){
                Log.d(TAG,"AND IT WAS SUCESSFUL!!!");
                ((TextView) findViewById(R.id.select_policy)).setText(data.getStringExtra("policyType"));
                Log.d(TAG,data.getSerializableExtra("topics").toString());
                List<TopicDto> topics= new ArrayList<>();
                for( String s : (ArrayList<String>) data.getSerializableExtra("topics")){ //TODO: CHANGE THIS WHEN CHANGED TO TOPICDTO
                    topics.add(new TopicDto(s));
                }
                _policy=new PolicyDto(data.getStringExtra("policyType"),topics);
                //_policy=new PolicyDto()

                //data.getParcelableArrayExtra()
                //_policy=new PolicyDto(data.getStringExtra("policyType"),)
            }
        }
    }
}
