package com.rr.rgem.gem.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 9/14/2016.
 */
public class Answer {
    String type;
    String name;
    String response;
    List<String> values = new ArrayList<String>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
