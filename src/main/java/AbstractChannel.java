import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static j2html.TagCreator.*;

/**
 * Created by robert on 23.01.17.
 */
@Getter
@Setter
@AllArgsConstructor
public abstract class AbstractChannel implements IChannel{
    String ChannelName;
    public boolean inCurrentChannel(User user) {
        return user.getChannel().getChannelName().equals(this.getChannelName());
    }
    //Builds a HTML element with a sender-name, a message, and a timestamp,
    static String createHtmlMessageFromSender(String sender, String message) {
        return article().with(
                b(sender + " says:"),
                p(message),
                span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(new Date()))
        ).render();
    }
    public void refreshChannelUsersList(Map<Session,User> userUsernameMap){
        userUsernameMap.keySet().stream().filter(Session::isOpen).filter(session -> inCurrentChannel(userUsernameMap.get(session)))
                .forEach(session -> {
            try {
                session.getRemote().sendString(String.valueOf(new JSONObject()
                        .put("reason","userRefresh")
                        .put("userList",usersInChannel(userUsernameMap))
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    List<String> usersInChannel(Map<Session,User> userUsernameMap){
        return userUsernameMap.values().stream().filter(this::inCurrentChannel).map(User::getName).collect(Collectors.toList());
    }

}
