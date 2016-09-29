package com.rr.rgem.gem.views;

import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jacob on 2016/09/14.
 */
public class MultipleChoice extends Message
{
    private Map<String, View.OnClickListener> clicks;

    public MultipleChoice(long id, String dateAndTime, boolean botMessage, ResponseType responseType, Map<String, View.OnClickListener> clicks)
    {
        super(id, dateAndTime, botMessage, responseType, null);
        this.clicks = clicks;
    }

    public Map<String, View.OnClickListener> getChoices() { return clicks; }
    public void setChoices(Map<String, View.OnClickListener> clicks)
    {
        this.clicks = clicks;
    }
}