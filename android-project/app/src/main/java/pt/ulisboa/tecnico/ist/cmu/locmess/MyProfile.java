package pt.ulisboa.tecnico.ist.cmu.locmess;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pt.ulisboa.tecnico.ist.cmu.locmess.adapters.TopicsListAdapter;
import pt.ulisboa.tecnico.ist.cmu.locmess.commands.AddKeyCommand;
import pt.ulisboa.tecnico.ist.cmu.locmess.commands.ListKeysCommand;
import pt.ulisboa.tecnico.ist.cmu.locmess.dto.TopicDto;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.CommandNotExecutedException;

public class MyProfile extends AppCompatActivity {
    private static final String TAG = "MyProfileActivity";
    private static final String[] mockTopics = {};//{"os=linux", "university=ist", "club=none"};
    private ArrayList<String> topics = new ArrayList<String>(Arrays.asList(mockTopics));
    private TopicsListAdapter topicsAdapter;
    private LocMessManager manager = LocMessManager.getInstance();
    private FloatingActionButton fab;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab = (FloatingActionButton) findViewById(R.id.abAddTopic);
        fab.setEnabled(false);

        TextView tvUsername = (TextView) findViewById(R.id.username);
        tvUsername.setText(manager.getUsername());

        final ListKeysCommand request = new ListKeysCommand(manager.getToken());
        manager.executeAsync(request, new LocMessManager.CompleteCallback() {
            @Override
            public void OnComplete(final boolean result, String message) {
                if(result) {
                    try {
                        List<TopicDto> dtos = request.getResultsDto();
                        for(TopicDto t : dtos) {
                            topics.add(t.toString());
                        }
                    } catch (CommandNotExecutedException | JSONException e) {
                        e.printStackTrace();
                    }
                    displayTopics();
                } else {
                    Toast.makeText(MyProfile.this, message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void displayTopics() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "Action Button clicked");
                topicsAdapter.showAddTopicDialog();
            }
        });
        fab.setEnabled(true);

        TopicsListAdapter.NewItemCallback newItemCallback = new TopicsListAdapter.NewItemCallback() {
            @Override
            public void onNewItem(final int index) {
                String token = manager.getToken();
                String text = topics.get(index);
                AddKeyCommand request = new AddKeyCommand(token, new TopicDto(text));
                manager.executeAsync(request, new LocMessManager.CompleteCallback() {
                    @Override
                    public void OnComplete(final boolean result, String message) {
                        int duration = Toast.LENGTH_SHORT;
                        if(result){
                            message = "topic added";
                        } else {
                            topics.remove(index); // FIXME the index may have changed!
                            topicsAdapter.notifyDataSetChanged();
                            duration = Toast.LENGTH_LONG;
                        }
                        Toast.makeText(MyProfile.this, message, duration).show();
                    }
                });
            }
        };
        topicsAdapter = new TopicsListAdapter(topics, this, newItemCallback);

        ListView lvTopics = (ListView)findViewById(R.id.lvTopics);
        lvTopics.setAdapter(topicsAdapter);
    }

}
