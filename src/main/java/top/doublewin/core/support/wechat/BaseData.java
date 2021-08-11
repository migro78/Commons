package top.doublewin.core.support.wechat;

/**
 * <p>
 * 微信消息数据体基类
 * </p>
 *
 * @author migro
 * @since 2019/7/8 17:59
 */
public class BaseData {

    private DataValue first;
    private DataValue remark;

    public DataValue getFirst() {
        return first;
    }

    public void setFirst(DataValue first) {
        this.first = first;
    }

    public DataValue getRemark() {
        return remark;
    }

    public void setRemark(DataValue remark) {
        this.remark = remark;
    }
}
