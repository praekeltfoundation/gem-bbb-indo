package com.nike.dooit.views.main.fragments.tip.adapters;

import com.nike.dooit.models.Tip;

import java.util.List;

/**
 * Created by Wimpie Victor on 2016/11/13.
 */

public interface TipsAdapter {
    public void updateAllTips(List<Tip> tips);
    public List<Tip> getAllTips();
}
