package support.lfp.toolkit.action;

/**
 * <pre>
 * Tip:
 *      只有一个参数的回调
 *
 * Created by LiFuPing on 2018/10/29 14:36
 * </pre>
 */
@FunctionalInterface
public interface Action3<T1,T2,T3> {
    void call(T1 t1, T2 t2,T3 t3);
}
