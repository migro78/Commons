package top.doublewin.core.base;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.transaction.annotation.Transactional;
import top.doublewin.model.ExcludeSet;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 公共基础服务接口
 * </p>
 *
 * @author migro
 * @since 2019/3/11 11:15
 */
public interface IBaseService<T extends BaseModel> {

    @Transactional(rollbackFor = Exception.class)
    void delete(T param);

    void deleteById(Serializable id);

    @Transactional(rollbackFor = Exception.class)
    void deleteByIds(Serializable[] ids);

    @Transactional(rollbackFor = Exception.class)
    void deleteByIds(List<Serializable> ids);

    <T> IPage<T> pagingQuery(Map<String, Object> param);

    <T> IPage<T> pagingQuery(T param);

    Object pagingQuery(String sqlId, Map<String, Object> param, List<String> splitDates);

    <T> List<T> unpagingQuery(Map<String, Object> param);

    <T> List<T> unpagingQuery(T param);

    List<T> queryList(Map<String, Object> param);

    List<T> queryList(T param);

    T queryById(Serializable id);

    T queryVoById(Serializable id);

    T selectOne(T entity);

    T update(T entity);

    void updateOnly(T entity);

    boolean updateBatch(List<T> entityList);

    List<T> queryList(final List<Long> ids);

    T copyAndUpdate(T src, T queryParam, ExcludeSet ext);

    T copyAndUpdate(T src, T queryParam);
}
