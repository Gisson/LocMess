package pt.ulisboa.tecnico.ist.cmu.locmess.commands;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import pt.ulisboa.tecnico.ist.cmu.locmess.dto.MessageDto;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.DuplicateExecutionException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.CommandNotExecutedException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.LocMessHttpException;

/**
 * Created by jorge on 03/04/17.
 */

public class ListMessagesCommand extends AbstractCommand {

    //TODO

    private static final String _endpoint="listMessages";
    private HashMap<String,MessageDto> _results=null;

    public ListMessagesCommand(String token, String location) {
        super(_endpoint,"token="+token+"&location="+location);
    }
    public ListMessagesCommand(String token){
        super(_endpoint,"token="+token);
    }

    @Override
    public void execute() throws IOException, DuplicateExecutionException {
        super.execute();
    }

    public Map<String,MessageDto> getResults() throws IOException, LocMessHttpException, JSONException {
        if(!successfulRequest()){
            throw new LocMessHttpException(getReason());
        }
        if(_results==null){
            JSONObject obj=new JSONObject(getResponse());
            JSONArray arr=obj.getJSONArray("messages");
            _results=new HashMap<String,MessageDto>();
            for(int i=0; i<arr.length(); i++){
                _results.put(arr.getJSONObject(i).getString("Id"),
                        new MessageDto(arr.getJSONObject(i).getString("Author"),
                                arr.getJSONObject(i).getString("Content"),
                                arr.getJSONObject(i).getString("Title"),
                                arr.getJSONObject(i).getString("Location")
                                ));
            }
        }
        return _results;
    }
}
