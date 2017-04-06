package pt.ulisboa.tecnico.ist.cmu.locmess.commands;


import org.json.JSONException;

import java.io.IOException;

import pt.ulisboa.tecnico.ist.cmu.locmess.JsonParser;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.AlreadyRequestedException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.NotYetRequestedException;

/**
 * Created by jorge on 03/04/17.
 */

public class RegisterUserCommand extends AbstractCommand {

    private static final String _endpoint="registerUser";


    public RegisterUserCommand(String username, String password){
        super(_endpoint,"username="+username+"&password="+password);
    }

    @Override
    public void execute() throws IOException, AlreadyRequestedException {
        super.execute();
    }

    public boolean successfulRequest() throws IOException, AlreadyRequestedException, JSONException, NotYetRequestedException {
        return JsonParser.getValue(super.getResponse(),"response").equals("success");
    }
}
