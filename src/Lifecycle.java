/** 定义卫星生命周期；封锁对象只允许重新激活。 */
public interface Lifecycle {
    void activate();
    void deactivate();
    void archive();
}