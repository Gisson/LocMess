package pt.ulisboa.tecnico.ist.cmu.locmess.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.ist.cmu.locmess.R;
import pt.ulisboa.tecnico.ist.cmu.locmess.dto.MessageDto;

/**
 * Created by ist179042 on 20/04/17.
 */

public class MessageListAdapter extends BaseAdapter {

    private List<MessageDto> _messages;
    private Activity _activity;

    public MessageListAdapter(List<MessageDto> messages, Activity parentActivity) {
        _messages = messages;
        _activity = parentActivity;
    }


    public MessageListAdapter(MessageDto message, Activity parentActivity) {
        _messages = new ArrayList<MessageDto>();
        _messages.add(message);
        _activity = parentActivity;
    }

    public MessageListAdapter(Activity parentActivity) {
        _messages = new ArrayList<MessageDto>();
        _activity = parentActivity;
    }


    @Override
    public int getCount() {
        return _messages.size();
    }

    @Override
    public MessageDto getItem(int position) {
        return _messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int index, View reusableView, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (reusableView == null) {
            // create a new view
            holder = new ViewHolder();
            LayoutInflater inflater = _activity.getLayoutInflater();
            reusableView = inflater.inflate(android.R.layout.simple_list_item_1, null);
            holder.text = (TextView) reusableView.findViewById(android.R.id.text1);
            reusableView.setTag(holder);
        } else {
            // an old view can be reused!
            holder = (ViewHolder) reusableView.getTag();
        }
        holder.index = index;

        holder.text.setText(_messages.get(index).getTitle());
        reusableView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ViewHolder holder = (ViewHolder) view.getTag();
                showEditTopicDialog(holder.index, getItem(holder.index).getTitle());
            }
        });
        return reusableView;
    }

    private class ViewHolder {
        TextView text;
        int index;
    }

    public void showEditTopicDialog(final int index, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
        builder.setTitle(title);

        final LayoutInflater inflater = _activity.getLayoutInflater();
        final View view1 = inflater.inflate(R.layout.dialog_read_message, null);
        final TextView dialogText = (TextView) view1.findViewById(R.id.textView);
        dialogText.setText("Author: "+_messages.get(index).getAuthor()+"\nContent: "+_messages.get(index).getMessage()
                            +"\nLocation: "+_messages.get(index).getLocation()+"\nTopics: "+
                            _messages.get(index).getPolicy().getTopics().toString()+"\nType: "+
                            _messages.get(index).getPolicy().getType());

        builder.setView(view1);
        builder.show();

    }
}
