package top.doublewin.model;

/**
 * <p>
 * 类描述
 * </p>
 *
 * @author migro
 * @since 2019/3/28 10:43
 */
public class LoginUser {
    private String account;
    private String password;
    private boolean rememberMe;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
}
