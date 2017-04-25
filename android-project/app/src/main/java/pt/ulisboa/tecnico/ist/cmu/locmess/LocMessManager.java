package pt.ulisboa.tecnico.ist.cmu.locmess;

import android.os.AsyncTask;

import java.io.IOException;

import pt.ulisboa.tecnico.ist.cmu.locmess.commands.AbstractCommand;
import pt.ulisboa.tecnico.ist.cmu.locmess.commands.LoginUserCommand;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.DuplicateExecutionException;

/**
 * Created by jorge on 03/04/17.
 */

public class LocMessManager {

    private static LocMessManager _manager=null;
    private String _currentToken;


    protected LocMessManager(){

    }
    public static LocMessManager getInstance(){
        if(_manager==null){
            _manager=new LocMessManager();
        }
        return _manager;
    }

    public void executeAsync(LoginUserCommand loginCommand) throws IOException, DuplicateExecutionException {
        NetworkingCommand nc = new NetworkingCommand();
        nc.execute(loginCommand);

    }

    public void executeAsync(AbstractCommand abstractCommand){
        (new NetworkingCommand()).execute(abstractCommand);
    }

    public class NetworkingCommand extends AsyncTask<AbstractCommand,Void,Boolean>{

        public boolean _completed=false;

        @Override
        protected Boolean doInBackground(AbstractCommand... abstractCommands) {
            for( AbstractCommand c : abstractCommands){
                try {
                    c.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DuplicateExecutionException e) {
                    //FIXME: Handle this exception correctly
                    e.printStackTrace();
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result){
            System.out.println("RESULT IS: "+result);
            _completed = result;
        }
    }

    public void setToken(String token){
        _currentToken=token;
    }
    public String getToken(){
        return _currentToken;
    }
}
