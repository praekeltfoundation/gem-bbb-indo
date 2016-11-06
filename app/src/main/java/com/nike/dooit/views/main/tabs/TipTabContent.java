package com.nike.dooit.views.main.tabs;

import android.content.Context;
import android.view.View;
import android.widget.TabHost;

/**
 * Created by Wimpie Victor on 2016/11/06.
 */

public class TipTabContent implements TabHost.TabContentFactory {

    private Context context;

    public TipTabContent(Context context) {
        this.context = context;
    }

    @Override
    public View createTabContent(String tag) {
        View view = new View(context);
        return view;
    }
}
