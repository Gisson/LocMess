package pt.ulisboa.tecnico.ist.cmu.locmess;

import android.os.AsyncTask;

import java.io.IOException;

import pt.ulisboa.tecnico.ist.cmu.locmess.commands.AbstractCommand;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.AlreadyRequestedException;

/**
 * Created by jorge on 03/04/17.
 */

public class LocMessManager {

    private static LocMessManager _manager=null;

    protected LocMessManager(){

    }
    public LocMessManager getInstance(){
        if(_manager==null){
            _manager=new LocMessManager();
        }
        return _manager;
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
                } catch (AlreadyRequestedException e) {
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
}
