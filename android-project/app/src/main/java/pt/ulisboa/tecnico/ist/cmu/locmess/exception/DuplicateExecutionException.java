package pt.ulisboa.tecnico.ist.cmu.locmess.exception;

/**
 * Created by jorge on 03/04/17.
 */

public final class DuplicateExecutionException extends LocMessHttpException {
    public DuplicateExecutionException(String reason){
        super(reason);
    }

    public DuplicateExecutionException(){
        super();
    }
}
