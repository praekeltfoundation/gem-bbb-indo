package com.nike.dooit.api.responses;

/**
 * Created by herman on 2016/11/05.
 */

public class ErrorResponse {

    public String status;
    public ErrorBody error;

    public static class ErrorBody {
        public String code;
        public String message;
    }
}