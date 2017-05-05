package pt.ulisboa.tecnico.ist.cmu.locmess.commands;

import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.ulisboa.tecnico.ist.cmu.locmess.dto.LocationDto;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.DuplicateExecutionException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.CommandNotExecutedException;

/**
 * Created by jorge on 03/04/17.
 */

public class ListLocationsCommand extends AbstractCommand {

    private static final String _endpoint="listLocations";

    private List<LocationDto> _results=null;

    public ListLocationsCommand(String token) {
        super(_endpoint,"token="+token);
    }

    @Override
    public void execute() throws IOException, DuplicateExecutionException {
        super.execute();
    }

    //DONT GET SCARED!!! READ THE COMMENTS
    public List<LocationDto> getResults() throws IOException, DuplicateExecutionException, JSONException, CommandNotExecutedException {
        if(_results==null){
            JSONObject obj=new JSONObject(getResponse());
            JSONArray arr=obj.getJSONArray("locations");
            _results=new ArrayList<>();
            for(int i=0; i<arr.length(); i++){
                _results.add(new LocationDto(arr.getJSONObject(i).getJSONObject("references").getString("name"),
                        arr.getJSONObject(i).getJSONObject("references").getString("latitude"),
                        arr.getJSONObject(i).getJSONObject("references").getString("longitude"),
                        arr.getJSONObject(i).getJSONObject("references").getString("radius")));
            }
        }
        return _results;
    }

    private boolean hasGpsCoordinates(String latitude,String longitude,String radius){
        return !(latitude.equals("") && longitude.equals("") && radius.equals(""));
    }
    private boolean hasBssid(String bssid){
        return !bssid.equals(bssid);
    }
    private boolean hasSsid(String ssid){
        return !ssid.equals(ssid);
    }
}
