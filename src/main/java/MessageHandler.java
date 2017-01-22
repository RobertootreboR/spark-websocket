import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

/**
 * Created by robert on 22.01.17.
 */
public class MessageHandler {

    public void addUser(Session user, String message) {
        String username = message.substring(message.indexOf('*') + 1);
        Chat.userUsernameMap.put(user, username);
        Chat.broadcastMessage("Server",(username + " joined the chat"),"message");
    }

    public boolean uniqueUsername(String newUsername) {
        return !Chat.userUsernameMap.values().stream()
                .filter(username -> username.equals(newUsername))
                .findFirst()
                .isPresent();
    }

    public void retryLogin(Session user) {
        try{ user.getRemote().sendString(String.valueOf(new JSONObject().put("reason", "duplicate_username") ) );
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
