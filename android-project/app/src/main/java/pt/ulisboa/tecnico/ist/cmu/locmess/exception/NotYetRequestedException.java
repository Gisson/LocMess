package pt.ulisboa.tecnico.ist.cmu.locmess.exception;

/**
 * Created by jorge on 03/04/17.
 */

public final class NotYetRequestedException extends LocMessHttpException {

    public NotYetRequestedException(String reason){
        super(reason);
    }

    public NotYetRequestedException(){
        super();
    }
}
