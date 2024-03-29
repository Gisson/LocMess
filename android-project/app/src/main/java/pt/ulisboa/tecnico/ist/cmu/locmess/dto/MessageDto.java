package pt.ulisboa.tecnico.ist.cmu.locmess.dto;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ist179042 on 20/04/17.
 */

public class MessageDto implements LocMessDto, Serializable {

    private String _author;
    private String _message;
    private String _title;
    private String _location;
    private PolicyDto _policy;
    private String _deliveryMode;
    private final String _centralization="Centralized";

    public static class JsonAtributes{
        public static final String AUTHOR="author";
        public static final String MESSAGE="message";
        public static final String TITLE="title";
        public static final String LOCATION="location";
        public static final String POLICY="policy";


    }

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

    public String toJson(){
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(JsonAtributes.AUTHOR,_author);
            jsonObject.put(JsonAtributes.MESSAGE,_message);
            jsonObject.put(JsonAtributes.TITLE,_title);
            jsonObject.put(JsonAtributes.LOCATION,_location);
            jsonObject.put(JsonAtributes.POLICY,_policy.getJsonObject());
        } catch (JSONException e) {
            e.toString();
        }
        return jsonObject.toString();
    }

    public String getCentralization(){
        return _centralization;
    }


}
