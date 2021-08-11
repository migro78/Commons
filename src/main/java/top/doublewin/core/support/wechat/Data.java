package top.doublewin.core.support.wechat;

/**
 * 微信消息内容
 * @author rxf113
 */
public class Data {

    private DataInfo first;

    private DataInfo keyword1;

    private DataInfo keyword2;

    private DataInfo keyword3;

    private DataInfo keyword4;

    private DataInfo keyword5;

    private DataInfo remark;

    private DataInfo orderMoneySum;

    private DataInfo orderProductName;

    public DataInfo getOrderMoneySum() {
        return orderMoneySum;
    }

    public void setOrderMoneySum(DataInfo orderMoneySum) {
        this.orderMoneySum = orderMoneySum;
    }

    public DataInfo getOrderProductName() {
        return orderProductName;
    }

    public void setOrderProductName(DataInfo orderProductName) {
        this.orderProductName = orderProductName;
    }

    public DataInfo getFirst() {
        return first;
    }

    public void setFirst(DataInfo first) {
        this.first = first;
    }

    public DataInfo getKeyword1() {
        return keyword1;
    }

    public void setKeyword1(DataInfo keyword1) {
        this.keyword1 = keyword1;
    }

    public DataInfo getKeyword2() {
        return keyword2;
    }

    public void setKeyword2(DataInfo keyword2) {
        this.keyword2 = keyword2;
    }

    public DataInfo getKeyword3() {
        return keyword3;
    }

    public void setKeyword3(DataInfo keyword3) {
        this.keyword3 = keyword3;
    }

    public DataInfo getKeyword4() {
        return keyword4;
    }

    public void setKeyword4(DataInfo keyword4) {
        this.keyword4 = keyword4;
    }

    public DataInfo getKeyword5() {
        return keyword5;
    }

    public void setKeyword5(DataInfo keyword5) {
        this.keyword5 = keyword5;
    }

    public DataInfo getRemark() {
        return remark;
    }

    public void setRemark(DataInfo remark) {
        this.remark = remark;
    }
}
