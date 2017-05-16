package pt.ulisboa.tecnico.ist.cmu.locmess.exception;

/**
 * Created by jorge on 11/05/17.
 */
public class NoLocationException extends LocMessHttpException {
    public NoLocationException(String reason){
        super(reason);
    }
}
