package pt.ulisboa.tecnico.ist.cmu.locmess.commands;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import pt.ulisboa.tecnico.ist.cmu.locmess.JsonParser;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.DuplicateExecutionException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.CommandNotExecutedException;

/**
 * Created by jorge on 03/04/17.
 */

public class PostMessageCommand extends AbstractCommand {


    private static final String _endpoint="postMessage";

    public PostMessageCommand(String token, String locationName,String content,String title, String deliveryMode,List<String> topics, String duration) {
        super(_endpoint,"token="+token+"&location="+locationName+"&message="+content+"&title="+title+"&deliveryMode="+deliveryMode+"&topics="+getTopics(topics)
        +"&endTime="+duration);
    }

    @Override
    public void execute() throws IOException, DuplicateExecutionException {
        super.execute();
    }

    public boolean successfulRequest() throws IOException, DuplicateExecutionException, JSONException, CommandNotExecutedException {
        return JsonParser.getValue(super.getResponse(),"response").equals("success");
    }

    private static String getTopics(List<String> topics){
        String topicStr="";
        if(topics.size()==0){
            return topicStr;
        }
        for(int i=0;i<topics.size()-1;i++){
            topicStr+=topics.get(i)+",";
        }
        topicStr+=topics.get(topics.size()-1);
        return topicStr;
    }
}
