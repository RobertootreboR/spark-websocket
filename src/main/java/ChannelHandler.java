import lombok.NoArgsConstructor;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Created by robert on 23.01.17.
 */
//@NoArgsConstructor
public class ChannelHandler {
     List<IChannel> channels=new CopyOnWriteArrayList<>();

    public boolean uniqueChannelName(String channelName) {
        return !getChannelNames().stream()
                .filter(name -> name != null)
                .filter(name -> name.equals(channelName))
                .findFirst()
                .isPresent();
    }
    public void addChannel(Session user, String channelName, Map<Session,User> userUsernameMap) {
        channels.add(new UserChannel(channelName));
        //userUsernameMap.get(user).getChannel().setChannelName(channelName);
    }
    public void refreshChannelList(Map<Session,User> userUsernameMap){
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(String.valueOf(new JSONObject()
                        .put("channellist", getChannelNames())
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public List<String> getChannelNames() {
        return channels.stream().map(IChannel::getChannelName).collect(Collectors.toList());
    }
}
