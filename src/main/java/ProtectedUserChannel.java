import lombok.Getter;
import org.eclipse.jetty.websocket.api.Session;

import java.util.List;
import java.util.Map;

/**
 * Created by robert on 24.01.17.
 */
@Getter
public class ProtectedUserChannel extends AbstractChannel {
    String password;

    ProtectedUserChannel(String username, String password) {
        super(username);
        this.password = password;
    }

    public void processMessage(String sender, String message, String reason, Map<Session, User> userUsernameMap, List<String> channels, Session session) {
        broadcastMessage(sender, message, reason, userUsernameMap, channels);
    }

}
