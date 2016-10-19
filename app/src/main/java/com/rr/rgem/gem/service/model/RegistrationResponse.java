package com.rr.rgem.gem.service.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Wimpie Victor on 2016/10/19.
 */
public class RegistrationResponse {

    @SerializedName("id")
    int userId;

    public int getUserId() {
        return userId;
    }
}
