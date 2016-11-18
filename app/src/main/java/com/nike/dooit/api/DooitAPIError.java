package com.nike.dooit.api;

import com.nike.dooit.api.responses.ErrorResponse;

import java.util.ArrayList;
import java.util.List;

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
        return getErrorResponse().hasErrors();
    }

    public List<String> getErrorMessages() {
        if (hasError())
            return getErrorResponse().getErrors();
        return new ArrayList();
    }

    @Override
    public Throwable getCause() {
        return cause;
    }
}
