package top.doublewin.core.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 类描述
 * </p>
 *
 * @author migro
 * @since 2018/12/28 13:53
 */
public class BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "全文搜索关键字")
    @TableField(exist = false)
    private String keyword;

    @ApiModelProperty(value = "排序字段")
    @TableField(exist = false)
    private String orderBy;

    @ApiModelProperty(value = "根据id列表批量处理")
    @TableField(exist = false)
    private List<Long> ids;

    @ApiModelProperty(value = "分页查询第几页参数")
    @TableField(exist = false)
    private String current;

    @ApiModelProperty(value = "分页查询每页记录数参数")
    @TableField(exist = false)
    private String size;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @ApiModelProperty(value = "创建人id")
    public void setCreateBy(Long createBy){}
    @ApiModelProperty(value = "修改人id")
    public void setUpdateBy(Long updateBy){}
    @ApiModelProperty(value = "创建时间")
    public void setCreateTime(Date createTime){}
    @ApiModelProperty(value = "修改时间")
    public void setUpdateTime(Date updateTime){}

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
