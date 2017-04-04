package pt.ulisboa.tecnico.ist.cmu.locmess.commands;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import pt.ulisboa.tecnico.ist.cmu.locmess.JsonParser;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.AlreadyRequestedException;

/**
 * Created by jorge on 03/04/17.
 */

public class ReadMessageCommand extends AbstractCommand {


    private static final String _endpoint="readMessage";
    private HashMap<String,String> _results=null;

    public ReadMessageCommand(String token,String location, String id) {
        super(_endpoint,"token="+token+"&location="+location+"&messageId="+id);
    }

    @Override
    public void execute() throws IOException, AlreadyRequestedException {
        super.execute();
    }

    public HashMap<String,String> getResult() throws IOException, AlreadyRequestedException, JSONException {
        if(_results==null){
            _results=new HashMap<String,String>();
            _results.put("Id", JsonParser.getValue(super.getResponse(),"Id"));
            _results.put("Author", JsonParser.getValue(super.getResponse(),"Author"));
            _results.put("Content", JsonParser.getValue(super.getResponse(),"Content"));
        }
        return _results;
    }
}
