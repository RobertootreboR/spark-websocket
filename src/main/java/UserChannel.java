import lombok.Getter;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by robert on 22.01.17.
 */
@Getter
public class UserChannel extends AbstractChannel{
    UserChannel(String channelName){
        super(channelName);
    }

    public  void broadcastMessage(String sender, String message, String reason, Map<Session, User> userUsernameMap,List<String> channels) {
        userUsernameMap.keySet().stream().filter(Session::isOpen).filter(session -> inChannel(userUsernameMap.get(session))).forEach(session -> {
            try {
                session.getRemote().sendString(String.valueOf(new JSONObject()
                        .put("userMessage", Chat.createHtmlMessageFromSender(sender, message))
                        .put("reason", reason)
                        .put("channellist", channels)
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    private boolean inChannel(User user) {
        return user.getChannel().getChannelName().equals(this.getChannelName());
    }
}
