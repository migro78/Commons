package top.doublewin.core.support.wechat;

import io.swagger.annotations.ApiModelProperty;

/**
 * 消息内容
 * @author rxf113
 */
public class DataInfo {
    @ApiModelProperty("value")
    private String value;
    @ApiModelProperty("颜色默认黑色")
    private String color;

    public DataInfo(String value,String color){
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
