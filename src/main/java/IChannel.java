import lombok.Getter;
import org.eclipse.jetty.websocket.api.Session;

import java.util.List;
import java.util.Map;

/**
 * Created by robert on 22.01.17.
 */

public interface IChannel {
    String getChannelName();
    void setChannelName(String name);
    public  void broadcastMessage(String sender, String message, String reason, Map<Session, User> userUsernameMap,List<String> channels);

}
