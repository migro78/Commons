package top.doublewin.core.util;

import java.util.regex.Pattern;

/**
 * <p>
 * 正则表达式辅助类
 * </p>
 *
 * @author migro
 * @since 2020/11/2 16:54
 */
public class PatternUtil {

    /**
     * 四则运算表达式
     **/
    public static String fourMixed = "(?<!\\d)-?\\d+(\\.\\d+)?|[+\\-*/()]";
    public static Pattern fourMixedPattern = Pattern.compile(fourMixed);

    /**
     * 四则运算表达式(带参数)
     **/
    public static String fourMixedWithParam = "(?<!\\d)-?\\d+(\\.\\d+)?|[+\\-*/()]|\\w+";
    public static Pattern fourMixedWithParamPattern = Pattern.compile(fourMixedWithParam);

    /**
     * 模板参数
     **/
    public static String param = "\\$\\{[\\w+\\-*/()\\s]+\\}";
    public static Pattern paramPattern = Pattern.compile(param);

    /**
     * 运算符号
     **/
    public static String operSymbol = "[+\\-*/()]";
    public static Pattern operSymbolPattern = Pattern.compile(operSymbol);


}
