package support.lfp.toolkit.action;

/**
 * <pre>
 * Tip:
 *
 * Function:
 *
 * Created by LiFuPing on 2019/3/21 13:45
 * </pre>
 */
@FunctionalInterface
public interface Callback1<T,R> {
    R call(T t);
}
