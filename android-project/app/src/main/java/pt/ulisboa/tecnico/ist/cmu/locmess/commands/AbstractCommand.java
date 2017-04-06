package pt.ulisboa.tecnico.ist.cmu.locmess.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import pt.ulisboa.tecnico.ist.cmu.locmess.exception.AlreadyRequestedException;
import pt.ulisboa.tecnico.ist.cmu.locmess.exception.NotYetRequestedException;

/**
 * Created by jorge on 03/04/17.
 */

public abstract class AbstractCommand {

    private final String SERVERADDR="pikachu.rnl.tecnico.ulisboa.pt";
    private final Integer SERVERPORT=31000;
    private String _endpoint,_args,_response;
    protected boolean _requestState=false;

    protected AbstractCommand(String endpoint, String args){
        _endpoint=endpoint;
        _args=args;
    }

    public AbstractCommand(String endpoint){
        _endpoint=endpoint;
        _args="";
    }

    public void execute() throws IOException, AlreadyRequestedException {
        if(_requestState){
            throw new AlreadyRequestedException();
        }
        URL url=new URL("http://"+SERVERADDR+":"+SERVERPORT.toString()+"/"+_endpoint+"?"+_args);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        String response ="";

        while ((inputLine = in.readLine()) != null) {
            response+=inputLine;
        }
        in.close();
        _response=response;
        _requestState=true;
    }

    public String getResponse() throws NotYetRequestedException {
        if(!_requestState){
            throw new NotYetRequestedException();
        }
        return _response;

    }

    public String getAddr(){
        return SERVERADDR;
    }

    public Integer getPort(){
        return SERVERPORT;
    }

}
