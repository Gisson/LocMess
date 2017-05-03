package pt.ulisboa.tecnico.ist.cmu.locmess;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pt.ulisboa.tecnico.ist.cmu.locmess.adapters.MessageListAdapter;
import pt.ulisboa.tecnico.ist.cmu.locmess.commands.ListMessagesCommand;
import pt.ulisboa.tecnico.ist.cmu.locmess.dto.MessageDto;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.LocMessHttpException;

public class MyMessagesMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private ArrayList<String> messages;
    private MessageListAdapter messagesAdapter;
    private static final String TAG = "MyMessagesActivity";

    private ListMessagesCommand command;

    private Map<String, HashMap<String, String>> myMessages = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_messages_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);






        setTitle("My Messages");



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        command = new ListMessagesCommand(LocMessManager.getInstance().getToken());

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
            ArrayList<MessageDto> messages = new ArrayList<>();
            Map<String, MessageDto> bla = command.getResults();
            for (MessageDto m : command.getResults().values()) {
                messages.add(m);
            }
            messagesAdapter = new MessageListAdapter(messages, this);
            ListView messagesList = (ListView) findViewById(R.id.messages_list);
            messagesList.setAdapter(messagesAdapter);
            ((FloatingActionButton) findViewById(R.id.add_message)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v(TAG, "Action Button clicked");
                    startActivity(new Intent(getApplicationContext(),NewMessageActivity.class));
                }
            });
            ((FloatingActionButton) findViewById(R.id.add_message)).setEnabled(true);
        }catch( LocMessHttpException e) {
            Toast.makeText(MyMessagesMenuActivity.this,e.getReason(),Toast.LENGTH_SHORT);
        }catch (Exception e){
            //Still not receiving the messages? I need to fix this...
            Log.e("MESSAGES", "Request success but we do not receive any messages");
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
            Intent i=new Intent(getApplicationContext(),MyProfile.class);
            startActivity(i);

        } else if (id == R.id.nav_my_messages) {
            Intent i=new Intent(getApplicationContext(),MyMessagesMenuActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_help) {
            Toast.makeText(getApplication().getBaseContext(),"No help lel",Toast.LENGTH_SHORT).show();


        } else if (id == R.id.nav_logout) {
            //TODO
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onResume(){
        super.onResume();
        command = new ListMessagesCommand(LocMessManager.getInstance().getToken());

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


}
