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

    public void processMessage(String sender, String message, String reason, Map<Session, User> userUsernameMap,List<String> channels,Session session){
        broadcastMessage(sender,message,reason,userUsernameMap,channels);
    }

    @Override
    public String getPassword() {
        return "";
    }
}
