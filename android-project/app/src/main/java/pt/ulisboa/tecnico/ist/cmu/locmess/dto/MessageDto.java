package pt.ulisboa.tecnico.ist.cmu.locmess.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ist179042 on 20/04/17.
 */

public class MessageDto implements LocMessDto {

    private String _author;
    private String _message;
    private String _title;
    private String _location;
    private PolicyDto _policy;
    private String _deliveryMode;
    public static final String CENTRALIZED="Centralized";
    public static final String DECENTRALIZED="Decentralized";

    public MessageDto(String author, String message,String title,String location,PolicyDto policy){
        _author=author;
        _message=message;
        _title=title;
        _location=location;
        _policy = policy;
    }

    @Deprecated
    public MessageDto(String author,String message, String title, String location){
        _author=author;
        _message=message;
        _title=title;
        _location=location;
        _policy = new PolicyDto("",new ArrayList<TopicDto>());
    }

    public String getAuthor(){return _author;}

    public String getMessage(){return _message;}

    public String getTitle(){return _title;}

    public String getLocation(){return _location;}

    public PolicyDto getPolicy(){return _policy;}


}
