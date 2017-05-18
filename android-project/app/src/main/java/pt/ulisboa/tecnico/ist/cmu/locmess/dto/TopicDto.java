package pt.ulisboa.tecnico.ist.cmu.locmess.dto;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nuno on 26/04/17.
 */

public class TopicDto implements LocMessDto {
    private String key;
    private String value;

    public static class JsonAtributes{
        public static final String KEY="key";
        public static final String VALUE="value";

    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof TopicDto) {
            return key.equals(((TopicDto) obj).key)
                    && value.equals(((TopicDto) obj).value);
        }
        return false;
    }

    public TopicDto(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /** create a topic from a 'key=value' string */
    public TopicDto(String topic) {
        String[] fields = topic.split("=");
        key = fields[0];
        value = fields[1];
    }

    public String getKey() { return key; }
    public String getValue() { return value; }
    public String toString() {
        return key + "=" + value;
    }

    public String toJson(){

        return getJsonObject().toString();
    }

    public JSONObject getJsonObject(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JsonAtributes.KEY, key);
            jsonObject.put(JsonAtributes.VALUE, value);
        } catch (JSONException e) {
            e.toString();
        }
        return jsonObject;
    }
}
