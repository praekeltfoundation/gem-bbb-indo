package com.rr.rgem.gem.models;

import java.util.Map;

/**
 * Created by Rudolph Jacobs on 2016-09-26.
 */
public interface ConvoCallback {
    public String callback(Map<String, String> vars, Map<String, String> responses);
}
