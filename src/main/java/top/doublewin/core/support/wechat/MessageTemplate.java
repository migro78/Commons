package top.doublewin.core.support.wechat;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * 微信消息模板
 * @author rxf113
 */
public class MessageTemplate {
    @ApiModelProperty("接收者openid")
    private String touser;
    @ApiModelProperty("模板ID")
    private String template_id;
    @ApiModelProperty("模板跳转链接（海外帐号没有跳转能力）")
    private String url;
    @ApiModelProperty("跳小程序所需数据，不需跳小程序可不用传该数据")
    //miniprogram":{
    //所需跳转到的小程序appid（该小程序appid必须与发模板消息的公众号是绑定关联关系，暂不支持小游戏）
    // "appid":"xiaochengxuappid12345",
    //所需跳转到小程序的具体页面路径，支持带参数,（示例index?foo=bar），要求该小程序已发布，暂不支持小游戏
    // "pagepath":"index?foo=bar"
    private List<Map<String,String>> miniprogram;
    @ApiModelProperty("发送内容")
    private Data data;

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Map<String, String>> getMiniprogram() {
        return miniprogram;
    }

    public void setMiniprogram(List<Map<String, String>> miniprogram) {
        this.miniprogram = miniprogram;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
