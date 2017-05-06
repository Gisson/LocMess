package pt.ulisboa.tecnico.ist.cmu.locmess.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.ist.cmu.locmess.R;
import pt.ulisboa.tecnico.ist.cmu.locmess.dto.LocationDto;

/**
 * Created by jorge on 05/05/17.
 */

public class LocationsListAdapter extends BaseAdapter {

    private List<LocationDto> _locations;
    private Activity _activity;

    public LocationsListAdapter(List<LocationDto> locations,Activity parentActivity){
        _locations=locations;
        _activity=parentActivity;
    }

    @Deprecated
    public LocationsListAdapter(Activity parentActivity){
        _locations=new ArrayList<>();
        _activity=parentActivity;
    }

    @Override
    public int getCount() {
        return _locations.size();
    }

    @Override
    public LocationDto getItem(int i) {
        return _locations.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
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

        holder.text.setText(_locations.get(index).getName());
        if(_activity.getIntent().getStringExtra("chooseLocation")!=null){
            Toast.makeText(_activity,"This is to choose",Toast.LENGTH_SHORT).show();

            reusableView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent returnItent = new Intent();
                    returnItent.putExtra("locationChoice",getItem(index).getName());
                    _activity.setResult(_activity.RESULT_OK,returnItent);
                    _activity.finish();
                }
            });
        }
        else {
            Toast.makeText(_activity,"This is not to choose",Toast.LENGTH_SHORT).show();

            reusableView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final ViewHolder holder = (ViewHolder) view.getTag();
                    showLocation(holder.index, getItem(holder.index).getName());
                }
            });
        }
        return reusableView;
    }

    private class ViewHolder {
        TextView text;
        int index;
    }

    public void showLocation(final int index, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
        builder.setTitle(title);

        final LayoutInflater inflater = _activity.getLayoutInflater();
        final View view1 = inflater.inflate(R.layout.dialog_location_info, null);
        final TextView dialogText = (TextView) view1.findViewById(R.id.textView);
        dialogText.setText("Latitude: "+_locations.get(index).getLat()+"\nLongitude: "+_locations.get(index).getLongitude()
                +"\nRadius: "+_locations.get(index).getRadius()+"\nSSids: "+
                _locations.get(index).getWifiIdsAsString());

        builder.setView(view1);
        builder.show();

    }
}
