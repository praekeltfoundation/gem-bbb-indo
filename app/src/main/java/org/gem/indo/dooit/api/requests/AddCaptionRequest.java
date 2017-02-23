package org.gem.indo.dooit.api.requests;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Reinhardt on 2017/02/23.
 */

public class AddCaptionRequest {
    @SerializedName("caption")
    String caption;

    public AddCaptionRequest(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
