package springeventdemo;

import org.springframework.context.ApplicationEvent;

/**
 * 定义一个事件
 *
 * @author jiangyuanlin@163.com
 */
public class ScoreEvent extends ApplicationEvent {

    /**
     * @param source 参数可以传入任意类型的类型，比如传入json字符串
     */
    public ScoreEvent(Object source) {
        super(source);
    }

}
