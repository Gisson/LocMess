package pt.ulisboa.tecnico.ist.cmu.locmess;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationListener;
import android.net.wifi.WifiManager;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pt.ulisboa.tecnico.ist.cmu.locmess.Handlers.IncomingMessageHandler;
import pt.ulisboa.tecnico.ist.cmu.locmess.LocMessActivityInterfaces.LocMessLocationNeedyActivity;
import pt.ulisboa.tecnico.ist.cmu.locmess.adapters.MessageListAdapter;
import pt.ulisboa.tecnico.ist.cmu.locmess.commands.ListKeysCommand;
import pt.ulisboa.tecnico.ist.cmu.locmess.commands.ListMessagesCommand;
import pt.ulisboa.tecnico.ist.cmu.locmess.dto.MessageDto;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.CommandNotExecutedException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.DuplicateExecutionException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.LocMessHttpException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.NoNetworksAroundException;
import pt.ulisboa.tecnico.ist.cmu.locmess.services.MessageNotificationService;
import pt.ulisboa.tecnico.ist.cmu.locmess.wifiDirect.WifiP2PHandler;

public class MyMessagesMenuActivity extends LocMessLocationNeedyActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private ArrayList<String> messages;
    private MessageListAdapter messagesAdapter;
    private static final String TAG = "MyMessagesActivity";
    private String _locationName="";

    private ListMessagesCommand command;

    private Map<String, HashMap<String, String>> myMessages = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_messages_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //START SECTION TO MOVE TO MESSAGES ACTIVITY
        if(LocMessManager.getInstance().getWifiHandler() == null) {
            LocMessManager.getInstance().setWifiHandler(new WifiP2PHandler(this, new IntentFilter()));
            WifiP2PHandler _wifiHandler = LocMessManager.getInstance().getWifiHandler();
            _wifiHandler.wifiOn();
        }
        if(LocMessManager.getInstance().getMessageHandler()==null) {
            LocMessManager.getInstance().setMessageHandler(new IncomingMessageHandler(this));
            IncomingMessageHandler _messageHandler = LocMessManager.getInstance().getMessageHandler();
            _messageHandler.doBindService();
        }
            //(new ListKeysCommand(LocMessManager.getInstance().getToken())).execute();
        final ListKeysCommand auxComm = new ListKeysCommand(LocMessManager.getInstance().getToken());
            LocMessManager.getInstance().executeAsync(auxComm, new LocMessManager.CompleteCallback() {
                @Override
                public void OnPreExecute(){}

                @Override
                public void OnComplete(boolean result, String message) {
                    if (result) {
                        try {
                            LocMessManager.getInstance().setUserTopics(auxComm.getResultsDto());
                        } catch (CommandNotExecutedException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            Log.d(TAG,""+LocMessManager.getInstance().getUserTopics());


        //END SECTION TO MOVE TO MESSAGES ACTIVITY

//        LocMessManager.getInstance()._batteryReceiver = new BatteryScannerReceiver();
 //       registerReceiver(LocMessManager.getInstance()._batteryReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));


        try {
            _locationName = LocMessManager.getInstance().findLocation(getLastLocation(),
                    LocMessManager.getInstance().getClosestSsid(getWifiManager()).SSID);
        }catch(NoNetworksAroundException e){
            _locationName=LocMessManager.getInstance().findLocation(getLastLocation());
            e.getMessage();
        }
        if( getIntent().getBooleanExtra("nearby",false)) {
            if(_locationName.equals("")){
                setTitle("Not in a known location");
            }
            else {
                setTitle("Messages in " + _locationName);
            }
        }else{
            setTitle("My Messages");
        }





        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_messages);
        navigationView.setNavigationItemSelectedListener(this);


        NavigationView nv = (NavigationView) findViewById(R.id.nav_view_messages);
        View v = nv.getHeaderView(0);
        TextView tvUsername = (TextView) v.findViewById(R.id.user_name);
        tvUsername.setText(LocMessManager.getInstance().getUsername());
        if(!_locationName.equals("")) {
            Log.d(TAG, "" + getIntent().getBooleanExtra("nearby", false));
            if (getIntent().getBooleanExtra("nearby", false)) {
                command = new ListMessagesCommand(LocMessManager.getInstance().getToken(),
                        _locationName);
                Log.d(TAG, "Inside if");
            } else {
                command = new ListMessagesCommand(LocMessManager.getInstance().getToken());
                Log.d(TAG, "Inside else");
            }

            //Run in Manager and catch errors
            LocMessManager.getInstance().executeAsync(command, new LocMessManager.CompleteCallback() {
                @Override
                public void OnPreExecute(){}

                @Override
                public void OnComplete(boolean result, String message) {
                    if (result) {
                        Toast.makeText(MyMessagesMenuActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        populateData();
                    } else {
                        Toast.makeText(MyMessagesMenuActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void  populateData(){
        ArrayList<MessageDto> messages = new ArrayList<>();
        messagesAdapter = new MessageListAdapter(messages, this);
        try {
            //Parse received messages

            Map<String, MessageDto> bla = command.getResults();
            for (MessageDto m : command.getResults().values()) {
                messages.add(m);
            }
            messagesAdapter.notifyDataSetChanged();
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
        }finally{
            for(MessageDto messageDto : LocMessManager.getInstance().getMyMessages()){
                messages.add(messageDto);
            }
            messagesAdapter.notifyDataSetChanged();
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
            Intent i = new Intent(getApplicationContext(), MyMessagesMenuActivity.class);
            startActivity(i);

        }else if (id == R.id.nav_messages_nearbu){
            Intent i = new Intent(getApplicationContext(), MyMessagesMenuActivity.class);
            i.putExtra("nearby",true);
            startActivity(i);
        } else if (id == R.id.nav_locations) {
            Intent i = new Intent(getApplicationContext(), LocationsMenuActivity.class);
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
        if( getIntent().getBooleanExtra("nearby",false)) {
            command = new ListMessagesCommand(LocMessManager.getInstance().getToken(),_locationName);
        }else{
            command = new ListMessagesCommand(LocMessManager.getInstance().getToken());
        }
        //Run in Manager and catch errors
        LocMessManager.getInstance().executeAsync(command, new LocMessManager.CompleteCallback() {

            @Override
            public void OnPreExecute(){}
            @Override
            public void OnComplete(boolean result, String message) {
                if(result){
                    //Toast.makeText(MyMessagesMenuActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    populateData();
                } else {
                    Toast.makeText(MyMessagesMenuActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDestroy(){
        LocMessManager.getInstance().getMessageHandler().doUnbindService();
        super.onDestroy();

    }



}
