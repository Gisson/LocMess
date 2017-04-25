package pt.ulisboa.tecnico.ist.cmu.locmess.commands;

import org.json.JSONException;

import java.io.IOException;

import pt.ulisboa.tecnico.ist.cmu.locmess.JsonParser;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.DuplicateExecutionException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.CommandNotExecutedException;

/**
 * Created by jorge on 03/04/17.
 */

public class AddLocationCommand extends AbstractCommand {

    private static final String _endpoint="addLocation";
    private String _response=null;

    //This big line of code is just due to java being obligated to use super as first line of code of a constructor.
    public AddLocationCommand(String token, String name,String latitude,String longitude,String radius,String ssid,String bssid) {
        super(_endpoint,"token="+token+"name="+name+((hasGpsCoordinates(latitude,longitude,radius)) ?  //Everything starts with name, if latitude, longitude and radius are defined
                "&latitude="+latitude+"&longitude="+longitude+"&radius="+radius:        // Use longitude, latitude and radius
                (hasBssid(bssid))?"" +                                                  // else check if bssid is defined
                        "&bssid="+bssid:                                                // if it is then use it
                        "&ssid="+ssid));                                                // last case scenario, the ssid MUST be defined
    }

    @Override
    public void execute() throws IOException, DuplicateExecutionException {
        super.execute();
    }

    private static boolean hasGpsCoordinates(String latitude,String longitude,String radius){
        return !(latitude.equals("") && longitude.equals("") && radius.equals(""));
    }
    private static boolean hasBssid(String bssid){
        return !bssid.equals(bssid);
    }
    private static boolean hasSsid(String ssid){
        return !ssid.equals(ssid);
    }

    public boolean successfulRequest() throws IOException, DuplicateExecutionException, JSONException, CommandNotExecutedException {
            return JsonParser.getValue(super.getResponse(),"response").equals("success");
    }
}
