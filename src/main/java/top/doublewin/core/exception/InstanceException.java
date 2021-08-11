/**
 * 
 */
package top.doublewin.core.exception;

import top.doublewin.core.support.http.HttpCode;

/**
 * 
 * @author ShenHuaJie
 * @version 2017年3月24日 下午9:30:10
 */
@SuppressWarnings("serial")
public class InstanceException extends BaseException {
    public InstanceException() {
        super();
    }

    public InstanceException(Throwable t) {
        super(t);
    }

    protected HttpCode getCode() {
        return HttpCode.INTERNAL_SERVER_ERROR;
    }
}
