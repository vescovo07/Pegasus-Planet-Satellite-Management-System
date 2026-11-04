import java.math.BigDecimal;
/** 其他国家扩展；允许任意两位大写拉丁字母作为自定义国家编码。 */
public final class InternationalSatellite extends Satellite {
    public InternationalSatellite(String id, String name, BigDecimal orbit, String description) {
        super(id, name, orbit, description);
        if (id.startsWith("CN")) {
            throw new IllegalArgumentException("CN应使用中国卫星类");
        }
    }
    public String country() {
        return "国家编码 " + getId().substring(0, 2);
    }
}