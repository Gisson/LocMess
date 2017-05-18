package pt.ulisboa.tecnico.ist.cmu.locmess;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pt.ulisboa.tecnico.ist.cmu.locmess.adapters.TopicsListAdapter;

public class EditPolicyActivity extends AppCompatActivity {
    private static final String TAG = "EditPolicyActivity";

    /* FIXME implement a Topic DTO :) */
    private static final String[] mockTopics = {"os=linux", "university=ist", "club=none"};
    private ArrayList<String> topics = new ArrayList<String>(Arrays.asList(mockTopics));
    private TopicsListAdapter topicsAdapter;
    private String policyChoice="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_policy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.abAddTopic);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "Action Button clicked");
                topicsAdapter.showAddTopicDialog();
            }
        });

        topicsAdapter = new TopicsListAdapter(topics, this);

        ListView lvTopics = (ListView)findViewById(R.id.lvTopics);
        lvTopics.setAdapter(topicsAdapter);
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rbBlackList:
                Log.v(TAG, "Black " + checked); // FIXME remove this
                policyChoice="Black";
                    break;
            case R.id.rbWhiteList:
                Log.v(TAG, "White " + checked); // FIXME remove this
                policyChoice="White";
                    break;
        }
    }

    @Override
    public void onBackPressed(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("policyType",(policyChoice+"list").toLowerCase());
        returnIntent.putExtra("topics",topics);
        setResult(RESULT_OK,returnIntent);
        finish();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
}
