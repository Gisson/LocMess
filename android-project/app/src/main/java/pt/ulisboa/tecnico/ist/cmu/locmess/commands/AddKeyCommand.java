package pt.ulisboa.tecnico.ist.cmu.locmess.commands;

import org.json.JSONException;

import java.io.IOException;

import pt.ulisboa.tecnico.ist.cmu.locmess.JsonParser;
import pt.ulisboa.tecnico.ist.cmu.locmess.LocMessManager;
import pt.ulisboa.tecnico.ist.cmu.locmess.dto.TopicDto;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.DuplicateExecutionException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.CommandNotExecutedException;

/**
 * Created by jorge on 03/04/17.
 */

public class AddKeyCommand extends AbstractCommand {

    private static final String _endpoint="addKey";

    public AddKeyCommand(String token,String key,String value) {
        super(_endpoint,"token="+token+"&key="+key+"&value="+value);
    }

    public AddKeyCommand(String token, TopicDto topic) {
        super(_endpoint, "token="+token + "&key="+topic.getKey() + "&value="+topic.getValue());
        LocMessManager.getInstance().addTopic(topic);
    }

    @Override
    public void execute() throws IOException, DuplicateExecutionException {
        super.execute();
    }

    public boolean successfulRequest() throws CommandNotExecutedException, JSONException {
        return JsonParser.getValue(super.getResponse(),"response").equals("success");
    }
}
