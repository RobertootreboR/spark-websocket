import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@WebSocket
public class ChatWebSocketHandler {
    public MessageHandler handle = new MessageHandler();
    //static List<String> channels = new CopyOnWriteArrayList<String>();
    ChannelHandler channelHandler =new ChannelHandler();
    UserHandler userHandler = new UserHandler();
    private String sender, msg;

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        if(channelHandler.uniqueChannelName("Default"))
            channelHandler.channels.add(new UserChannel("Default"));
        channelHandler.refreshChannelList(userHandler.userUsernameMap);
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        String username = userHandler.userUsernameMap.get(user).getName();
        userHandler.userUsernameMap.remove(user);
        userHandler.broadcastMessage(sender = "Server", msg = (username + " left the chat"), "message",channelHandler.getChannelNames());
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        if (message.startsWith("#username#*")) {
            if (userHandler.uniqueUsername(decode(message)))
                userHandler.addUser(user, message,channelHandler.getChannelNames());
            else userHandler.retryLogin(user);
        } else if (message.startsWith("#addChannel#*")) {
            if (channelHandler.uniqueChannelName(decode(message))) {
                channelHandler.addChannel(user, decode(message),userHandler.userUsernameMap);
            } else {
                userHandler.retryNamingChannel(user,channelHandler.getChannelNames());
            }

        }else if(message.startsWith("#joinChannel#*")) {
            userHandler.joinChannel(user,decode(message));
            userHandler.sendToUser(user,"You are currently in "+decode(message)+ " channel");
            channelHandler.refreshChannelList(userHandler.userUsernameMap);
        }else userHandler.userUsernameMap.get(user).getChannel().broadcastMessage(sender = userHandler.userUsernameMap.get(user).getName(), msg = message,"message",userHandler.userUsernameMap,channelHandler.getChannelNames());
        channelHandler.refreshChannelList(userHandler.userUsernameMap);

    }

    String decode(String message) {
        return message.substring(message.indexOf('*') + 1);
    }

}// albo globalna lista userów. każdy ma w sobie username, ?session, channel. klasa channel ma liste kanałów, jako stringi
//chat data tylko przy
//usermap klasa z klasa wewnetrzna user, ktora ogarnia dodawanie userów itd.
// w chatdata powinno byc2 rzeczy. userow mapa i lista kanałów. obiekt data robimy w sockethandlerach
//klasa dodatkowa z tymi z chatu pierdołami. sprobować można statycznie początkowo.
