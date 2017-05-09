package org.gem.indo.dooit.api.responses;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by herman on 2016/11/05.
 */

public class ErrorResponse {

    @SerializedName("status_code")
    private int status;

    private List<String> errors;

    @SerializedName("rel_errors")
    private Map<String, List<String>> relErrors;

    @SerializedName("field_errors")
    private Map<String, List<String>> fieldErrors;

    public int getStatus() {
        return status;
    }

    public List<String> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    public Map<String, List<String>> getRelErrors() {
        return relErrors;
    }

    public Map<String, List<String>> getFieldErrors() {
        if (fieldErrors == null)
            return new HashMap<>();
        return fieldErrors;
    }
}