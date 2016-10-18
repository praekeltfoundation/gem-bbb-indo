package com.rr.rgem.gem.service;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Wimpie Victor on 2016/10/18.
 */
public class ErrorUtil {

    private static final String TAG = "ErrorUtil";

    Retrofit retrofit;

    public ErrorUtil(Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    public WebServiceError parseError(Response<?> response) {
        return parseError(response, WebServiceError.class);
    }

    public <T extends WebServiceError> T parseError(Response<?> response, Class<T> errorClass) {
        Converter<ResponseBody, T> converter =
                retrofit.responseBodyConverter(errorClass, new Annotation[]{});

        T error;

        try {
            error = converter.convert(response.errorBody());
            error.setStatus(response.code());
            Log.d(TAG, String.format("Parsed error %s", error.toString()));
        } catch (IOException e) {
            Log.e(TAG, "IO Exception while parsing error", e);
            error = null;
        }

        return error;
    }

    public static class WebServiceError {

        protected int status;
        protected String detail;
        @SerializedName("non_field_errors")
        protected List<String> nonFieldErrors;

        public int getStatus() {
            return status;
        }

        void setStatus(int status) {
            this.status = status;
        }

        public String getDetail() {
            return detail;
        }

        public List<String> getNonFieldErrors() {
            return nonFieldErrors;
        }

        public String getNonFieldErrorsJoined() {
            if (nonFieldErrors == null) {
                return "";
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < nonFieldErrors.size(); i++) {
                if (i > 0) {
                    sb.append("; ");
                }
                sb.append(nonFieldErrors.get(i));
            }
            return sb.toString();
        }

        @Override
        public String toString() {
            return "WebServiceError{" +
                    "status=" + status +
                    ", detail='" + detail + '\'' +
                    ", nonFieldErrors=" + nonFieldErrors +
                    '}';
        }
    }
}
