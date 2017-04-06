package pt.ulisboa.tecnico.ist.cmu.locmess.commands;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import pt.ulisboa.tecnico.ist.cmu.locmess.exception.AlreadyRequestedException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.NotYetRequestedException;

/**
 * Created by jorge on 03/04/17.
 */

public class ListLocationsCommand extends AbstractCommand {

    private static final String _endpoint="listLocations";

    private HashMap<String,HashMap<String,String>> _results=null;

    public ListLocationsCommand(String token) {
        super(_endpoint,"token="+token);
    }

    @Override
    public void execute() throws IOException, AlreadyRequestedException {
        super.execute();
    }

    //DONT GET SCARED!!! READ THE COMMENTS
    public Map<String,HashMap<String,String>> getResults() throws IOException, AlreadyRequestedException, JSONException, NotYetRequestedException {
        if(_results==null){
            JSONObject obj=new JSONObject(getResponse());
            JSONArray arr=obj.getJSONArray("locations");
            HashMap<String,String> references;
            for(int i=0; i<arr.length(); i++){
                references=new HashMap<String,String>();
                references.put("latitude",arr.getJSONObject(i).getJSONObject("references").getString("latitude")); //This basically puts everything that came from the json into a map
                references.put("longitude",arr.getJSONObject(i).getJSONObject("references").getString("longitude")); //Yes it could be a dictionary but idc
                references.put("radius",arr.getJSONObject(i).getJSONObject("references").getString("radius"));
                references.put("bssids",arr.getJSONObject(i).getJSONObject("references").getString("bssids"));
                references.put("ssids",arr.getJSONObject(i).getJSONObject("references").getString("ssids"));
                _results.put(arr.getJSONObject(i).getString("name"),references);
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
