package pt.ulisboa.tecnico.ist.cmu.locmess.commands;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import pt.ulisboa.tecnico.ist.cmu.locmess.JsonParser;
import pt.ulisboa.tecnico.ist.cmu.locmess.dto.TopicDto;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.CommandNotExecutedException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.DuplicateExecutionException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.NoLocationException;

/**
 * Created by jorge on 10/05/17.
 */

public class GetLocationCommand extends AbstractCommand {

    private static final String _endpoint = "getLocation";


    public GetLocationCommand(String token,String latitude, String longitude) {
        super(_endpoint,"token="+token+"&latitude="+latitude+"&longitude="+longitude);
    }
    public GetLocationCommand(String token,String ssid){
        super(_endpoint,"token="+token+"&ssid="+ssid);
    }

    public GetLocationCommand(String token, String latitude, String longitude, String ssid){
        super(_endpoint,"token="+token+"&latitude="+latitude+"&longitude="+longitude+"&ssid="+ssid);
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

    public String getResult() throws CommandNotExecutedException, NoLocationException, JSONException {
        try {
            return JsonParser.getValue(super.getResponse(),"location");
        } catch (JSONException e) {
            throw new NoLocationException(JsonParser.getValue(super.getResponse(),"reason"));
        }
    }

    private static String getSSidString(List<String> ssids){
        String result="";
        for(int i=0;i<ssids.size()-1;i++){
            result+=ssids.get(i);
        }
        return result+ssids.get(ssids.size()-1);
    }

}
