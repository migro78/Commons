package top.doublewin.core.support.context;

/**
 * @author ShenHuaJie
 * @since 2018年8月26日 上午11:58:44
 */
public enum RegexType {
    /** 不验证 */
    NONE(null),
    /** 不含特殊字符 */
    SPECIALCHAR(""),
    /** 不含中文 */
    NONECHINESE(""),
    /** 身份证号 */
    IDCARD(""),
    /** 年([-./_]月([-./_][日] */
    DATE("[1-9]{4}([-./_]?)(0?[1-9]|1[0-2])([-./_]?)((0?[1-9])|((1|2)[0-9])|30|31)?"),
    /** 强密码 */
    PASSWORD("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,18}$"),
    /** 纯中文 */
    CHINESE("^[\u4E00-\u9FA5]+$"),
    /** 邮箱 */
    EMAIL("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$"),
    /** IP */
    IP("^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$"),
    /** 网址 */
    URL("(http://|ftp://|https://|www){0,1}[^\u4e00-\u9fa5\\s]*?\\.(com|net|cn|me|tw|fr)[^\u4e00-\u9fa5\\s]*"),
    /** 数字 */
    NUMBER("^(-?\\d+)(\\.\\d+)?$"),
    /** 手机号 */
    PHONE("^((1[3,5,8][0-9])|(14[5,7])|(17[0,6,7,8])|(19[7]))\\d{8}$"),
    /** 电话号 */
    TELEPHONE("^(0\\d{2}-\\d{8}(-\\d{1,4})?)|(0\\d{3}-\\d{7,8}(-\\d{1,4})?)$");

    private String value;

    RegexType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
