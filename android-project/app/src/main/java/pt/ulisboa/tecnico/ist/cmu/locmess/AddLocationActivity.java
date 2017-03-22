package pt.ulisboa.tecnico.ist.cmu.locmess;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class AddLocationActivity extends AppCompatActivity {

    EditText nameEt, latEt, lonEt, radiusEt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setTitle("New Location");
        setContentView(R.layout.activity_add_location);

        nameEt = (EditText) findViewById(R.id.name_et);
        latEt = (EditText) findViewById(R.id.lat_et);
        lonEt = (EditText) findViewById(R.id.lon_et);
        radiusEt = (EditText) findViewById(R.id.radius_et);
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
            //here go back to locations and get data
            Intent i = new Intent(AddLocationActivity.this, LocationsMenuActivity.class);
            i.putExtra("name", nameEt.getText().toString());
            i.putExtra("lat", latEt.getText().toString());
            i.putExtra("lon", lonEt.getText().toString());
            i.putExtra("radius", radiusEt.getText().toString());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        } else if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
