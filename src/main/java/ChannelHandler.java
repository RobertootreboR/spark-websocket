import lombok.NoArgsConstructor;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Created by robert on 23.01.17.
 */
//@NoArgsConstructor
public class ChannelHandler {
     List<IChannel> channels=new CopyOnWriteArrayList<>();

    public boolean uniqueChannelName(String channelName) {
        String processedName = process(channelName);
        return !getChannelNames().stream()
                .filter(name -> name != null)
                .filter(name -> name.equals(processedName))
                .findFirst()
                .isPresent();
    }

    private String process(String channelName) {
        if(channelName.contains(","))
           return channelName.substring(0,channelName.indexOf(','));
        else return channelName;
    }

    public void addChannel(String channelName) {
        if(channelName.startsWith("Protected")){
            String[] tmp = channelName.split(",");
            channels.add(new ProtectedUserChannel(tmp[0],tmp[1]));
        }
        else channels.add(new UserChannel(channelName));
    }
    public void refreshChannelList(Map<Session,User> userUsernameMap){
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(String.valueOf(new JSONObject()
                        .put("channellist", getChannelNames())
                        .put("reason","refresh")
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public List<String> getChannelNames() {
        return channels.stream().map(IChannel::getChannelName).collect(Collectors.toList());
    }

    public boolean authenticate(String message, Session user, Map<Session, User> userUsernameMap) {
        String[] tmp = message.split(",");
        IChannel test = channels.stream()
                                .filter(channel -> channel.getChannelName().equals(tmp[0]))
                                .findFirst()
                                .orElse(new UserChannel(""));
            if(test.getPassword().equals(tmp[1])){
                userUsernameMap.get(user).setChannel(new ProtectedUserChannel(tmp[0],tmp[1]));
                return true;
            }
        return false;
    }
}
