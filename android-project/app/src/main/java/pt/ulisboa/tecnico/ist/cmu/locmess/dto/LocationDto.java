package pt.ulisboa.tecnico.ist.cmu.locmess.dto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorge on 05/05/17.
 */

public class LocationDto implements LocMessDto {

    private String _name;
    private String _radius="";
    private List<String> _wifiIds=null;
    private GpsPointDto _center;

    public static class JsonAtributes{
        public static final String NAME="name";
        public static final String LATITUDE="latitude";
        public static final String LONGITUDE="longitude";
        public static final String WIFIIDS="wifiids";

    }

    @Deprecated
    private String wifiids;

    public LocationDto(String name, String lat, String longitude, String radius){
        _name=name;
        _center=new GpsPointDto(Double.parseDouble(lat),Double.parseDouble(longitude));
        _radius=radius;
    }

    public LocationDto(String name, List<String> wifiIds){
        _name=name;
        _wifiIds=wifiIds;
        _center=new GpsPointDto();
    }

    @Deprecated
    public LocationDto(String name, String wifi){
        _name=name;
        _wifiIds=new ArrayList<>();
        wifiids=wifi;
        _center=new GpsPointDto();

    }


    public String getName() {
        return _name;
    }

    public String getLat() {
        return ""+_center.getLatitude();
    }

    public String getLongitude() {
        return ""+_center.getLongitude();
    }

    public String getRadius() {
        return _radius;
    }

    public List<String> getWifiIds() {
        return _wifiIds;
    }

    public String getWifiIdsAsString(){
        if(_wifiIds!=null){
            String ids="";
            for(String s: _wifiIds){
                ids+=s;
            }
            return ids;
        }
        return "None";
    }

    @Deprecated
    public String getWifiids() {
        return wifiids;
    }

    public String toJson(){

        return getJsonObject().toString();
    }

    public JSONObject getJsonObject(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JsonAtributes.NAME, _name);
            jsonObject.put(JsonAtributes.LATITUDE, getLat());
            jsonObject.put(JsonAtributes.LONGITUDE, getLongitude());
            JSONArray arr = new JSONArray();
            for(String wifiid : _wifiIds) {
                arr.put(wifiid);
            }
            jsonObject.put(JsonAtributes.WIFIIDS,arr);
        } catch (JSONException e) {
            e.toString();
        }
        return jsonObject;
    }
}
