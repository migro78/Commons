package top.doublewin.core.base;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 公共基础Mapper类
 * </p>
 *
 * @author migro
 * @since 2019/3/11 10:17
 */
public interface BaseMapper<T extends BaseModel> extends com.baomidou.mybatisplus.core.mapper.BaseMapper<T> {

    /**
     * 分页查询，接受对象参数
     *
     * @param
     * @return
     */
    public <T> IPage<T> pagingQuery(Page page, @Param("cm") T params);

    /**
     * 分页查询，接受Map参数
     *
     * @param
     * @return
     */
    public <T> IPage<T> pagingQuery(Page page, @Param("cm") Map<String, Object> params);

    /**
     * 普通查询，接收对象参数
     *
     * @param
     * @return
     */
    public <T> List<T> pagingQuery(@Param("cm") T params);

    /**
     * 普通查询，接受Map参数
     *
     * @param
     * @return
     */
    public <T> List<T> pagingQuery(@Param("cm") Map<String, Object> params);

}
