import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
/** 管理在轨和归档集合，统一校验唯一编号和轨道资源。 */
public final class SatelliteManager implements Serializable {
    private final ArrayList<Satellite> current = new ArrayList<Satellite>(10);
    private final ArrayList<Satellite> archived = new ArrayList<Satellite>(10);
    public void preload() {
        register("CN0001", "天问", "1.2", "科学探测；定期校准");
        register("CN0002", "北斗", "1.4", "导航定位；注意时钟同步");
        register("US0001", "远望", "1.6", "通信中继；注意频率协调");
        register("RU0001", "极光", "1.8", "气象观测；注意传感器温度");
        register("FR0001", "晨星", "2.0", "遥感成像；注意数据保密");
    }
    public void register(String id, String name, String orbitText, String description) {
        BigDecimal orbit;
        try {
            orbit = new BigDecimal(orbitText);
        }
        catch (NumberFormatException ex) {
            throw new IllegalArgumentException("轨道必须为有限十进制数");
        }
        for (Satellite s : search("")) if (s.getId().equals(id)) throw new IllegalArgumentException("COSPARID已使用（含归档）");
        for (Satellite s : current) if (s.getOrbit().subtract(orbit).abs().compareTo(new BigDecimal("0.2")) < 0) throw new IllegalArgumentException("轨道间距不得小于0.2，冲突编号: " + s.getId());
        Satellite s = id != null && id.startsWith("CN") ? new ChinaSatellite(id, name, orbit, description) : new InternationalSatellite(id, name, orbit, description);
        current.add(s);
    }
    public List<Satellite> list(boolean archive) {
        return new ArrayList<Satellite>(archive ? archived : current);
    }
    public Satellite find(String id) {
        for (Satellite s : search("")) if (s.getId().equals(id)) return s;
        throw new IllegalArgumentException("未找到卫星: " + id);
    }
    public List<Satellite> search(String query) {
        ArrayList<Satellite> result = new ArrayList<Satellite>(10);
        ArrayList<Satellite> all = new ArrayList<Satellite>(current);
        all.addAll(archived);
        for (Satellite s : all) if (s.getId().toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT)) || s.getName().contains(query))  result.add(s);
        return result;
    }
    public void delete(String id) {
        Satellite s = find(id);
        s.archive();
        current.remove(s);
        archived.add(s);
    }
}