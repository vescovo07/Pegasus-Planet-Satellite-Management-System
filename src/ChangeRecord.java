import java.io.Serializable;
import java.time.LocalDateTime;
/** 不可变的系统审计记录。 */
public final class ChangeRecord implements Serializable {
    private final LocalDateTime time = LocalDateTime.now();
    private final String action;
    public ChangeRecord(String action) {
        this.action = action;
    }
    public String toString() {
        return time + " | " + action;
    }
}