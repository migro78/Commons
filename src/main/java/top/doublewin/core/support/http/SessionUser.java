package top.doublewin.core.support.http;

import top.doublewin.core.base.BaseModel;

import java.util.Date;

/**
 * 用户会话信息
 * @author ShenHuaJie
 * @since 2018年7月22日 上午9:34:50
 */
@SuppressWarnings("serial")
public class SessionUser extends BaseModel {

    private Long id;
    //用户名
    private String userName;
    //组织机构ID
    private Long orgId;
    //用户电话
    private String userPhone;
    //用户类型 1--内部公司用户,2--加盟商用户,3--药房用户，4--供应商用户 -1 APP会员
    private Integer userType;
    //当前公司Id
    private Long cmpId;

    public SessionUser() {}

    public SessionUser(Long id, String userName, String userPhone, Long orgId, Long cmpId, Integer userType) {
        this.id = id;
        this.userName = userName;
        this.userPhone = userPhone;
        this.orgId  = orgId;
        this.cmpId = cmpId;
        this.userType = userType;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public Long getCmpId() {
        return cmpId;
    }

    public void setCmpId(Long cmpId) {
        this.cmpId = cmpId;
    }

    @Override
    public void setCreateBy(Long createBy) {

    }

    @Override
    public void setUpdateBy(Long updateBy) {

    }

    @Override
    public void setCreateTime(Date createTime) {

    }

    @Override
    public void setUpdateTime(Date updateTime) {

    }
}
