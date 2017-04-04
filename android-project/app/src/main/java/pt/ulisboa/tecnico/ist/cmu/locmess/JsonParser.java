package pt.ulisboa.tecnico.ist.cmu.locmess;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Dictionary;

/**
 * Created by jorge on 03/04/17.
 */

public class JsonParser {
    //I dont really feel like doing this right now.
 /*   public Dictionary<String,String> parse(String jsonToParse, String[] parameters) throws JSONException {
        JSONObject json = new JSONObject(jsonToParse);

    }*/

 //FIXME: This should be reimplemented because it is not enough abstract.......XXXX
 public static String getValue(String jsonToParse,String key) throws JSONException {
     JSONObject json=new JSONObject(jsonToParse);
     return json.getString(key);
 }

}
