import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by robert on 23.01.17.
 */
@Getter
@AllArgsConstructor
public abstract class AbstractChannel implements IChannel{
    String ChannelName;

}
