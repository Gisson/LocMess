package pt.ulisboa.tecnico.ist.cmu.locmess.commands;

import org.json.JSONException;

import java.io.IOException;

import pt.ulisboa.tecnico.ist.cmu.locmess.JsonParser;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.AlreadyRequestedException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.LoginFailedException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.NotYetRequestedException;

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
    public void execute() throws IOException, AlreadyRequestedException {
        super.execute();
        try {
            _token=JsonParser.getValue(super.getResponse(),"token");
            _success=true;
        } catch (JSONException e) {
            _success=false;
        } catch (NotYetRequestedException e) {
            e.printStackTrace();
        }
    }

    public String getToken() throws NotYetRequestedException, LoginFailedException {
        if(!_requestState){
           throw new NotYetRequestedException();
        }
        if( _success) {
            return _token;
        }
        else {
            throw new LoginFailedException();
        }
    }

}
