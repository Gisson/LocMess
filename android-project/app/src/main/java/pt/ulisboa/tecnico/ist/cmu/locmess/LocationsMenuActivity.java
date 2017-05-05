package pt.ulisboa.tecnico.ist.cmu.locmess;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.ist.cmu.locmess.adapters.LocationsListAdapter;
import pt.ulisboa.tecnico.ist.cmu.locmess.commands.ListLocationsCommand;
import pt.ulisboa.tecnico.ist.cmu.locmess.dto.LocationDto;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.LocMessHttpException;

public class LocationsMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private LocationsListAdapter messagesAdapter;
    private static final String TAG = "LocationsMeny";
    private ListLocationsCommand command;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        command = new ListLocationsCommand(LocMessManager.getInstance().getToken());

        //Run in Manager and catch errors
        LocMessManager.getInstance().executeAsync(command, new LocMessManager.CompleteCallback() {
            @Override
            public void OnComplete(boolean result, String message) {
                if(result){
                    Toast.makeText(LocationsMenuActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    populateData();
                } else {
                    Toast.makeText(LocationsMenuActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void  populateData(){
        try {
            //Parse received messages
            ArrayList<LocationDto> locations = new ArrayList<>();
            List<LocationDto> bla = command.getResults();
            for (LocationDto m : command.getResults()) {
                locations.add(m);
            }
            messagesAdapter = new LocationsListAdapter(locations, this);
            ListView messagesList = (ListView) findViewById(R.id.locations_list);
            messagesList.setAdapter(messagesAdapter);
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_location);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Intent i = new Intent(LocationsMenuActivity.this, AddLocationActivity.class);
                    startActivity(i);
                }
            });
            ((FloatingActionButton) findViewById(R.id.add_location)).setEnabled(true);
        }catch( LocMessHttpException e) {
            Toast.makeText(LocationsMenuActivity.this,e.getReason(),Toast.LENGTH_SHORT);
        }catch (Exception e){
            //Still not receiving the messages? I need to fix this...
            Log.e("MESSAGES", "Request success but we do not receive any locations");
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.locations_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent i = new Intent(LocationsMenuActivity.this, MyProfile.class);
            startActivity(i);
        } else if (id == R.id.nav_my_messages) {
            Intent i = new Intent(LocationsMenuActivity.this, MyMessagesMenuActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_help) {
            Toast.makeText(getApplication().getBaseContext(),"No help lel",Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    public void addLocation(View v){
        Intent i = new Intent(this, AddLocationActivity.class);
        startActivity(i);
    }
}
