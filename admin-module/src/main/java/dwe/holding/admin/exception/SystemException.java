package dwe.holding.admin.exception;

import java.util.Arrays;

/**
 *
 * System exception.
 * System exception is not recoverable by the user.
 * For notification purpose  only.
 *
 */
public class SystemException extends RuntimeException implements ParameterizedException {

    private static final long serialVersionUID = 1L;
    private static final String SYSTEM_SYSTEM_FAILURE = "SYS-00001";
    private final String errorCode;
    private final Object[] params;

    public SystemException() {
        this(SYSTEM_SYSTEM_FAILURE);
    }

    public SystemException(Throwable throwable) {
        this(SYSTEM_SYSTEM_FAILURE, null, throwable);
    }

    public SystemException(String errorCode) {
        this(errorCode, null, null);
    }

    public SystemException(String errorCode, Object[] params) {
        this(errorCode, params, null);
    }

    public SystemException(String errorCode, Throwable throwable) {
        this(errorCode, null, throwable);
    }

    public SystemException(String errorCode, Object[] params, Throwable throwable) {
        super("Code: " + errorCode + (params != null ? ", Parameters: " + Arrays.asList(params) : ""), throwable);

        this.errorCode = errorCode;
        this.params = params;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Object[] getParameters() {
        return params;
    }

    public String getMessage() {
        return "SYSTEM EXCEPTION DEFAULT MESSAGE - see nl.achtiiacht.vmas.exception.SystemException " + getErrorCode() + "|" + getParameters();
        // try {
        // ExceptionResourceBundle bundle = ExceptionResourceBundle.getBundle();
        // return bundle.getString(getErrorCode(), getParameters()) + " (" +
        // getErrorCode() + ")";
        // } catch (MissingResourceException exp) {
        // throw new SystemException("SYS-00004", new Object[]{getErrorCode()},
        // exp);
        // }
    }
}