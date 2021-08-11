package top.doublewin.core.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import top.doublewin.core.util.DataUtil;
import top.doublewin.core.util.InstanceUtil;
import top.doublewin.core.util.WebUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 类描述
 * </p>
 *
 * @author migro
 * @since 2019/3/11 11:12
 */
public abstract class BaseController<T extends BaseModel, S extends IBaseService<T>> extends AbstractController {

    @Autowired(required = false)
    protected S service;

    /**
     * 分页查询
     */
    public Object pagingQuery(Map<String, Object> param) {
        return pagingQuery(new ModelMap(), param);
    }


    /**
     * 分页查询
     *
     */
    public Object pagingQuery(ModelMap modelMap, Map<String, Object> param) {
        if (param.get("keyword") == null && param.get("search") != null) {
            param.put("keyword", param.get("search"));
            param.remove("search");
        }
        Object page = service.pagingQuery(param);
        return setSuccessModelMap(modelMap, page);
    }

    /**
     * 通过默认sql语句查询列表
     *
     * @param
     * @return
     */
    public Object unpagingQuery(ModelMap modelMap, Map<String, Object> param) {
        Object list = service.unpagingQuery(param);
        return setSuccessModelMap(modelMap,list);
    }

    /**
     * 通过默认sql语句查询单个实体对象
     *
     * @param
     * @return
     */
    public Object getByPagingQuery(ModelMap modelMap, BaseModel param) {
        return getByPagingQuery(modelMap,param.getId());
    }

    /**
     * 通过默认sql语句查询单个实体对象
     *
     * @param
     * @return
     */
    public Object getByPagingQuery(ModelMap modelMap, Long id) {
        Object ret = null;
        Map map = InstanceUtil.newHashMap();
        map.put("id",id);
        List<BaseModel> list = service.unpagingQuery(map);
        if (DataUtil.isNotEmpty(list)) {
            ret = list.get(0);
        }
        return setSuccessModelMap(modelMap,ret);
    }

    public Object queryList(ModelMap modelMap, Map<String, Object> param) {
        return setSuccessModelMap(modelMap, service.queryList(param));
    }

    public Object get(Long id) {
        return get(new ModelMap(), id);
    }

    public Object get(ModelMap modelMap, Long id) {
        Object result = service.queryById(id);
        return setSuccessModelMap(modelMap, result);
    }

    public Object get(BaseModel param) {
        return get(new ModelMap(), param);
    }

    public Object get(ModelMap modelMap, BaseModel param) {
        Serializable oid = param.getId();
        Object result = service.queryById(oid);
        return setSuccessModelMap(modelMap, result);
    }

    public Object update(HttpServletRequest request, T param) {
        return update(request, new ModelMap(), param);
    }

    public Object update(HttpServletRequest request, ModelMap modelMap, T param) {

        Long userId = WebUtil.getSessionUserId(request);

        if (DataUtil.isEmpty(param.getId())) {
            // 新增操作
            param.setCreateBy(userId);
        } else {
            // 修改操作
            param.setUpdateBy(userId);
        }
        service.update(param);
        return setSuccessModelMap(modelMap);
    }

    /**
     * 物理删除
     */
    public Object delete(T param) {
        return delete(new ModelMap(), param);
    }

    /**
     * 物理删除
     */
    public Object delete(ModelMap modelMap, T param) {
        service.deleteById(param.getId());
        return setSuccessModelMap(modelMap);
    }

    /**
     * 逻辑删除
     */
    public Object del(HttpServletRequest request, T param) {
        return del(new ModelMap(), request, param);
    }

    /**
     * 逻辑删除
     */
    public Object del(ModelMap modelMap, HttpServletRequest request, T param) {
        Long userId = WebUtil.getSessionUserId(request);
        // 暂时未处理

        return setSuccessModelMap(modelMap);
    }

}
