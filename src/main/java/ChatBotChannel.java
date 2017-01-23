import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by robert on 23.01.17.
 */
public class ChatBotChannel extends AbstractChannel {
    WeatherForecast forecast = new WeatherForecast();
    ChatBotChannel(String channelName){
        super(channelName);
    }

    public void processMessage(String sender, String message, String reason, Map<Session, User> userUsernameMap,List<String> channels){
        broadcastMessage(sender, message,"message",userUsernameMap,channels);
        if (message.equals("What's the weather in Krakow?"))
            broadcastMessage("Server", forecast.update(),"message",userUsernameMap,channels);
        else if (message.equals("What's the time?"))
            broadcastMessage("Server", new SimpleDateFormat("HH:mm:ss").format(new Date()),"message",userUsernameMap,channels);
        else if (message.equals("What's the day today?"))
            broadcastMessage("Server", new Date().toString().split(" ")[0],"message",userUsernameMap,channels);
        else if(message.equals("HELP!"))
            broadcastMessage("Server", "Possible options: "+
                                        "\'What's the weather in Krakow?\' \n" +
                                        "\'What's the time?\' \n" +
                                        "\'What's the day today?\' \n"
                                     ,"message",userUsernameMap,channels);
        else if(! message.endsWith("?"))
            broadcastMessage("Server", "That's not even a question!","message",userUsernameMap,channels);
         else broadcastMessage("Server", "I don't know the answer. Write 'HELP!' to see possible options","message",userUsernameMap,channels);
    }
}
