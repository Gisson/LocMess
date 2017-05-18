package pt.ulisboa.tecnico.ist.cmu.locmess.wifiDirect;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import pt.ulisboa.tecnico.ist.cmu.locmess.dto.MessageDto;
import pt.ulisboa.tecnico.ist.cmu.locmess.dto.PolicyDto;

/**
 * Created by jorge on 16/05/17.
 */

public class WifiDirectMessageDto extends MessageDto {

    @Deprecated
    private String _userTo;
    private int _ttl=2;

    private final String _centralization="Decentralized";



    public static class JsonAtributes extends MessageDto.JsonAtributes{
        public static final String USERTO="toUser";
        public static final String TTL="ttl";
    }

    @Deprecated
    public WifiDirectMessageDto(String author, String message, String title, String location,
                                PolicyDto policy,String userTo) {
        super(author, message, title, location, policy);
        _userTo = userTo;
    }
    @Deprecated
    public WifiDirectMessageDto(String author, String message, String title, String location,
                                    PolicyDto policy,String userTo,int ttl) {
        super(author, message, title, location, policy);
        _userTo = userTo;
        _ttl=ttl;
    }
    @Deprecated
    public WifiDirectMessageDto(MessageDto messageDto, String userTo, int ttl){
        super(messageDto.getAuthor(),messageDto.getMessage(),messageDto.getTitle(),
                messageDto.getLocation(), messageDto.getPolicy());
        _userTo=userTo;
        _ttl=ttl;
    }

    public WifiDirectMessageDto(MessageDto messageDto,int ttl){
        super(messageDto.getAuthor(),messageDto.getMessage(),messageDto.getTitle(),
                messageDto.getLocation(), messageDto.getPolicy());
        _ttl=ttl;
    }

    public WifiDirectMessageDto(String author, String message, String title, String location,
                                     PolicyDto policy,int ttl) {
        super(author, message, title, location, policy);
        _ttl=ttl;
    }

    public WifiDirectMessageDto(String author, String message, String title, String location,
                                PolicyDto policy) {
        super(author, message, title, location, policy);
    }

    public WifiDirectMessageDto(MessageDto messageDto){
        super(messageDto.getAuthor(),messageDto.getMessage(),messageDto.getTitle(),
                messageDto.getLocation(), messageDto.getPolicy());
    }



    @Deprecated
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
            jsonObject.put(JsonAtributes.TTL,_ttl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
    public String getCentralization() {
        return _centralization;
    }
}
