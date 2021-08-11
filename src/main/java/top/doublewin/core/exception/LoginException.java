package top.doublewin.core.exception;

import top.doublewin.core.support.http.HttpCode;

@SuppressWarnings("serial")
public class LoginException extends BaseException {
	public LoginException() {
	}

	public LoginException(String message) {
		super(message);
	}

	public LoginException(String message, Exception e) {
		super(message, e);
	}

	@Override
	protected HttpCode getCode() {
		return HttpCode.LOGIN_FAIL;
	}
}
