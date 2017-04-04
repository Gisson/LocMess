package pt.ulisboa.tecnico.ist.cmu.locmess.commands;

import org.json.JSONException;

import java.io.IOException;

import pt.ulisboa.tecnico.ist.cmu.locmess.JsonParser;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.AlreadyRequestedException;

/**
 * Created by jorge on 03/04/17.
 */

public class LoginUserCommand extends AbstractCommand {

    private static final String _endpoint="loginUser";
    private String _token;

    public LoginUserCommand(String username, String password){
        super(_endpoint,"username="+username+"&password="+password);
    }

    @Override
    public void execute() throws IOException, AlreadyRequestedException {
        super.execute();
        try {
            _token=JsonParser.getValue(super.getResponse(),"token");
        } catch (JSONException e) {
            //TODO: Handle this exception properly
            e.printStackTrace();
        }
    }

    public String getToken() throws IOException, AlreadyRequestedException {
        if(!_requestState){
            execute();
        }
        return _token;
    }
}
