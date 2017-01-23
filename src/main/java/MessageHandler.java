import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by robert on 22.01.17.
 */
public class MessageHandler {
    

    public boolean uniqueChannelName(String channel) {
        return !ChatWebSocketHandler.channels.stream()
                .filter(channelName -> channelName.equals(channel))
                .findFirst()
                .isPresent();
    }

    public void addChannel(Session user, String channelName, Map<Session,User> userUsernameMap) {
        String username = userUsernameMap.get(user).getName();
        Chat.userToChannel.put(username,channelName);
        ChatWebSocketHandler.channels.add(channelName);
    }

    public void getChannelName(Session user) {
        try{ user.getRemote().sendString(String.valueOf(new JSONObject().put("reason", "duplicate_channelname")
                                                                        .put("channellist", ChatWebSocketHandler.channels) ) );
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void refresh(Map<Session,User> userUsernameMap){
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(String.valueOf(new JSONObject()
                        .put("channellist", ChatWebSocketHandler.channels)
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
