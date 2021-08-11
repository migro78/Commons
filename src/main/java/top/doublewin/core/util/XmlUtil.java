package top.doublewin.core.util;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 类描述
 * </p>
 *
 * @author migro
 * @since 2020/1/8 15:42
 */
public class XmlUtil {
    /**
     * JavaBean装换成xml
     * 默认编码UTF-8
     *
     * @param obj
     * @return
     */
    public static String beanToXml(Object obj) {
        return beanToXml(obj, "UTF-8", true);

    }

    /**
     * JavaBean装换成xml
     *
     * @param obj
     * @param encoding
     * @return
     */
    private static String beanToXml(Object obj, String encoding, boolean escape) {
        String result = null;
        try {
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
            //去掉生成xml的默认报文头
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            StringWriter writer = new StringWriter();
            //writer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n");
            marshaller.marshal(obj, writer);
            result = writer.toString();
            if (escape) {
                result = transEscape(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 处理转义字符
     *
     * @param
     * @return
     */
    private static String transEscape(String src) {
        String ret = src;
        if (DataUtil.isNotEmpty(src)) {
            ret = ret.replaceAll("&lt;", "<");
            ret = ret.replaceAll("&gt;", ">");
        }
        return ret;
    }


    /**
     * xml装换成JavaBean
     *
     * @param xml
     * @param c
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T xmlToBean(String xml, Class<T> c) {
        T t = null;
        try {
            JAXBContext context = JAXBContext.newInstance(c);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            t = (T) unmarshaller.unmarshal(new StringReader(xml));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return t;

    }

    /**
     * xml转json
     *
     * @param xml
     */
    public static String xml2json(String xml) {
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(xml);

            JSONObject json = new JSONObject();
            dom4j2Json(doc.getRootElement(), json);
            return json.toJSONString();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * xml转json
     *
     * @param element
     * @param json
     */
    private static void dom4j2Json(Element element, JSONObject json) {
        //如果是属性
        for (Object o : element.attributes()) {
            Attribute attr = (Attribute) o;
            if (DataUtil.isNotEmpty(attr.getValue())) {
                json.put("@" + attr.getName(), attr.getValue());
            }
        }
        List<Element> chdEl = element.elements();
        if (chdEl.isEmpty() && DataUtil.isNotEmpty(element.getText())) {
            //如果没有子元素,只有一个值
            json.put(element.getName(), element.getText());
        }

        for (Element e : chdEl) {
            //有子元素
            if (!e.elements().isEmpty()) {
                //子元素也有子元素
                JSONObject chdjson = new JSONObject();
                dom4j2Json(e, chdjson);
                Object o = json.get(e.getName());
                if (o != null) {
                    JSONArray jsona = null;
                    if (o instanceof JSONObject) {
                        //如果此元素已存在,则转为jsonArray
                        JSONObject jsono = (JSONObject) o;
                        json.remove(e.getName());
                        jsona = new JSONArray();
                        jsona.add(jsono);
                        jsona.add(chdjson);
                    }
                    if (o instanceof JSONArray) {
                        jsona = (JSONArray) o;
                        jsona.add(chdjson);
                    }
                    json.put(e.getName(), jsona);
                } else {
                    if (!chdjson.isEmpty()) {
                        json.put(e.getName(), chdjson);
                    }
                }
            } else {//子元素没有子元素
                for (Object o : element.attributes()) {
                    Attribute attr = (Attribute) o;
                    if (DataUtil.isNotEmpty(attr.getValue())) {
                        json.put("@" + attr.getName(), attr.getValue());
                    }
                }
                if (!e.getText().isEmpty()) {
                    json.put(e.getName(), e.getText());
                }
            }
        }
    }

    /**
     * 单层或多层map转xml字符串
     *
     * @param param
     * @return
     */
    public static String mapToXmlStr(Map<String, Object> param, String rootStr) {
        rootStr = rootStr == null ? "xml" : rootStr;
        Objects.requireNonNull(param);
        StringBuilder sb = new StringBuilder();
        //sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n");
        sb.append("<" + rootStr + ">");
        sb.append(resolveMap(param));
        sb.append("</" + rootStr + ">");
        return sb.toString();
    }

    public static String mapToXmlStr(Map<String, Object> param) {
        Objects.requireNonNull(param);
        StringBuilder sb = new StringBuilder();
        sb.append(resolveMap(param));
        return sb.toString();
    }

    /**
     * 递归多层Map
     *
     * @param param
     * @return
     */
    @SuppressWarnings("unchecked")
    private static String resolveMap(Map<String, Object> param) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            Object v = entry.getValue();
            String k = entry.getKey();
            if (v instanceof Map) {
                sb.append("<").append(k).append(">");
                sb.append(resolveMap((Map<String, Object>) v));
                sb.append("</").append(k).append(">");
            } else {
                sb.append("<").append(k).append(">");
                sb.append(v);
                sb.append("</").append(k).append(">");
            }
        }
        return sb.toString();
    }

    /**
     * 单层或者多层xml字符串转map
     *
     * @param xmlStr
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> xmlSrtToMap(String xmlStr) {
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(xmlStr);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        // 获取根节点
        Element rootElt = Objects.requireNonNull(doc).getRootElement();
        List elements = rootElt.elements();
        return resolveElements(elements);
    }

    /**
     * 递归多层嵌套xml
     *
     * @param elements
     * @return
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> resolveElements(List<Element> elements) {
        Map<String, Object> map = new HashMap<>(elements.size());

        for (Element element : elements) {
            if (element.elements().size() > 0) {
                map.put(element.getName(), resolveElements(element.elements()));
            } else {
                map.put(element.getName(), element.getText());
            }
        }
        return map;
    }

}

