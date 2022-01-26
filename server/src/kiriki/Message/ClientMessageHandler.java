package kiriki.Message;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ClientMessageHandler extends MessageHandler {

    protected static JSONObject getJSONObject(String message) throws ParseException {
        return (JSONObject) new JSONParser().parse(message);
    }

    //  message: {"userName": "foobar"}
    protected static String getName(String message) {
        try {
            return (String) getJSONObject(message).get("userName");
        } catch (ParseException e) {
            System.out.println("Error Parsing JSON object to get client name:");
            e.printStackTrace();
        } catch(Exception e) {
            System.out.println("Unexpected error Parsing JSON object to get client name:");
            e.printStackTrace();
        }
        return null;
    }

    //  message: {"guess": [1, 2, 3]}
    protected static Object[] getGuess(String message) {
        try {
            return ((JSONArray) getJSONObject(message).get("guess")).toArray();
        } catch (ParseException e) {
            System.out.println("Error Parsing JSON object to get client guess:");
            e.printStackTrace();
        } catch(Exception e) {
            System.out.println("Unexpected error Parsing JSON object to get client guess:");
            e.printStackTrace();
        }
        return null;
    }

    //  message: {"value": 1}
    protected static int getExcludeDiceValue(String message) {
        try {
            return (int) (long) (Long) getJSONObject(message).get("value");
        } catch (ParseException e) {
            System.out.println("Error Parsing JSON object to get exclude dice value:");
            e.printStackTrace();
        } catch(Exception e) {
            System.out.println("Unexpected error Parsing JSON object to get exclude dice value:");
            e.printStackTrace();
        }
        return 0;
    }
}
