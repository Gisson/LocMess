package pt.ulisboa.tecnico.ist.cmu.locmess.wifiDirect;

import org.json.JSONException;
import org.json.JSONObject;

import pt.ulisboa.tecnico.ist.cmu.locmess.dto.MessageDto;
import pt.ulisboa.tecnico.ist.cmu.locmess.dto.PolicyDto;

/**
 * Created by jorge on 16/05/17.
 */

public class WifiDirectMessageDto extends MessageDto {

    private String _userTo;
    private int _ttl=1;

    public static class JsonAtributes extends MessageDto.JsonAtributes{
        public static final String USERTO="toUser";
        public static final String TTL="ttl";
    }

    public WifiDirectMessageDto(String author, String message, String title, String location, PolicyDto policy,String userTo) {
        super(author, message, title, location, policy);
        _userTo = userTo;
    }

    public WifiDirectMessageDto(String author, String message, String title, String location,
                                    PolicyDto policy,String userTo,int ttl) {
        super(author, message, title, location, policy);
        _userTo = userTo;
        _ttl=ttl;
    }

    public String getUserTo(){
        return _userTo;
    }

    public int getTtl(){
        return _ttl;
    }

    public String toJson(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(super.toJson());
            jsonObject.put(JsonAtributes.USERTO,_userTo);
            jsonObject.put(JsonAtributes.TTL,_ttl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
