import org.junit.Test;
import top.doublewin.core.util.DataUtil;
import top.doublewin.core.util.InstanceUtil;
import top.doublewin.core.util.MathUtil;
import top.doublewin.core.util.PatternUtil;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 类描述
 * </p>
 *
 * @author migro
 * @since 2020/11/2 14:48
 */
public class PatternTest {

    @Test
    public void fourMixOper() {
        // 四则混合运算测试
        String expr = "-3.5*(4.5-(4+(-1-1/2)))";
        try {
            System.out.println(MathUtil.fourMixedOperations(expr));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void stringMatches(){
        String param = "weight / (height * height)";
        System.out.println(param.matches("\\w*\\s*[+\\-*/]\\s*\\w*"));
    }

    @Test
    public void fourMixOperWithParam() {
        // 带参数的四则混合运算
        String str = "我的身高${ height},我的体重${weight },我的BMI计算结果是${ weight / (height * height)}，距离正常值 ${24 - weight / ( height * height ) }"; //待判断的字符串
        Map<String,Object> map = InstanceUtil.newHashMap();
        map.put("weight",70);
        map.put("height",1.68);
        System.out.println(DataUtil.paramsFill(str,map));
    }
}
