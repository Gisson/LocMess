package pt.ulisboa.tecnico.ist.cmu.locmess.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorge on 05/05/17.
 */

public class LocationDto implements LocMessDto {

    private String _name;
    private String _lat="",_longitude="",_radius="";
    private List<String> _wifiIds=null;

    @Deprecated
    private String wifiids;

    public LocationDto(String name, String lat, String longitude, String radius){
        _name=name;
        _lat=lat;
        _longitude=longitude;
        _radius=radius;
    }

    public LocationDto(String name, List<String> wifiIds){
        _name=name;
        _wifiIds=wifiIds;
    }

    @Deprecated
    public LocationDto(String name, String wifi){
        _name=name;
        _wifiIds=new ArrayList<>();
        wifiids=wifi;
    }


    public String getName() {
        return _name;
    }

    public String getLat() {
        return _lat;
    }

    public String getLongitude() {
        return _longitude;
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
}
