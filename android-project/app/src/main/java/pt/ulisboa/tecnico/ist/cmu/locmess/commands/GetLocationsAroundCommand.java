package pt.ulisboa.tecnico.ist.cmu.locmess.commands;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.ist.cmu.locmess.JsonParser;
import pt.ulisboa.tecnico.ist.cmu.locmess.dto.LocationDto;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.CommandNotExecutedException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.DuplicateExecutionException;

/**
 * Created by jorge on 17/05/17.
 */

public class GetLocationsAroundCommand extends AbstractCommand {

    private static final String _endpoint = "getLocationsAround";

    public GetLocationsAroundCommand(String latitude, String longitude,String radius){
        super(_endpoint,"latitude="+latitude+"&longitude="+longitude+"&radius="+radius);
    }

    public GetLocationsAroundCommand(String locationName){
        super(_endpoint,"location="+locationName);
    }
    @Override
    public void execute() throws IOException, DuplicateExecutionException {
        super.execute();
    }

    public boolean successfulRequest() throws IOException, DuplicateExecutionException, JSONException, CommandNotExecutedException {
        return JsonParser.getValue(super.getResponse(),"response").equals("success");
    }

    public String getReason() throws CommandNotExecutedException, JSONException {
        return JsonParser.getValue(super.getResponse(),"reason");
    }

    public List<LocationDto> getResults(){
        List<LocationDto> results=new ArrayList<>();
        try {
            JSONObject response = new JSONObject(getResponse());
            JSONArray locationsArr = response.getJSONArray("locations");
            for(int i=0;i<locationsArr.length();i++){
                JSONObject location = locationsArr.getJSONObject(i);
                results.add(new LocationDto(location.getString("name"),
                            location.getString("latitude"),location.getString("longitude"),
                            location.getString("radius")));
            }
        } catch (JSONException | CommandNotExecutedException e) {
            e.printStackTrace();
        }
        return results;
    }

}
