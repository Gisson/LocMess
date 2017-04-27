package pt.ulisboa.tecnico.ist.cmu.locmess.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import pt.ulisboa.tecnico.ist.cmu.locmess.R;

/**
 * Created by nuno on 26/03/17.
 * @brief adapts a List of Topics to be displayed on a ListView
 */

public class TopicsListAdapter extends BaseAdapter {
    protected static final String TAG = "TopicsListAdapter";
    private List<String> topics;
    private Activity activity;
    private NewItemCallback newItemCallback = null;

    public TopicsListAdapter(List<String> topics, Activity parentActivity) {
        this(topics, parentActivity, null);
    }

    public TopicsListAdapter(List<String> topics, Activity parentActivity, NewItemCallback newItemCallback) {
        this.topics = Collections.synchronizedList(topics);
        this.activity = parentActivity;
        this.newItemCallback = newItemCallback;
    }

    @Override
    public int getCount() {
        return topics.size();
    }

    @Override
    public String getItem(int index) {
        return topics.get(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    /**
     * @brief returns a view to display the topic at the given index in the list
     */
    @Override
    public View getView(final int index, View reusableView, ViewGroup viewGroup) {
        final ViewHolder holder;
        if(reusableView == null) {
            // create a new view
            holder = new ViewHolder();
            LayoutInflater inflater = activity.getLayoutInflater();
            reusableView = inflater.inflate(android.R.layout.simple_list_item_1, null);
            holder.text = (TextView) reusableView.findViewById(android.R.id.text1);
            reusableView.setTag(holder);
        } else {
            // an old view can be reused!
            holder = (ViewHolder) reusableView.getTag();
        }
        holder.index = index;

        holder.text.setText(topics.get(index));
        reusableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ViewHolder holder = (ViewHolder) view.getTag();
                showEditTopicDialog(holder.index, "Edit filter topic");
            }
        });
        return reusableView;
    }

    @Override
    public int getItemViewType(int index) {
        return 0; // all our items are of the same view
    }

    @Override
    public int getViewTypeCount() {
        return 1; // all our items are of the same view
    }

    public interface NewItemCallback {
        /** indicates that the item at index was just created/edited */
        void onNewItem(int index);
    };

    /**
     * @brief creates and shows a dialog to edit the topic at the given index
     */
    public void showEditTopicDialog(final int index, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);

        final LayoutInflater inflater = activity.getLayoutInflater();
        final View view1 = inflater.inflate(R.layout.dialog_edit_topic, null);
        final EditText dialogText = (EditText)view1.findViewById(R.id.editText1);
        dialogText.setText(topics.get(index));

        builder.setView(view1);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String text = dialogText.getText().toString();
                if(text.replace("=", "").trim().isEmpty()){ // FIXME this should be abstracted by a Topic class or something
                    topics.remove(index);
                } else {
                    topics.set(index, text);
                    if(newItemCallback != null)
                        newItemCallback.onNewItem(index);
                }
                TopicsListAdapter.this.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String text = dialogText.getText().toString();
                // if the dialog was shown for a new item, it might be empty
                if(text.isEmpty()){ // FIXME this should be abstracted by a Topic class or something
                    topics.remove(index);
                    TopicsListAdapter.this.notifyDataSetChanged();
                }
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d(TAG, "DialogInterface onCancel");
                String text = dialogText.getText().toString();
                // if the dialog was shown for a new item, it might be empty
                if(text.isEmpty()){ // FIXME this should be abstracted by a Topic class or something
                    topics.remove(index);
                    TopicsListAdapter.this.notifyDataSetChanged();
                }
            }
        });
        builder.show();
    }

    public void showAddTopicDialog() {
        int index = topics.size();
        // explicitly append to the end of the list
        topics.add(index, "");
        showEditTopicDialog(index, "Add filter topic");
    }

    private class ViewHolder {
        TextView text;
        int index;
    }

}
