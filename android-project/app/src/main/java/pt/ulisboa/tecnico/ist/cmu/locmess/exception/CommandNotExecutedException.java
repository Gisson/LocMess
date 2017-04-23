package pt.ulisboa.tecnico.ist.cmu.locmess.exception;

/**
 * Created by jorge on 03/04/17.
 */

public final class CommandNotExecutedException extends LocMessHttpException {

    public CommandNotExecutedException(String reason){
        super(reason);
    }

    public CommandNotExecutedException(){
        super();
    }
}
