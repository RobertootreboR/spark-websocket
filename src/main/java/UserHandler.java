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

    public void addUser(Session user, String message) {
        String username = message.substring(message.indexOf('*') + 1);
        userUsernameMap.put(user, new User(username,new UserChannel("Default")));
    }

    public boolean uniqueUsername(String newUsername) {
        return !userUsernameMap.values().stream()
                .filter(user -> user.getName().equals(newUsername))
                .findFirst()
                .isPresent();
    }
    public void retryLogin(Session user) {
        try{ user.getRemote().sendString(String.valueOf(new JSONObject().put("reason", "duplicate_username") ) );
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void retryNamingChannel(Session user,List<String> channels) {
        try{ user.getRemote().sendString(String.valueOf(new JSONObject().put("reason", "duplicate_channelname")
                .put("channellist", channels) ) );
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean joinChannel(Session user, String channel) {
        if(channel.equals("ChatBot"))
            userUsernameMap.get(user).setChannel(new ChatBotChannel(channel));
        else if(channel.startsWith("Protected")){
            try{ user.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("reason", "authenticate")
                    .put("channel",channel)) );
            }catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
        } else userUsernameMap.get(user).setChannel(new UserChannel(channel));
        return true;
    }
    public void sendToUser(Session session, User user, String message) {
        try {
            session.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("userMessage", AbstractChannel.createHtmlMessageFromSender(user.getName(), message))
                    .put("reason", "message")
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
