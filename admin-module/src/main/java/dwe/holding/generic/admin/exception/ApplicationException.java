package dwe.holding.generic.admin.exception;

import java.util.Arrays;

/**
 *
 * Application exception.
 * Definition for all exceptions which can be valuable for the user.
 * Application exception is only used if the user can act upon.
 * So e.g. duplicate field errors, wrong combinations etc.
 * If the user cannot handle the exception, than a system exception should be thrown instead.
 *
 */
public class ApplicationException extends Exception implements ParameterizedException {

    private static final long serialVersionUID = 1L;

    private final String errorCode;

    private final Object[] params;

    public ApplicationException(String errorCode) {
        this(errorCode, null, null);
    }

    public ApplicationException(String errorCode, Object[] params) {
        this(errorCode, params, null);
    }

    public ApplicationException(String errorCode, Throwable throwable) {
        this(errorCode, null, throwable);
    }

    public ApplicationException(String errorCode, Object[] params, Throwable throwable) {
        super("Code: " + errorCode + (params != null ? ", Parameters: " + Arrays.asList(params) : ""), throwable);

        this.errorCode = errorCode;
        this.params = params;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public Object[] getParameters() {
        return this.params;
    }
}