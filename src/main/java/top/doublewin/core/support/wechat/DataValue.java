package top.doublewin.core.support.wechat;

/**
 * <p>
 * 微信消息内容值
 * </p>
 *
 * @author migro
 * @since 2019/7/8 17:59
 */
public class DataValue {

    static final String DEFAULT_COLOR = "#173177";
    static final String RED_COLOR = "#FF0000";

    private String value;
    private String color;

    public DataValue(String value){
        this(value,DEFAULT_COLOR);
    }

    public DataValue(String value,String color){
        this.value = value;
        this.color = color;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
