import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

@WebSocket
public class ChatWebSocketHandler {
    public MessageHandler handle = new MessageHandler();
    private String sender, msg;
    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        String username = Chat.userUsernameMap.get(user);
        Chat.userUsernameMap.remove(user);
        Chat.broadcastMessage(sender = "Server", msg = (username + " left the chat"),"message");
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        if(message.startsWith("#username#*")){
            if(handle.uniqueUsername(decode(message)))
                 handle.addUser(user,message);
            else handle.retryLogin(user);
        }else
        if(message.startsWith("#addChannel#*")){
            if(handle.uniqueChannelName(decode(message)))
                handle.addChannel(user,decode(message));
            else handle.getChannelName(user);

        }
        else Chat.processMessage(sender = Chat.userUsernameMap.get(user), msg = message);
    }

    String decode(String message){
        return message.substring(message.indexOf('*') + 1);
    }

}
