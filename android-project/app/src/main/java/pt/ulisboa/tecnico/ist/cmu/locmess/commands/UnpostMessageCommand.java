package pt.ulisboa.tecnico.ist.cmu.locmess.commands;

import org.json.JSONException;

import java.io.IOException;

import pt.ulisboa.tecnico.ist.cmu.locmess.JsonParser;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.DuplicateExecutionException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.CommandNotExecutedException;

/**
 * Created by jorge on 03/04/17.
 */

public class UnpostMessageCommand extends AbstractCommand {


    private static final String _endpoint="unpostMessage";

    public UnpostMessageCommand(String token, String locationName, String id) {
        super(_endpoint,"token="+token+"&location="+locationName+"&messageId="+id);
    }


    @Override
    public void execute() throws IOException, DuplicateExecutionException {
        super.execute();
    }

    public boolean successfulRequest() throws IOException, DuplicateExecutionException, JSONException, CommandNotExecutedException {
        return JsonParser.getValue(super.getResponse(),"response").equals("success");
    }
}
