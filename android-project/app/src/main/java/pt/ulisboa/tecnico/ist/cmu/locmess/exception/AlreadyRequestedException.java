package pt.ulisboa.tecnico.ist.cmu.locmess.exception;

/**
 * Created by jorge on 03/04/17.
 */

public final class AlreadyRequestedException extends LocMessHttpException {
    public AlreadyRequestedException(String reason){
        super(reason);
    }

    public AlreadyRequestedException(){
        super();
    }
}
