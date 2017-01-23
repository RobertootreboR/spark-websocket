import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by robert on 23.01.17.
 */
@Getter
@Setter
@AllArgsConstructor
public abstract class AbstractChannel implements IChannel{
    String ChannelName;

}
