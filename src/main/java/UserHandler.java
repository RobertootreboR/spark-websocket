import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by robert on 23.01.17.
 */
public class UserHandler {
    Map<Session, User> userUsernameMap = new ConcurrentHashMap<>();
    static WeatherForecast forecast = new WeatherForecast();

    public void addUser(Session user, String message,List<String> channels) {
        String username = message.substring(message.indexOf('*') + 1);
        userUsernameMap.put(user, new User(username,"Default"));
        broadcastMessage("Server",(username + " joined the chat"),"message",channels);
    }

    public boolean uniqueUsername(String newUsername) {
        return !userUsernameMap.values().stream()
                .filter(user -> user.getName().equals(newUsername))
                .findFirst()
                .isPresent();
    }
    public void retryLogin(Session user) {
        try{ user.getRemote().sendString(String.valueOf(new JSONObject().put("reason", "duplicate_username").put("channellist", ChatWebSocketHandler.channels) ) );
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //Sends a message from one user to all users, along with a list of current usernames
    public  void broadcastMessage(String sender, String message, String reason, List<String> channels) {
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(String.valueOf(new JSONObject()
                        .put("userMessage", Chat.createHtmlMessageFromSender(sender, message))
                        .put("channellist", channels)
                        .put("reason", reason)
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    public void processMessage(String sender, String message,List<String> channels) {
        broadcastMessage(sender, message,"message",channels);
        if (message.equals("what's the weather in Krakow?"))
            broadcastMessage("Server", forecast.update(),"message",channels);
        else if (message.equals("What's the time?"))
            broadcastMessage("Server", new SimpleDateFormat("HH:mm:ss").format(new Date()),"message",channels);
        else if (message.equals("What's the day today?"))
            broadcastMessage("Server", new Date().toString().split(" ")[0],"message",channels);
    }

}
