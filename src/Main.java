import java.util.List;
import java.util.Scanner;
/** 字符终端入口。菜单循环与业务对象分离。 */
public final class Main {
    private final Scanner input = new Scanner(System.in, "UTF-8");
    private final DataStore store = new DataStore("satellites.dat");
    private SatelliteManager manager;
    public static void main(String[] args) { new Main().run(); }
    public void showMenu() {
        System.out.println("\n=== 飞马星球卫星微管理系统 ===");
        System.out.println("1 当前列表  2 注册卫星  3 删除归档  4 激活  5 封锁");
        System.out.println("6 失活列表  7 模糊查询  8 修改名称  9 功能与注意事项");
        System.out.println("10 修改历史  11 归档列表  0 退出");
    }
    private String read(String prompt) {
        System.out.print(prompt + ": ");
        if (!input.hasNextLine()) {
            System.out.println("输入结束，退出。"); System.exit(0);
        }
        return input.nextLine().trim();
    }
    private void printList(List<Satellite> list, boolean inactive) {
        int count = 0;
        System.out.println("COSPARID | 名称 | 国家 | 轨道 | 状态");
        for (Satellite s : list)
            if (!inactive || "DEACTIVATED".equals(s.getStatus())) {
                System.out.println(s.summary()); count++;
            }
        System.out.println("共 " + count + " 颗");
    }
    private void run() {
        try {
            manager = store.load();
        }
        catch (Exception ex) {
            System.err.println("数据加载失败，停止以保护原文件: " + ex); return;
        }
        String choice;
        do {
            showMenu();
            choice = read("选择");
            try {
                execute(choice);
                store.save(manager);
            } catch (IllegalArgumentException | IllegalStateException ex) {
                System.out.println("操作拒绝: " + ex.getMessage());
            } catch (java.io.IOException ex) {
                System.err.println("保存失败，停止操作。请检查磁盘权限: " + ex.getMessage());
                return;
            }
        } while (!"0".equals(choice));
        input.close();
        System.out.println("数据已保存，再见。");
        System.exit(0);
    }
    private void execute(String choice) {
        switch (choice) {
            case "0": break;
            case "1": printList(manager.list(false), false); break;
            case "2":
                manager.register(read("COSPARID"), read("名称"), read("轨道"), read("功能与注意事项"));
                System.out.println("注册成功"); break;
            case "3": manager.delete(read("COSPARID")); System.out.println("删除并归档成功"); break;
            case "4": manager.find(read("COSPARID")).activate(); System.out.println("激活成功"); break;
            case "5": manager.find(read("COSPARID")).deactivate(); System.out.println("封锁成功"); break;
            case "6": printList(manager.list(false), true); break;
            case "7":
                String query = read("ID或名称关键词");
                if (query.isEmpty()) throw new IllegalArgumentException("查询词不能为空");
                List<Satellite> matches = manager.search(query);
                for (Satellite s : matches) System.out.println(s.detail());
                System.out.println("匹配 " + matches.size() + " 颗（含归档）"); break;
            case "8": manager.find(read("COSPARID")).rename(read("新名称")); System.out.println("名称修改成功"); break;
            case "9":
                Satellite s = manager.find(read("COSPARID"));
                System.out.println(s.detail());
                String action = read("输入1修改，0返回");
                if ("1".equals(action)) {
                    s.describe(read("新功能与注意事项"));
                    System.out.println("描述修改成功");
                }
                else if (!"0".equals(action)) throw new IllegalArgumentException("请选择0或1");
                break;
            case "10":
                Satellite target = manager.find(read("COSPARID"));
                System.out.println(target.detail());
                for (ChangeRecord record : target.getHistory()) System.out.println(record);
                break;
            case "11": printList(manager.list(true), false); break;
            default: throw new IllegalArgumentException("菜单选项应为0至11");
        }
    }
}