package pt.ulisboa.tecnico.ist.cmu.locmess;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pt.ulisboa.tecnico.ist.cmu.locmess.commands.ListMessagesCommand;

public class MyMessagesMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private ArrayList<String> messages;
    private BaseAdapter messagesAdapter;

    private ListMessagesCommand command;

    private Map<String, HashMap<String, String>> myMessages = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_messages_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView messagesList = (ListView) findViewById(R.id.messages_list);


        setTitle("My Messages");



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        command = new ListMessagesCommand(LocMessManager.getInstance().getToken(), "");

        //Run in Manager and catch errors
        LocMessManager.getInstance().executeAsync(command, new LocMessManager.CompleteCallback() {
            @Override
            public void OnComplete(boolean result, String message) {
                if(result){
                    Toast.makeText(MyMessagesMenuActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    populateData();
                } else {
                    Toast.makeText(MyMessagesMenuActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void  populateData(){
        try {
            //Parse received messages
            myMessages = command.getResults();
        }catch (Exception e){
            //Still not receiving the messages? I need to fix this...
            Log.e("MESSAGES", "Request success but we do not receive any messages");
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

    public void newMessage(View v){
        Intent i = new Intent(this, NewMessageActivity.class);
        startActivity(i);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Actully do Nothing
        } else if (id == R.id.nav_my_messages) {

        } else if (id == R.id.nav_help) {
            Toast.makeText(getApplication().getBaseContext(),"No help lel",Toast.LENGTH_SHORT).show();


        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }
}
