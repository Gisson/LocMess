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

    public static class JsonAtributes extends MessageDto.JsonAtributes{
        public static final String USERTO="toUser";
    }

    public WifiDirectMessageDto(String author, String message, String title, String location, PolicyDto policy,String userTo) {
        super(author, message, title, location, policy);
        _userTo = userTo;
    }

    public String getUserTo(){
        return _userTo;
    }

    public String toJson(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(super.toJson());
            jsonObject.put("toUser",_userTo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
