package pt.ulisboa.tecnico.ist.cmu.locmess.commands;

import org.json.JSONException;

import java.io.IOException;

import pt.ulisboa.tecnico.ist.cmu.locmess.JsonParser;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.AlreadyRequestedException;

/**
 * Created by jorge on 03/04/17.
 */

public class RemoveLocationCommand extends AbstractCommand {

    private static final String _endpoint="removeLocation";

    public RemoveLocationCommand(String token, String locationName) {
        super(_endpoint,"token="+token+"&location="+locationName);
    }

    @Override
    public void execute() throws IOException, AlreadyRequestedException {
        super.execute();
    }

    public boolean successfulRequest() throws IOException, AlreadyRequestedException, JSONException {
        return JsonParser.getValue(super.getResponse(),"response").equals("success");
    }
}
