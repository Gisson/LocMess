package pt.ulisboa.tecnico.ist.cmu.locmess.dto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by jorge on 03/05/17.
 */

public class PolicyDto implements LocMessDto {

    private String _type;
    private List<TopicDto> _topics;
    public static final String WHITELIST="whitelist";
    public static final String BLACKLIST="blacklist";

    public static class JsonAtributes{
        public static final String TYPE="type";
        public static final String TOPICS="topics";

    }

    public PolicyDto(String type, List<TopicDto> topics){
        _type=type;
        _topics=topics;
    }

    public String getType(){return _type;}
    public List<TopicDto> getTopics(){return _topics;}

    public String toJson(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JsonAtributes.TYPE, _type);
            JSONArray arr = new JSONArray();
            for(TopicDto topic : _topics) {
                arr.put(topic.toJson());
            }
            jsonObject.put(JsonAtributes.TOPICS,arr);
        } catch (JSONException e) {
            e.toString();
        }
        return jsonObject.toString();
    }

}
