package top.doublewin.model;

import com.alibaba.fastjson.JSON;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * 对象拷贝时，排除字段集合
 * </p>
 *
 * @author migro
 * @since 2020/7/22 17:08
 */
public class ExcludeSet {

    private Set set = new HashSet();

    public static ExcludeSet getInstance() {
        return new ExcludeSet();
    }

    public ExcludeSet add(String o) {
        set.add(o);
        return this;
    }

    public Set getSet() {
        return set;
    }

    public void setSet(Set set) {
        this.set = set;
    }

    public boolean contains(String key) {
        return set.contains(key);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(set);
    }
}
