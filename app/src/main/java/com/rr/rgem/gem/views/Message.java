package com.rr.rgem.gem.views;

import android.view.View;
import android.widget.TextView;

/**
 * Created by jacob on 2016/09/14.
 */
public class Message
{
    public enum ResponseType
    {
        User, FreeForm, ImageUpload, QuickReply, QuickButton, Carousel
    }

    private long id;
    private String title = "";
    private String subtitle = "";
    private String text = "";
    private String dateAndTime;
    private boolean botMessage;
    private String image = "";
    private ResponseType responseType;
    private TextView.OnEditorActionListener textListener;

    public Message(long id, String dateAndTime, boolean botMessage, ResponseType responseType, TextView.OnEditorActionListener textListener)
    {
        this.id = id;
        this.dateAndTime = dateAndTime;
        this.botMessage = botMessage;
        this.responseType = responseType;
        this.textListener = textListener;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getTime() { return dateAndTime; }
    public void setTime(String dateAndTime) { this.dateAndTime = dateAndTime; }

    public boolean getBotMessage() { return botMessage; }
    public void seBotMessage(boolean botMessage) { this.botMessage = botMessage; }

    public String getImagePath() { return image; }
    public void setImagePath(String image) { this.image = image; }

    public ResponseType getResponseType() { return responseType; }
    public void setResponseType(ResponseType responseType) { this.responseType = responseType; }

    public TextView.OnEditorActionListener getEditorActionListener() { return textListener; }
    public void setEditorActionListener(TextView.OnEditorActionListener textListener) { this.textListener = textListener; }
}