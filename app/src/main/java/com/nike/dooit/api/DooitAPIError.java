package com.nike.dooit.api;

import com.nike.dooit.api.responses.ErrorResponse;

import retrofit2.adapter.rxjava.HttpException;

/**
 * Created by Bernhard Müller on 2016/05/18.
 */
public class DooitAPIError extends Throwable {
    private Throwable cause;
    private ErrorResponse response;

    public DooitAPIError(Throwable cause) {
        this.cause = cause;
    }

    public DooitAPIError(Throwable cause, ErrorResponse response) {
        this.cause = cause;
        this.response = response;
    }

    public ErrorResponse getErrorResponse() {
        return response;
    }

    private boolean hasError() {
        return getErrorResponse() != null &&
                getErrorResponse().error != null &&
                getErrorResponse().error.message != null;
    }

    public String getErrorMessage() {
        if (hasError())
            return getErrorResponse().error.message;
        return "";
    }

    public String getErrorCode() {
        if (hasError())
            return getErrorResponse().error.code;
        return "";
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    public boolean isResourceNotFound() {
        return "ResourceNotFound".equals(getErrorCode());
    }

    public boolean isServerException() {
        return cause instanceof HttpException && getErrorMessage() != null && !getErrorMessage().isEmpty();
    }

    public boolean isDuplicateIntroduction() {
        return "Duplicate introduction".equals(getErrorMessage());
    }

    public boolean isDuplicatePhoneNumber() {
        return "duplicate phone_number".equals(getErrorMessage());
    }

    public boolean isDuplicateEmail() {
        return "duplicate email".equals(getErrorMessage());
    }

    public boolean isUserDeleted() {
        return getCause() != null && getCause() instanceof HttpException && (((HttpException) getCause()).code() == 410);
    }

    public boolean isTokenExpired() {
        return "InvalidCredentials".equals(getErrorCode());
    }
}
