package pt.ulisboa.tecnico.ist.cmu.locmess.dto;

/**
 * Created by ist179042 on 20/04/17.
 */

public class MessageDto implements LocMessDto {

    private String _author;
    private String _message;

    public MessageDto(String author, String message){
        _author=author;
        _message=message;
    }

    public String getAuthor(){return _author;}

    public String getMessage(){return _message;}

}
