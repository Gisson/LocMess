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

public class ListMessagesCommand extends AbstractCommand {

    //TODO

    private static final String _endpoint="listMessages";
    private HashMap<String,HashMap<String,String>> _results=null;

    public ListMessagesCommand(String token, String location) {
        super(_endpoint,"token="+token+"&location="+location);
    }

    @Override
    public void execute() throws IOException, DuplicateExecutionException {
        super.execute();
    }

    public Map<String,HashMap<String,String>> getResults() throws IOException, DuplicateExecutionException, JSONException, CommandNotExecutedException {
        if(_results==null){
            JSONObject obj=new JSONObject(getResponse());
            JSONArray arr=obj.getJSONArray("messages");
            _results=new HashMap<String,HashMap<String,String>>();
            HashMap<String,String> references;
            for(int i=0; i<arr.length(); i++){
                references=new HashMap<String,String>();
                references.put("Author",arr.getJSONObject(i).getJSONObject("references").getString("Author"));
                references.put("Content",arr.getJSONObject(i).getJSONObject("references").getString("Content"));
                _results.put(references.put("Id",arr.getJSONObject(i).getJSONObject("messages").getString("Id")),references);
            }
        }
        return _results;
    }
}
