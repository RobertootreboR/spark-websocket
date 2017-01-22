import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

/**
 * Created by robert on 22.01.17.
 */
public class MessageHandler {

    public void addUser(Session user, String message) {
        String username = message.substring(message.indexOf('*') + 1);
        Chat.userUsernameMap.put(user, username);
        Chat.userToChannel.put(username,"Default");
        Chat.broadcastMessage("Server",(username + " joined the chat"),"message");
    }

    public boolean uniqueUsername(String newUsername) {
        return !Chat.userUsernameMap.values().stream()
                .filter(username -> username.equals(newUsername))
                .findFirst()
                .isPresent();
    }

    public void retryLogin(Session user) {
        try{ user.getRemote().sendString(String.valueOf(new JSONObject().put("reason", "duplicate_username").put("channellist", Chat.channels) ) );
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public boolean uniqueChannelName(String channel) {
        return !Chat.userToChannel.values().stream()
                .filter(channelName -> channelName.equals(channel))
                .findFirst()
                .isPresent();
    }

    public void addChannel(Session user, String channelName) {
        String username = Chat.userUsernameMap.get(user);
        Chat.userToChannel.put(username,channelName);
        Chat.channels.add(channelName);
    }

    public void getChannelName(Session user) {
        try{ user.getRemote().sendString(String.valueOf(new JSONObject().put("reason", "duplicate_channelname")
                                                                        .put("channellist", Chat.channels) ) );
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void refresh(){
        Chat.userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(String.valueOf(new JSONObject()
                        .put("channellist", Chat.channels)
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
