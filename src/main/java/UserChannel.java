import lombok.Getter;

/**
 * Created by robert on 22.01.17.
 */
@Getter
public class UserChannel extends AbstractChannel{
    UserChannel(String channelName){
        super(channelName);
    }
}
