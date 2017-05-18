package pt.ulisboa.tecnico.ist.cmu.locmess;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import pt.ulisboa.tecnico.ist.cmu.locmess.commands.AddLocationCommand;

public class AddLocationActivity extends AppCompatActivity {

    EditText nameEt, gpsEt, ssidEt, bssidEt;

    AddLocationCommand command;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setTitle("New Location");
        setContentView(R.layout.activity_add_location);

        nameEt = (EditText) findViewById(R.id.name_et);
        gpsEt = (EditText) findViewById(R.id.gps_coords_et);
        ssidEt = (EditText) findViewById(R.id.ssid_et);
        bssidEt = (EditText) findViewById(R.id.bssid_et);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_location_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.action_add) {
            //here run add location command

            //Get lat, lon and radius from entered string
            String[] gpsData = gpsEt.getText().toString().split(",");
            String lat, lon, radius;
            try {
                lat = gpsData[0];
            }catch (IndexOutOfBoundsException e){
                lat = "";
            }
            try{
                lon = gpsData[1];
            }catch (IndexOutOfBoundsException e){
                lon = "";
            }
            try{
                radius = gpsData[2];
            }catch (IndexOutOfBoundsException e){
                radius = "";
            }

            command=new AddLocationCommand(LocMessManager.getInstance().getToken(),
                    nameEt.getText().toString(),
                    lat, lon, radius,
                    ssidEt.getText().toString(),
                    bssidEt.getText().toString());

            //We Running async task in Manager and catch errors
            LocMessManager.getInstance().executeAsync(command, new LocMessManager.CompleteCallback() {

                @Override
                public void OnPreExecute(){}
                @Override
                public void OnComplete(boolean result, String message) {
                    if(validateRequest()){
                        Toast.makeText(AddLocationActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(AddLocationActivity.this, LocationsMenuActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(AddLocationActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    //Here I get Fail even if request is success, because if location is successfully added we receive an empty body
    public boolean validateRequest(){
        boolean result;
        try{
            result = command != null && command.successfulRequest();
        } catch (Exception e){
            result = false;
        }
        return  result;
    }
}
