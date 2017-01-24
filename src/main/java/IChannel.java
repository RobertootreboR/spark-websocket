import org.eclipse.jetty.websocket.api.Session;

import java.util.List;
import java.util.Map;

/**
 * Created by robert on 22.01.17.
 */

public interface IChannel {
    String getChannelName();
    String getPassword();
    void broadcastMessage(String sender, String message, String reason, Map<Session, User> userUsernameMap,List<String> channels);
    void processMessage(String sender, String message, String reason, Map<Session, User> userUsernameMap,List<String> channels,Session session);
    boolean inCurrentChannel(User user);
    void refreshChannelUsersList(Map<Session, User> userUsernameMap);
}
