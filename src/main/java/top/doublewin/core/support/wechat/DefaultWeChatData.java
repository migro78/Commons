package top.doublewin.core.support.wechat;

import top.doublewin.core.util.DataUtil;

/**
 * <p>
 * 默认使用得微信消息Data元素对象
 * </p>
 *
 * @author migro
 * @since 2019/7/8 18:12
 */
public class DefaultWeChatData extends BaseData {

    private DataValue keyword1;

    private DataValue keyword2;

    private DataValue keyword3;

    private DataValue keyword4;

    private DataValue keyword5;

    private DataValue keyword6;

    public DefaultWeChatData() {

    }

    public DefaultWeChatData(DataValue first, DataValue remark, DataValue... values) {
        this.setFirst(first);
        this.setRemark(remark);
        if(DataUtil.isNotEmpty(values)){
            int len = values.length;
            if(len>0){
                keyword1 = values[0];
            }
            if(len>1){
                keyword2 = values[1];
            }
            if(len>2){
                keyword3 = values[2];
            }
            if(len>3){
                keyword4 = values[3];
            }
            if(len>4){
                keyword5 = values[4];
            }
            if(len>5){
                keyword6 = values[5];
            }
        }
    }


    public DataValue getKeyword1() {
        return keyword1;
    }

    public void setKeyword1(DataValue keyword1) {
        this.keyword1 = keyword1;
    }

    public DataValue getKeyword2() {
        return keyword2;
    }

    public void setKeyword2(DataValue keyword2) {
        this.keyword2 = keyword2;
    }

    public DataValue getKeyword3() {
        return keyword3;
    }

    public void setKeyword3(DataValue keyword3) {
        this.keyword3 = keyword3;
    }

    public DataValue getKeyword4() {
        return keyword4;
    }

    public void setKeyword4(DataValue keyword4) {
        this.keyword4 = keyword4;
    }

    public DataValue getKeyword5() {
        return keyword5;
    }

    public void setKeyword5(DataValue keyword5) {
        this.keyword5 = keyword5;
    }

    public DataValue getKeyword6() {
        return keyword6;
    }

    public void setKeyword6(DataValue keyword6) {
        this.keyword6 = keyword6;
    }
}
