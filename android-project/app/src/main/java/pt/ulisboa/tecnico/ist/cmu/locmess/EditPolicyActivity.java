package pt.ulisboa.tecnico.ist.cmu.locmess;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;

public class EditPolicyActivity extends AppCompatActivity {
    private static final String TAG = "EditPolicyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_policy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.abAddTopic);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Adding a new policy is not yet implemented", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        /* FIXME implement a Topic DTO :) */
        String[] topics = {"os=linux", "university=ist", "club=none"};
        ArrayAdapter<String> topicsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, topics);

        ListView lvTopics = (ListView)findViewById(R.id.lvTopics);
        lvTopics.setAdapter(topicsAdapter);
    }


    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rbBlackList:
                Log.v(TAG, "Black " + checked); // FIXME remove this
                    break;
            case R.id.rbWhiteList:
                Log.v(TAG, "White " + checked); // FIXME remove this
                    break;
        }
    }
}
