package pt.ulisboa.tecnico.ist.cmu.locmess.dto;

import java.util.List;

/**
 * Created by jorge on 03/05/17.
 */

public class PolicyDto implements LocMessDto {

    private String _type;
    private List<TopicDto> _topics;
    public static final String WHITELIST="whitelist";
    public static final String BLACKLIST="blacklist";

    public PolicyDto(String type, List<TopicDto> topics){
        _type=type;
        _topics=topics;
    }

    public String getType(){return _type;}
    public List<TopicDto> getTopics(){return _topics;}

}
