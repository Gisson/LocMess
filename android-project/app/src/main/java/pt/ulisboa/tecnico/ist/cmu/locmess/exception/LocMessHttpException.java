package pt.ulisboa.tecnico.ist.cmu.locmess.exception;

/**
 * Created by jorge on 03/04/17.
 */

public class LocMessHttpException extends Exception {
    private String _reason;

    public LocMessHttpException(){
        _reason="No apparent reason!";
    }

    public LocMessHttpException(String reason){
        _reason=reason;
    }

}
