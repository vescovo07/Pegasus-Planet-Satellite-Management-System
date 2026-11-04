import java.io.*;
import java.nio.file.*;
/** 本地数据读取与保存。这里先写临时文件再替换，避免半写入破坏旧数据。 */
public final class DataStore {
    private final Path file;
    public DataStore(String path) { file = Paths.get(path); }
    public SatelliteManager load() throws IOException, ClassNotFoundException {
        if (!Files.exists(file)) {
            SatelliteManager manager = new SatelliteManager();
            manager.preload();
            save(manager);
            return manager;
        }
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
            return (SatelliteManager) in.readObject();
        }
    }
    public void save(SatelliteManager manager) throws IOException {
        Path temp = Paths.get(file.toString() + ".tmp");
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(temp))) {
            out.writeObject(manager);
        }
        try {
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        }
        catch (AtomicMoveNotSupportedException ex) {
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}