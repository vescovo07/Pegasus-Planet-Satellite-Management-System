import java.math.BigDecimal;
/** 中国卫星扩展，以 CN 为国家编码。 */
public final class ChinaSatellite extends Satellite {
    public ChinaSatellite(String id, String name, BigDecimal orbit, String description) {
        super(id, name, orbit, description);
        if (!id.startsWith("CN")) 
            throw new IllegalArgumentException("中国编号必须以CN开头");
    }
    public String country() {
        return "中国";
    }
}