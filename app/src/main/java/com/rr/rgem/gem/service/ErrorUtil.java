package com.rr.rgem.gem.service;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
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
        Converter<ResponseBody, WebServiceError> converter =
                retrofit.responseBodyConverter(WebServiceError.class, new Annotation[]{});

        WebServiceError error;

        try {
            error = converter.convert(response.errorBody());
            Log.d(TAG, String.format("Parsed error %s", error.toString()));
        } catch (IOException e) {
            Log.e(TAG, "IO Exception while parsing error", e);
            error =  new WebServiceError();
        }

        return error;
    }


    public static class WebServiceError {

        int status;
        String detail;
        @SerializedName("non_field_errors")
        List<String> nonFieldErrors;

        public int getStatus() {
            return status;
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
    }
}
