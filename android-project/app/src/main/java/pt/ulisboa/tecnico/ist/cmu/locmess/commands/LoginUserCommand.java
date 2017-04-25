package pt.ulisboa.tecnico.ist.cmu.locmess.commands;

import org.json.JSONException;

import java.io.IOException;

import pt.ulisboa.tecnico.ist.cmu.locmess.JsonParser;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.DuplicateExecutionException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.LoginFailedException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.CommandNotExecutedException;

/**
 * Created by jorge on 03/04/17.
 */

public class LoginUserCommand extends AbstractCommand {

    private static final String _endpoint="loginUser";
    private String _token;
    private boolean _success=false;


    public LoginUserCommand(String username, String password){
        super(_endpoint,"username="+username+"&password="+password);
    }

    @Override
    public void execute() throws IOException, DuplicateExecutionException {
        super.execute();
        try {
            _token=JsonParser.getValue(super.getResponse(),"token");
            _success=true;
        } catch (JSONException e) {
            _success=false;
        } catch (CommandNotExecutedException e) {
            e.printStackTrace();
        }
    }

    public String getToken() throws CommandNotExecutedException, LoginFailedException {
        if(!_executed){
           throw new CommandNotExecutedException();
        }
        if( _success) {
            return _token;
        }
        else {
            throw new LoginFailedException();
        }
    }

}
