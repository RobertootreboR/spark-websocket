import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static j2html.TagCreator.*;

@Getter
@Setter
@AllArgsConstructor
public abstract class AbstractChannel implements IChannel {
    String ChannelName;

    public boolean inCurrentChannel(User user) {
        return user.getChannel().getChannelName().equals(this.getChannelName());
    }

    static String createHtmlMessageFromSender(String sender, String message) {
        return article().with(
                b(sender + " says:"),
                p(message),
                span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(new Date()))
        ).render();
    }

    public void refreshChannelUsersList(Map<Session, User> userUsernameMap) {
        try {
            broadcast(userUsernameMap, new JSONObject()
                    .put("reason", "userRefresh")
                    .put("userList", usersInChannel(userUsernameMap)));
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    private List<String> usersInChannel(Map<Session, User> userUsernameMap) {
        return userUsernameMap.values().stream().filter(this::inCurrentChannel).map(User::getName).collect(Collectors.toList());
    }

    public void broadcastMessage(String sender, String message, String reason, Map<Session, User> userUsernameMap, List<String> channels) {
        try {
            broadcast(userUsernameMap, new JSONObject()
                    .put("userMessage", createHtmlMessageFromSender(sender, message))
                    .put("reason", reason)
                    .put("channellist", channels));
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public void broadcast(Map<Session, User> userUsernameMap, JSONObject jsonObject) {
        userUsernameMap.keySet().stream().filter(Session::isOpen).filter(session -> inCurrentChannel(userUsernameMap.get(session))).forEach(session -> {
            try {
                session.getRemote().sendString(String.valueOf(jsonObject));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


}
