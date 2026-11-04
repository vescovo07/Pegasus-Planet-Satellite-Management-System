import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/** 共享身份、轨道、状态与审计行为；所有业务修改均通过方法进行。 */
public abstract class Satellite implements Lifecycle, Serializable {
    private final String cosparId;
    private final BigDecimal orbit;
    private final LocalDateTime registeredAt = LocalDateTime.now();
    private final ArrayList<ChangeRecord> history = new ArrayList<ChangeRecord>(10);
    private String name;
    private String description;
    private String status = "ACTIVATED";

    protected Satellite(String id, String name, BigDecimal orbit, String description) {
        if (id == null || !id.matches("[A-Z]{2}[0-9]{4}")) throw new IllegalArgumentException("编号必须为两位大写国家编码和四位数字");
        if (orbit == null || orbit.compareTo(new BigDecimal("1.2")) < 0) throw new IllegalArgumentException("轨道不得低于1.2");
        this.cosparId = id;
        this.name = requireText(name);
        this.orbit = orbit;
        this.description = requireText(description);
        record("注册 " + name);
    }
    /** 子类提供所属国家的显示名称。 */
    public abstract String country();
    public String getId() {
        return cosparId;
    }
    public String getName() {
        return name;
    }
    public BigDecimal getOrbit() {
        return orbit;
    }
    public String getStatus() {
        return status;
    }
    public List<ChangeRecord> getHistory() {
        return Collections.unmodifiableList(new ArrayList<ChangeRecord>(history));
    }
    public String requireText(String text) {
        if (text == null || text.trim().isEmpty() || text.length() > 200) throw new IllegalArgumentException("文本不能为空且不能超过200字符");
        return text.trim();
    }
    private void record(String action) {
        history.add(new ChangeRecord(action));
    }
    private void requireActive() {
        if (!"ACTIVATED".equals(status)) throw new IllegalStateException("仅激活卫星可修改或删除；封锁卫星请先激活");
    }
    public void rename(String value) {
        requireActive();
        String next = requireText(value);
        if (name.equals(next)) throw new IllegalArgumentException("名称未变化");
        record("名称: " + name + " -> " + next);
        name = next;
    }
    public void describe(String value) {
        requireActive();
        String next = requireText(value);
        if (description.equals(next)) throw new IllegalArgumentException("描述未变化");
        record("功能与注意事项: " + description + " -> " + next);
        description = next;
    }
    public void activate() {
        if (!"DEACTIVATED".equals(status)) throw new IllegalStateException("只可激活封锁卫星");
        status = "ACTIVATED";
        record("DEACTIVATED -> ACTIVATED");
    }
    public void deactivate() {
        requireActive();
        status = "DEACTIVATED";
        record("ACTIVATED -> DEACTIVATED");
    }
    public void archive() {
        requireActive();
        status = "DELETE";
        record("ACTIVATED -> DELETE，删除并归档");
    }
    public String summary() {
        return cosparId + " | " + name + " | " + country() + " | " + orbit + " | " + status;
    }
    public String detail() {
        return summary() + "\n功能与注意事项: " + description + "\n注册时间: " + registeredAt;
    }
}