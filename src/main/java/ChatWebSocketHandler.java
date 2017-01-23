import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@WebSocket
public class ChatWebSocketHandler {
    public MessageHandler handle = new MessageHandler();
    static List<String> channels = new CopyOnWriteArrayList<String>();
    UserHandler userHandler = new UserHandler();
    private String sender, msg;

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        if(handle.uniqueChannelName("Default"))
            channels.add("Default");

    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        String username = userHandler.userUsernameMap.get(user).getName();
        userHandler.userUsernameMap.remove(user);
        userHandler.broadcastMessage(sender = "Server", msg = (username + " left the chat"), "message",channels);
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        if (message.startsWith("#username#*")) {
            if (userHandler.uniqueUsername(decode(message)))
                userHandler.addUser(user, message,channels);
            else userHandler.retryLogin(user);
        } else if (message.startsWith("#addChannel#*")) {
            if (handle.uniqueChannelName(decode(message))) {
                handle.addChannel(user, decode(message),userHandler.userUsernameMap);
                handle.refresh(userHandler.userUsernameMap);
            } else {
                handle.getChannelName(user);
                handle.refresh(userHandler.userUsernameMap);
            }

        } else userHandler.processMessage(sender = userHandler.userUsernameMap.get(user).getName(), msg = message,channels);
    }

    String decode(String message) {
        return message.substring(message.indexOf('*') + 1);
    }

}// albo globalna lista userów. każdy ma w sobie username, ?session, channel. klasa channel ma liste kanałów, jako stringi
//chat data tylko przy
//usermap klasa z klasa wewnetrzna user, ktora ogarnia dodawanie userów itd.
// w chatdata powinno byc2 rzeczy. userow mapa i lista kanałów. obiekt data robimy w sockethandlerach
//klasa dodatkowa z tymi z chatu pierdołami. sprobować można statycznie początkowo.
