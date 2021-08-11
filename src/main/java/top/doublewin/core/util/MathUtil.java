package top.doublewin.core.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final strictfp class MathUtil {
    protected static Logger logger = LogManager.getLogger();


    private MathUtil() {
    }

    // 默认运算精度
    private static int DEF_SCALE = 2;

    /**
     * 提供数据类型转换为BigDecimal
     *
     * @param object 原始数据
     * @return BigDecimal
     */
    public static final BigDecimal bigDecimal(Object object) {
        if (object == null) {
            throw new NullPointerException();
        }
        BigDecimal result;
        try {
            result = new BigDecimal(String.valueOf(object).replaceAll(",", ""));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Please give me a numeral.Not " + object);
        }
        return result;
    }

    /**
     * 提供(相对)精确的加法运算。
     *
     * @param num1 被加数
     * @param num2 加数
     * @return 两个参数的和
     */
    public static final Double add(Object num1, Object num2) {
        BigDecimal result = bigDecimal(num1).add(bigDecimal(num2));
        return result.setScale(DEF_SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 提供(相对)精确的减法运算。
     *
     * @param num1 被减数
     * @param num2 减数
     * @return 两个参数的差
     */
    public static final Double subtract(Object num1, Object num2) {
        BigDecimal result = bigDecimal(num1).subtract(bigDecimal(num2));
        return result.setScale(DEF_SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 提供(相对)精确的乘法运算。
     *
     * @param num1 被乘数
     * @param num2 乘数
     * @return 两个参数的积
     */
    public static final Double multiply(Object num1, Object num2) {
        BigDecimal result = bigDecimal(num1).multiply(bigDecimal(num2));
        return result.setScale(DEF_SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 提供(相对)精确的除法运算，当发生除不尽的情况时，精度为10位，以后的数字四舍五入。
     *
     * @param num1 被除数
     * @param num2 除数
     * @return 两个参数的商
     */
    public static final Double divide(Object num1, Object num2) {
        return divide(num1, num2, DEF_SCALE);
    }

    /**
     * 提供(相对)精确的除法运算。 当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入。
     *
     * @param num1  被除数
     * @param num2  除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static final Double divide(Object num1, Object num2, Integer scale) {
        if (scale == null) {
            scale = DEF_SCALE;
        }
        num2 = num2 == null || Math.abs(new Double(num2.toString())) == 0 ? 1 : num2;
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal result = bigDecimal(num1).divide(bigDecimal(num2), scale, BigDecimal.ROUND_HALF_UP);
        return result.doubleValue();
    }

    /**
     * 提供精确的小数位四舍五入处理。
     *
     * @param num   需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static final Double round(Object num, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal result = bigDecimal(num).divide(bigDecimal("1"), scale, BigDecimal.ROUND_HALF_UP);
        return result.doubleValue();
    }

    /**
     * 获取start到end区间的随机数,不包含start+end
     *
     * @param start
     * @param end
     * @return
     */
    public static final BigDecimal getRandom(double start, double end) {
        return new BigDecimal(start + Math.random() * (end - start));
    }

    /**
     * 格式化
     *
     * @param obj
     * @param pattern
     * @return
     */
    public static final String format(Object obj, String pattern) {
        if (obj == null) {
            return null;
        }
        if (pattern == null || "".equals(pattern)) {
            pattern = "#";
        }
        DecimalFormat format = new DecimalFormat(pattern);
        return format.format(bigDecimal(obj));
    }

    /**
     * 是否数字
     */
    public static final boolean isNumber(Object object) {
        Pattern pattern = Pattern.compile("\\d+(.\\d+)?$");
        return pattern.matcher(object.toString()).matches();
    }


    /**
     * @return
     * @Author aw
     * 生成n位随机数
     * @Param num
     */
    public static String getNumRandomNumber(int num) {
        if (num < 1) {
            throw new RuntimeException("输入有误!");
        }
        int ranInt = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < num; i++) {
            double dou = Math.random() * 10;
            ranInt = (int) dou;
            sb.append(ranInt);
        }
        return sb.toString();
    }

    /**
     * 四则混合运算（加减乘除）
     *
     * @param express 算式
     * @return 计算结果
     */
    public static String fourMixedOperations(String express) throws Exception {
        logger.debug("四则混合运算，算式：{}", express);
        // 数字栈
        Stack<Object> numberStk = new Stack<>();
        // 符号栈，初始化空栈顶
        Stack<String> operStk = new Stack<>();
        operStk.push(null);

        // 将express打散为运算数和运算符,这个正则为匹配表达式中的数字或运算符
        Matcher m = PatternUtil.fourMixedPattern.matcher(express);
        while (m.find()) {
            String temp = m.group();
            //遇到符号
            if (temp.matches(PatternUtil.operSymbol)) {
                //遇到左括号，直接入符号栈
                if (temp.equals("(")) {
                    operStk.push(temp);
                } else if (temp.equals(")")) {
                    //遇到右括号，"符号栈弹栈取栈顶符号b，数字栈弹栈取栈顶数字a1，数字栈弹栈取栈顶数字a2，计算a2 b a1 ,将结果压入数字栈"，重复引号步骤至取栈顶为左括号，将左括号弹出
                    String b = null;
                    while (!(b = operStk.pop()).equals("(")) {
                        calcAndPush(numberStk, b);
                    }
                } else {//遇到运算符，满足该运算符的优先级大于栈顶元素的优先级压栈；否则计算后压栈
                    while (getOperPriority(temp) <= getOperPriority(operStk.peek())) {
                        String b = operStk.pop();
                        calcAndPush(numberStk, b);
                    }
                    operStk.push(temp);
                }
            } else {//遇到数字，直接压入数字栈
                numberStk.push(bigDecimal(temp));
            }
        }

        //遍历结束后，符号栈数字栈依次弹栈计算，并将结果压入数字栈
        while (operStk.peek() != null) {
            String b = operStk.pop();
            calcAndPush(numberStk, b);
        }

        Object ret = numberStk.pop();
        logger.debug("运算结果类型 {}",ret.getClass().getName());
        return ret + "";
    }

    /**
     * 二元栈计算
     *
     * @param
     * @return
     */
    private static void calcAndPush(Stack<Object> numberStk, String oper) throws Exception {
        Object a1 = numberStk.pop();
        Object a2 = numberStk.pop();
        logger.debug("二元计算 {} {} {}", a2, oper, a1);
        numberStk.push(twoOperations(a2, a1, oper.charAt(0)));
        logger.debug("数字栈更新：" + numberStk);
    }

    /**
     * 二元运算
     *
     * @param
     * @return
     */
    private static Object twoOperations(Object num1, Object num2, char oper) throws Exception {
        switch (oper) {
            case '+':
                return add(num1, num2);
            case '-':
                return subtract(num1, num2);
            case '*':
                return multiply(num1, num2);
            case '/':
                return divide(num1, num2);
            default:
                break;
        }
        throw new Exception("illegal operator!");
    }

    /**
     * 根据运算符号获得四则运算优先级 ( ==> 1, + - ==> 2 , * / ==> 3
     *
     * @param oper 运算符号
     * @return 优先级数
     */
    private static int getOperPriority(String oper) throws Exception {
        if (oper == null) {
            return 0;
        }
        switch (oper) {
            case "(":
                return 1;
            case "+":
                return 2;
            case "-":
                return 2;
            case "*":
                return 3;
            case "/":
                return 3;
            default:
                break;
        }
        throw new Exception("illegal operator!");
    }

    public static final void main(String[] args) {

    }
}
