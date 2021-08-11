package top.doublewin.core.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import top.doublewin.core.exception.BusinessException;
import top.doublewin.core.support.cache.CacheKey;
import top.doublewin.core.support.cache.RedisUtil;
import top.doublewin.core.support.context.Constants;
import top.doublewin.core.util.DataUtil;
import top.doublewin.core.util.DateUtil;
import top.doublewin.core.util.ExceptionUtil;
import top.doublewin.core.util.InstanceUtil;
import top.doublewin.model.ExcludeSet;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * <p>
 * 公共基础服务类
 * </p>
 *
 * @author migro
 * @since 2019/3/6 18:22
 */
public abstract class BaseService<T extends BaseModel, M extends BaseMapper<T>> implements IBaseService<T> {
    protected Logger logger = LogManager.getLogger();

    @Autowired
    protected M mapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public T update(T param) {
        updateOnly(param);
        return mapper.selectById(param.getId());
    }

    @Override
    public void updateOnly(T param){
        if (DataUtil.isEmpty(param.getId())) {
            // 新增操作
            param.setCreateTime(new Date());
            mapper.insert(param);
        } else {
            // 修改操作
            param.setUpdateTime(new Date());
            mapper.updateById(param);
        }
    }

    @Override
    public void delete(T param) {
        UpdateWrapper<T> wrapper = new UpdateWrapper<>(param);
        mapper.delete(wrapper);
    }

    @Override
    public void deleteById(Serializable id) {
        mapper.deleteById(id);
    }

    @Override
    public void deleteByIds(Serializable[] ids) {
        if (DataUtil.isNotEmpty(ids)) {
            List<Serializable> list = Stream.of(ids).collect(Collectors.toList());
            mapper.deleteBatchIds(list);
        }
    }

    @Override
    public void deleteByIds(List<Serializable> ids) {
        if (DataUtil.isNotEmpty(ids)) {
            mapper.deleteBatchIds(ids);
        }
    }


    private Page getPage(Map<String, Object> param) {
        int current = 1;
        int size = 10;
        String orderBy;
        if (DataUtil.isNotEmpty(param.get("current"))) {
            current = Integer.valueOf(param.get("current").toString());
        }
        if (DataUtil.isNotEmpty(param.get("pageNumber"))) {
            current = Integer.valueOf(param.get("pageNumber").toString());
        }
        if (DataUtil.isNotEmpty(param.get("size"))) {
            size = Integer.valueOf(param.get("size").toString());
        }
        if (DataUtil.isNotEmpty(param.get("pageSize"))) {
            size = Integer.valueOf(param.get("pageSize").toString());
        }
        if (DataUtil.isNotEmpty(param.get("offset"))) {
            int offset = Integer.valueOf(param.get("offset").toString());
            current = offset / size + 1;
        }
        Page<T> page = new Page<T>(current, size);

        if (DataUtil.isNotEmpty(param.get("orderBy"))) {
            orderBy = (String) param.get("orderBy");
            orderBy = DataUtil.camel2Underline(orderBy);
            if (DataUtil.isNotEmpty(param.get("sortAsc"))) {
                String sortStr = (String) param.get("sortAsc");
                if ("Y".equals(sortStr)) {
                    page.setAsc(orderBy);
                } else {
                    page.setDesc(orderBy);
                }
                // 分页查询语句中去掉排序条件
                param.remove("sortAsc");
            } else {
                page.setDesc(orderBy);
            }
            // 分页查询语句中去掉排序条件
            param.remove("orderBy");
        }
        return page;
    }


    /**
     * 分页查询
     *
     * @param
     * @return
     */
    @Override
    public <T> IPage<T> pagingQuery(Map<String, Object> param) {
        Page page = getPage(param);
        return mapper.pagingQuery(page, param);
    }

    /**
     * 分页查询
     *
     * @param
     * @return
     */
    @Override
    public <T> IPage<T> pagingQuery(T param) {
        Map map = InstanceUtil.transBean2Map(param);
        return pagingQuery(map);
    }

    /**
     * 通过默认查询语句进行非分页查询
     *
     * @param
     * @return
     */
    @Override
    public <T> List<T> unpagingQuery(Map<String, Object> param) {
        return mapper.pagingQuery(param);
    }

    /**
     * 通过默认查询语句进行非分页查询
     *
     * @param
     * @return
     */
    @Override
    public <T> List<T> unpagingQuery(T param) {
        return mapper.pagingQuery(param);
    }

    /**
     * 列表查询
     *
     * @param
     * @return
     */
    @Override
    public List<T> queryList(T param) {
        return mapper.selectList(new QueryWrapper<T>(param));
    }

    /**
     * 列表查询
     *
     * @param
     * @return
     */
    @Override
    public List<T> queryList(Map<String, Object> param) {
        Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        T t = InstanceUtil.transMap2Bean(param, entityClass);
        QueryWrapper<T> wrapper = new QueryWrapper<T>(t);
        return mapper.selectList(wrapper);
    }

    /**
     * 根据Id查询(默认类型T)
     */
    @Override
    public List<T> queryList(final List<Long> ids) {
        final List<T> list = InstanceUtil.newArrayList();
        if (ids != null) {
            IntStream.range(0, ids.size()).forEach(i -> list.add(null));
            IntStream.range(0, ids.size()).parallel().forEach(i -> {
                list.set(i, queryById(ids.get(i)));
            });
        }
        return list;
    }


    /**
     * 自定义分页查询
     *
     * @param
     * @return
     */
    @Override
    public Object pagingQuery(String sqlId, Map<String, Object> param, List<String> splitDates) {
        if (DataUtil.isNotEmpty(splitDates)) {
            splitDates.forEach(t -> DateUtil.splitDate(param, t));
        }
        Page page = getPage(param);
        Object[] op = {page, param};
        return InstanceUtil.invokeMethod(mapper, sqlId, op);
    }

    @Override
    /** 根据id查询实体 */
    public T queryById(Serializable id) {
        return mapper.selectById(id);
    }

    @Override
    public T queryVoById(Serializable id) {
        Map param = InstanceUtil.newHashMap("id",id);
        List<T> list = unpagingQuery(param);
        if(DataUtil.isNotEmpty(list) && list.size()>0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public T selectOne(T entity) {
        QueryWrapper<T> wrapper = new QueryWrapper(entity);
        return mapper.selectOne(wrapper);
    }


    /**
     * 获取缓存键值
     *
     * @param id
     * @return
     */
    protected String getLockKey(Object id) {
        CacheKey cacheKey = CacheKey.getInstance(getClass());
        StringBuilder sb = new StringBuilder();
        if (cacheKey == null) {
            sb.append(getClass().getName());
        } else {
            sb.append(cacheKey.getValue());
        }
        return sb.append(":LOCK:").append(id).toString();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatch(List<T> entityList) {
        return updateBatch(entityList, 30);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateBatch(List<T> entityList, int batchSize) {
        return updateBatch(entityList, batchSize, true);
    }


    private boolean updateBatch(List<T> entityList, int batchSize, boolean selective) {
        if (CollectionUtils.isEmpty(entityList)) {
            throw new IllegalArgumentException("Error: entityList must not be empty");
        }
        try (SqlSession batchSqlSession = sqlSessionBatch()) {
            IntStream.range(0, entityList.size()).forEach(i -> {
                if (selective) {
                    update(entityList.get(i));
                } else {
                    updateAllColumn(entityList.get(i));
                }
                if (i >= 1 && i % batchSize == 0) {
                    batchSqlSession.flushStatements();
                }
            });
            batchSqlSession.flushStatements();
        } catch (Throwable e) {
            throw new MybatisPlusException("Error: Cannot execute insertOrUpdateBatch Method. Cause", e);
        }
        return true;
    }

    /**
     * <p>
     * 批量操作 SqlSession
     * </p>
     */
    protected SqlSession sqlSessionBatch() {
        return SqlHelper.sqlSessionBatch(currentModelClass());
    }


    @SuppressWarnings("unchecked")
    protected Class<T> currentModelClass() {
        return ReflectionKit.getSuperClassGenericType(getClass(), 0);
    }


    @Transactional(rollbackFor = Exception.class)
    public T updateAllColumn(T record) {
        try {
            // 不同基础模型类，使用不同的处理方式  modify by migro
            Serializable oid = record.getId();
            record.setUpdateTime(new Date());
            if (record.getId() == null) {
                record.setCreateTime(new Date());
                mapper.insert(record);
                oid = record.getId();
            } else {
                String lockKey = getLockKey("U" + record.getId());
                if (redisUtil.lock(lockKey, String.valueOf(record.getId()))) {
                    try {
                        mapper.updateById(record);
                    } finally {
                        redisUtil.unlock(lockKey, "");
                    }
                } else {
                    throw new RuntimeException("数据不一致!请刷新页面重新编辑!");
                }
            }

            record = mapper.selectById(oid);

        } catch (DuplicateKeyException e) {
            logger.error(Constants.Exception_Head, e);
            throw new BusinessException("已经存在相同的记录.");
        } catch (Exception e) {
            logger.error(Constants.Exception_Head, e);
            throw new RuntimeException(ExceptionUtil.getStackTraceAsString(e));
        }
        return record;
    }

    @Override
    public T copyAndUpdate(T src, T queryParam, ExcludeSet ext) {
        T old = selectOne(queryParam);
        if (DataUtil.isNotEmpty(old)) {
            // 数据拷贝
            InstanceUtil.to(src, old, ext);
            return update(old);
        } else {
            return update(src);
        }
    }

    @Override
    public T copyAndUpdate(T src, T queryParam) {
        ExcludeSet ext = ExcludeSet.getInstance();
        ext.add("id");
        return copyAndUpdate(src, queryParam, ext);
    }

}
