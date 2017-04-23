package pt.ulisboa.tecnico.ist.cmu.locmess.commands;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import pt.ulisboa.tecnico.ist.cmu.locmess.exception.DuplicateExecutionException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.CommandNotExecutedException;

/**
 * Created by jorge on 03/04/17.
 */

public class ListKeysCommand extends AbstractCommand {


    private static final String _endpoint="listKeys";
    private HashMap<String,String> _results=null;

    public ListKeysCommand(String token) {
        super(_endpoint,"token="+token);
    }

    @Override
    public void execute() throws IOException, DuplicateExecutionException {
        super.execute();
    }

    public Map<String,String> getResults() throws IOException, DuplicateExecutionException, JSONException, CommandNotExecutedException {
        if(_results==null){
            JSONObject obj=new JSONObject(getResponse());
            JSONArray arr=obj.getJSONArray("keys");
            _results=new HashMap<String,String>();
            for(int i=0; i<arr.length(); i++){
                _results.put(arr.getJSONObject(i).getString("key"),arr.getJSONObject(i).getString("value"));
            }
        }
        return _results;
    }
}
