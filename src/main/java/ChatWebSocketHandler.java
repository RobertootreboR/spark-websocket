import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@WebSocket
public class ChatWebSocketHandler {
    private ChannelHandler channelHandler = new ChannelHandler();
    private UserHandler userHandler = new UserHandler();

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        if (channelHandler.uniqueChannelName("Default"))
            channelHandler.channels.add(new UserChannel("Default"));
        channelHandler.refreshChannelList(userHandler.userUsernameMap);
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        String username = userHandler.userUsernameMap.get(user).getName();
        userHandler.userUsernameMap.remove(user);
        userHandler.userUsernameMap.get(user).getChannel().broadcastMessage("Server", username + " left the chat", "message", userHandler.userUsernameMap, channelHandler.getChannelNames());
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        if (message.startsWith("#username#*")) {
            if (userHandler.uniqueUsername(decode(message)))
                userHandler.addUser(user, message);
            else {
                userHandler.retryLogin(user);
                return;
            }
        } else if (message.startsWith("#addChannel#*")) {
            if (channelHandler.uniqueChannelName(decode(message))) {
                channelHandler.addChannel(decode(message));
            } else {
                userHandler.retryNamingChannel(user, channelHandler.getChannelNames());
                return;
            }

        } else if (message.startsWith("#joinChannel#*")) {
            userHandler.joinChannel(user, decode(message));
            userHandler.sendToUser(user, userHandler.userUsernameMap.get(user), "You are currently in " + decode(message) + " channel");
        } else
            userHandler.userUsernameMap.get(user).getChannel().broadcastMessage(userHandler.userUsernameMap.get(user).getName(), message, "message", userHandler.userUsernameMap, channelHandler.getChannelNames());
        channelHandler.refreshChannelList(userHandler.userUsernameMap);
        userHandler.userUsernameMap.get(user).getChannel().refreshChannelUsersList(userHandler.userUsernameMap);

    }

    private String decode(String message) {
        return message.substring(message.indexOf('*') + 1);
    }

}