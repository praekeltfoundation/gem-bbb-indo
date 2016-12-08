package org.gem.indo.dooit.controllers;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wimpie Victor on 2016/12/08.
 */

public enum BotCallType {

    @SerializedName("#do_create")
    DO_CREATE,

    @SerializedName("#do_update")
    DO_UPDATE,

    @SerializedName("#do_delete")
    DO_DELETE,

    @SerializedName("#do_deposit")
    DO_DEPOSIT,

    @SerializedName("#do_withdraw")
    DO_WITHDRAW,

    @SerializedName("#add_badge")
    ADD_BADGE
}
