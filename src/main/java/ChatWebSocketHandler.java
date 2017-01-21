import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

@WebSocket
public class ChatWebSocketHandler {

    private String sender, msg;
    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        String username = Chat.userUsernameMap.get(user);
        Chat.userUsernameMap.remove(user);
        Chat.broadcastMessage(sender = "Server", msg = (username + " left the chat"));
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        if(message.startsWith("#username#*")){
            String username = message.substring(message.indexOf('*') + 1);
           Chat.userUsernameMap.put(user, username);
            Chat.broadcastMessage(sender = "Server", msg = (username + " joined the chat"));
        }
        else Chat.processMessage(sender = Chat.userUsernameMap.get(user), msg = message);
    }

}
