import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by robert on 22.01.17.
 */
@Getter
@Setter
@AllArgsConstructor
public class User {
    private String name;
    private IChannel Channel;
}
