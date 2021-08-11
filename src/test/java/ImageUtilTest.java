import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import com.alibaba.fastjson.JSON;
import me.chanjar.weixin.common.error.WxErrorException;
import org.junit.Test;
import top.doublewin.core.support.security.coder.MDCoder;
import top.doublewin.core.util.ImageUtil;
import top.doublewin.model.ExcludeSet;

import java.util.Set;

/**
 * <p>
 * 类描述
 * </p>
 *
 * @author migro
 * @since 2020/7/20 17:01
 */
public class ImageUtilTest {

    //@Test
    public void qrcode(){
        String url = "http://news.sina.com.cn";
        String logo = "http://110.188.23.173:9999/net-medical/picture/springboot.png";
        try {
            System.out.println(ImageUtil.genLogoQRCodeBase64(url,logo));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //@Test
    public void test(){
        ExcludeSet set = new ExcludeSet();
        set.add("1").add("2");
        System.out.println(set);
        System.out.println(set.contains("1"));
        System.out.println(set.contains("0"));
    }

    //@Test
    public void matest(){
        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid("wxb43928ccc48a825b");
        config.setSecret("9b1b96980ef6e1ee6e4a5f316efd77af");
        WxMaService wxService = new WxMaServiceImpl();
        wxService.setWxMaConfig(config);
        try {
            WxMaJscode2SessionResult session = wxService.getUserService().getSessionInfo("091EiOGa1bliEz0EYVGa1j7Vqx1EiOGz");
            System.out.println(JSON.toJSONString(session));
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
    }

    //@Test
    public void imageToBase64(){
        String url = "http://192.168.0.11:9999/net-medical/picture/eabef0a3-04c3-4c9f-aedf-5631b59c7998.jpg";
        String code = ImageUtil.imageToBase64(url);
        System.out.println(code);
    }

    //@Test
    public void  md5ForFile(){
        String url = "http://192.168.0.11:9999/net-medical/picture/eabef0a3-04c3-4c9f-aedf-5631b59c7998.jpg";
        try {
            System.out.println(MDCoder.encodeMD5(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
