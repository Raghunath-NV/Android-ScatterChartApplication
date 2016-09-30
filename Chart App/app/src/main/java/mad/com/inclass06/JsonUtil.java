package mad.com.inclass06;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


public class JsonUtil {
    public static class UserJsonParser{
        public static ArrayList<Data> getUserJson(String jsonData){
            ArrayList<Data> userList=new ArrayList<Data>();
            try {

                JSONArray userArray=new JSONArray(jsonData);
                for(int i=0;i<userArray.length();i++){
                    userList.add(Data.createData(userArray.getJSONObject(i)));
                }
            }  catch (JSONException e) {
                e.printStackTrace();
            }
            return userList;
        }
    }
}