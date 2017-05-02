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
    private List<String> _topics;
    private String _location;

    public MessageDto(String author, String message,String title,List<String> topics,String location){
        _author=author;
        _message=message;
        _title=title;
        _topics=topics;
        _location=location;
    }

    public MessageDto(String author, String message,String title,String topics, String location){
        _author=author;
        _message=message;
        _title=title;
        _topics=new ArrayList<>();
        _topics.add(topics);
        _location=location;
    }

    @Deprecated
    public MessageDto(String author,String message, String title, String location){
        _author=author;
        _message=message;
        _title=title;
        _location=location;
    }

    public String getAuthor(){return _author;}

    public String getMessage(){return _message;}

    public String getTitle(){return _title;}

    public String getLocation(){return _location;}

    public List<String> getTopics(){return _topics;}

}
