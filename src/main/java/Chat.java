import org.eclipse.jetty.websocket.api.*;
import org.json.*;

import java.text.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static j2html.TagCreator.*;
import static spark.Spark.*;

public class Chat {
    static WeatherForecast forecast = new WeatherForecast();
    // this map is shared between sessions and threads, so it needs to be thread-safe (http://stackoverflow.com/a/2688817)
    static Map<String, String> userToChannel = new ConcurrentHashMap<>();
    static List<String> channels = new CopyOnWriteArrayList<String>();
    static AtomicInteger nextChannelNumber=new AtomicInteger(0);
    static Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();
    static int nextUserNumber = 1; //Assign to username for next connecting user

    public static void main(String[] args) {
        staticFiles.location("/public"); //index.html is served at localhost:4567 (default port)
        staticFiles.expireTime(600);
        channels.add("Default");
        webSocket("/chat", ChatWebSocketHandler.class);
        init();
    }

    public static void processMessage(String sender, String message) {
        broadcastMessage(sender, message,"message");
        if (message.equals("what's the weather in Krakow?"))
            broadcastMessage("Server", forecast.update(),"message");
        else if (message.equals("What's the time?"))
            broadcastMessage("Server", new SimpleDateFormat("HH:mm:ss").format(new Date()),"message");
        else if (message.equals("What's the day today?"))
            broadcastMessage("Server", new Date().toString().split(" ")[0],"message");
    }

    //Sends a message from one user to all users, along with a list of current usernames
    public static void broadcastMessage(String sender, String message,String reason) {
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(String.valueOf(new JSONObject()
                        .put("userMessage", createHtmlMessageFromSender(sender, message))
                        .put("channellist", channels)
                        .put("reason", reason)
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    //Builds a HTML element with a sender-name, a message, and a timestamp,
    private static String createHtmlMessageFromSender(String sender, String message) {
        return article().with(
                b(sender + " says:"),
                p(message),
                span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(new Date()))
        ).render();
    }

}
