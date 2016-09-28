package com.rr.rgem.gem.views;

import java.util.ArrayList;

/**
 * Created by jacob on 2016/09/14.
 */
public class MessageCarousel extends Message
{
    private ArrayList<MultipleChoice> carouselMessages;

    public MessageCarousel (long id, String dateAndTime, boolean botMessage, ResponseType responseType)
    {
        super(id, dateAndTime, botMessage, responseType, null);
        this.carouselMessages = null;
    }

    public ArrayList<MultipleChoice> getMessages() { return carouselMessages; }
    public void setMessages(ArrayList<MultipleChoice> carouselMessages)
    {
        this.carouselMessages = carouselMessages;
        carouselMessages.trimToSize();
    }
}