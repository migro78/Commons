package top.doublewin.core.support.wechat;

/**
 * <p>
 * 微信模板参数
 * </p>
 *
 * @author migro
 * @since 2019/7/9 11:11
 */
public class TempletParam {
    private String id;
    private String url;
    private String first;
    private String remark;

    public TempletParam(String id, String url, String first, String remark) {
        this.id = id;
        this.url = url;
        this.first = first;
        this.remark = remark;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
