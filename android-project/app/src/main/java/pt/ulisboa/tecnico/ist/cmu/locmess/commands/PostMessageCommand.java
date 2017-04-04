package pt.ulisboa.tecnico.ist.cmu.locmess.commands;

import org.json.JSONException;

import java.io.IOException;

import pt.ulisboa.tecnico.ist.cmu.locmess.JsonParser;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.AlreadyRequestedException;

/**
 * Created by jorge on 03/04/17.
 */

public class PostMessageCommand extends AbstractCommand {


    private static final String _endpoint="postMessage";

    public PostMessageCommand(String token, String locationName,String content) {
        super(_endpoint,"token="+token+"&location="+locationName+"&message="+content);
    }

    @Override
    public void execute() throws IOException, AlreadyRequestedException {
        super.execute();
    }

    public boolean successfulRequest() throws IOException, AlreadyRequestedException, JSONException {
        return JsonParser.getValue(super.getResponse(),"response").equals("success");
    }
}
