package com.rr.rgem.gem.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sjj98 on 9/23/2016.
 */
public class Tips {

    List<Tip> tips = new ArrayList<Tip>();

    public List<Tip> getTips() {
        return tips;
    }

    public void setTips(List<Tip> tipps) {
        this.tips = tipps;
    }
}
