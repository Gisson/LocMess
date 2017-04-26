package pt.ulisboa.tecnico.ist.cmu.locmess;

import android.os.AsyncTask;
import android.text.TextUtils;

import org.json.JSONException;

import java.io.IOException;

import pt.ulisboa.tecnico.ist.cmu.locmess.commands.AbstractCommand;
import pt.ulisboa.tecnico.ist.cmu.locmess.commands.LoginUserCommand;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.CommandNotExecutedException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.DuplicateExecutionException;

/**
 * Created by jorge on 03/04/17.
 */

public class LocMessManager {

    //Callbcak will notify us when async task is finished
    public interface CompleteCallback{
        void OnComplete(boolean result, String message);
    }

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

//    public void executeAsync(LoginUserCommand loginCommand) throws IOException, DuplicateExecutionException {
//        NetworkingCommand nc = new NetworkingCommand();
//        nc.execute(loginCommand);
//
//    }

    public void executeAsync(AbstractCommand abstractCommand, CompleteCallback callback){
        (new NetworkingCommand(callback)).execute(abstractCommand);
    }

    public class NetworkingCommand extends AsyncTask<AbstractCommand,Void,Boolean>{

        public boolean _completed=false;

        CompleteCallback callback;
        String message = "";

        AbstractCommand command;

        //Here we run our async task, handle errors and passing error messages via callback
        public NetworkingCommand(CompleteCallback callback){
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(AbstractCommand... abstractCommands) {
            boolean result = false;
            for( AbstractCommand c : abstractCommands){
                command = c;
                try {
                    c.execute();
                    result = c.successfulRequest();
                } catch (IOException e) {
                    e.printStackTrace();
                    message = "Can not complete request. Response code: " + c.getResponseCode();
                } catch (DuplicateExecutionException e) {
                    //FIXME: Handle this exception correctly
                    e.printStackTrace();
                } catch (CommandNotExecutedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result){
            System.out.println("RESULT IS: "+result);
            _completed = result;
            if(!result){
                try{
                    //Check reason why task is fail
                    String reason = command.getReason();
                    if(!TextUtils.isEmpty(reason)){
                        message = reason;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(TextUtils.isEmpty(message)){
                    message = "Fail";
                }
            }
            if(callback != null){
                callback.OnComplete(result, message);
            }
        }
    }

    public void setToken(String token){
        _currentToken=token;
    }
    public String getToken(){
        return _currentToken;
    }
}
