package com.rr.rgem.gem.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.InputType;
import android.text.Layout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.rr.rgem.gem.MainActivity;
import com.rr.rgem.gem.R;

import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by chris on 9/13/2016.
 */
public class LeftRightConversation extends ConversationalBase
{
    private HashMap<Long, View> views;

    public LeftRightConversation(RelativeLayout contentLayout)
    {
        super(contentLayout);
        views = new HashMap<>();
    }

    public LeftRightConversation(RelativeLayout contentLayout, HashMap<Long, View> views)
    {
        super(contentLayout);
        this.views = views;

        for (Long key: views.keySet())
        {
            View message = views.get(key);
            if (message.getTag() == Message.ResponseType.FreeForm || message.getTag() == Message.ResponseType.QuickReply || message.getTag() == Message.ResponseType.QuickButton)
            {
                addLeftView(message, "name");
            }
            else
            {
                addRightView(message, "name");
            }
        }
    }

    public HashMap<Long, View> getViews()
    {
        return views;
    }

    public void addFreeFormQuestion(Message message, CharSequence placeholder)
    {
        View question = formatMessage(message);
        this.addLeftView(question, "name");

        final EditText textField = new EditText(contentLayout.getContext());
        textField.setHint(placeholder);
        textField.setImeOptions(EditorInfo.IME_ACTION_DONE);
        textField.setOnEditorActionListener(message.getEditorActionListener());
        textField.setSingleLine(true);
        addRightView(textField, "Textfield");
        question.setTag(Message.ResponseType.FreeForm);
        views.put(message.getId(), question);
    }

    public void addFreeFormPlain(Message message)
    {
        View question = formatMessage(message);
        this.addLeftView(question, "name");

        //final EditText textField = new EditText(contentLayout.getContext());
        //textField.setHint(placeholder);
        //textField.setImeOptions(EditorInfo.IME_ACTION_DONE);
        //textField.setOnEditorActionListener(message.getEditorActionListener());
        //addRightView(textField, "Textfield");
        question.setTag(Message.ResponseType.FreeForm);
        views.put(message.getId(), question);
    }

    public void addQuickReplyQuestion(MultipleChoice message)
    {
        View question = formatMessage(message);
        this.addLeftView(question, "name");
        ConversationalBase.OptionButtonSentence sentence = this.createOptionButtonSentence();
        for(String key: message.getChoices().keySet()){
            final CharSequence name = key;
            sentence.addOptionButton(name, message.getChoices().get(name));
        }
        question.setTag(Message.ResponseType.QuickReply);
        views.put(message.getId(), question);
    }

    public void addQuickButtonQuestion(MultipleChoice message)
    {
        View question = formatMessage(message);
        LinearLayout contentHolder = (LinearLayout) question.findViewById(R.id.contentHolder);
        this.addLeftView(question, "name");

        for (String key: message.getChoices().keySet())
        {
            final CharSequence name = key;
            Button button = new Button(contentLayout.getContext());
            button.setText(key);
            button.setOnClickListener(message.getChoices().get(name));
            button.setBackgroundColor(Color.WHITE);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 10, 10, 10);
            button.setLayoutParams(params);

            params.topMargin = 2;
            contentHolder.addView(button);
        }

        question.setTag(Message.ResponseType.QuickButton);
        views.put(message.getId(), question);
    }

    public void addUserAnswer(Message message)
    {
        View answer = formatMessage(message);
        LinearLayout contentHolder = (LinearLayout) answer.findViewById(R.id.contentHolder);
        this.addRightView(answer, "name");
        answer.setTag(Message.ResponseType.User);
        views.put(message.getId(), answer);
    }

    public void addMessageCarousel(MessageCarousel messageCarousel)
    {
        View carouselHolder = LayoutInflater.from(contentLayout.getContext()).inflate(R.layout.message_carousel, null);
        this.addLeftView(carouselHolder, "name");
        LinearLayout carousel = (LinearLayout) carouselHolder.findViewById(R.id.carousel);
        View preview = null;

        for (int i = 0; i < messageCarousel.getMessages().size(); ++i)
        {
            MultipleChoice message = messageCarousel.getMessages().get(i);
            View question = formatMessage(message);
            ImageView coachIcon = (ImageView) question.findViewById(R.id.coachImageButton);
            coachIcon.setVisibility(View.GONE);

            LinearLayout contentHolder = (LinearLayout) question.findViewById(R.id.contentHolder);

            carousel.addView(question);
            ;
            for (String key: message.getChoices().keySet())
            {
                final CharSequence name = key;
                Button button = new Button(contentLayout.getContext());
                button.setText(key);
                button.setOnClickListener(message.getChoices().get(name));
                button.setBackgroundColor(Color.WHITE);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(10, 10, 10, 10);
                button.setLayoutParams(params);

                params.topMargin = 2;
                contentHolder.addView(button);
            }

            question.setTag(Message.ResponseType.QuickButton);
        }

        views.put(messageCarousel.getId(), carouselHolder);
        carouselHolder.setTag(Message.ResponseType.Carousel);
    }

    public ImageView addImageUploadQuestion(Message message, View.OnClickListener listener)
    {
        View question = formatMessage(message);
        this.addLeftView(question, "name");

        RelativeLayout container = new RelativeLayout(contentLayout.getContext());
        this.addRightView(container, "image");
        final float scale = contentLayout.getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (200 * scale + 0.5f);
        /*
        TextView imageUpload = new TextView(contentLayout.getContext());
        container.addView(imageUpload);
        imageUpload.setText("Upload Image");
        imageUpload.setGravity(Gravity.CENTER);
        imageUpload.setBackgroundColor(Color.GRAY);
        imageUpload.getLayoutParams().width = pixels;
        imageUpload.getLayoutParams().height = pixels;
        */
        ImageView image = new ImageView(contentLayout.getContext());
        container.addView(image);
        image.getLayoutParams().width = pixels;
        image.getLayoutParams().height = pixels;
        image.setBackgroundColor(Color.BLUE);

        question.setTag(Message.ResponseType.ImageUpload);
        views.put(message.getId(), question);
        //imageUpload.setOnClickListener(listener);
        image.setOnClickListener(listener);

        return image;
    }


    public View formatMessage(Message message)
    {
        View question = LayoutInflater.from(contentLayout.getContext()).inflate(R.layout.coach_question, null);
        LinearLayout contentHolder = (LinearLayout) question.findViewById(R.id.contentHolder);
        ImageView image = (ImageView) contentHolder.findViewById(R.id.image);
        TextView title = (TextView) contentHolder.findViewById(R.id.title);
        TextView subtitle = (TextView) contentHolder.findViewById(R.id.subtitle);
        TextView text = (TextView) contentHolder.findViewById(R.id.text);

        title.setText(message.getTitle());
        subtitle.setText(message.getSubtitle());
        text.setText(message.getText());

        if(message.getImagePath() == "")
            image.setVisibility(View.GONE);
        else {
            new ImageDownloadTask(image).execute(message.getImagePath().toString());
            image.setVisibility(View.VISIBLE);
        }

        if (message.getTitle() == "")
            title.setVisibility(View.GONE);
        else
            title.setVisibility(View.VISIBLE);

        if(message.getSubtitle() == "")
            subtitle.setVisibility(View.GONE);
        else
            subtitle.setVisibility(View.VISIBLE);

        if(message.getText() == "")
            text.setVisibility(View.GONE);
        else
            text.setVisibility(View.VISIBLE);

        return question;
    }

    public void scroll(long viewId)
    {
        View desiredView = views.get(viewId);

        desiredView.getParent().requestChildFocus(desiredView ,desiredView);
    }

    // Not working properly yet...
    public void scroll()
    {
        ((ScrollView) contentLayout.getParent()).fullScroll(View.FOCUS_DOWN);
    }

    private class ImageDownloadTask extends AsyncTask<String, Void, Bitmap>
    {
        ImageView imageView;

        public ImageDownloadTask (ImageView imageView) { this.imageView = imageView; }

        protected Bitmap doInBackground(String...urls)
        {
            String url = urls[0];
            Bitmap image = null;

            try{
                InputStream stream = new URL(url).openStream();

                image = BitmapFactory.decodeStream(stream);
            }catch(Exception e){
                e.printStackTrace();
            }
            return image;
        }

        protected void onPostExecute(Bitmap result) { imageView.setImageBitmap(result); }
    }

    public void addTextInputQuestion(long id,String title, TextView.OnEditorActionListener listener){
        Message msg = new Message(newId(),new Date().toString(),true, Message.ResponseType.FreeForm,listener);
        msg.setTitle(title);
        addFreeFormQuestion(msg, "Type answer here...");
    }
    public void addMultipleChoiceQuestion(long id,String title,Map<String, View.OnClickListener> listeners){
        MultipleChoice msg = new MultipleChoice(newId(),new Date().toString(),true, Message.ResponseType.QuickReply,listeners);
        msg.setTitle(title);
        addQuickButtonQuestion(msg);
    }


    public void addPasswordQuestion(Message message, CharSequence placeholder)
    {
        View question = formatMessage(message);
        this.addLeftView(question, "name");

        EditText passwordField = new EditText(contentLayout.getContext());
        passwordField.setHint(placeholder);
        passwordField.setImeOptions(EditorInfo.IME_ACTION_DONE);
        passwordField.setSingleLine(true);
        passwordField.setOnEditorActionListener(message.getEditorActionListener());
        passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        this.addRightView(passwordField, "Passwordfield");

        question.setTag(Message.ResponseType.FreeForm);
        views.put(message.getId(), question);
    }
    public void addPasswordMessage(long id,String title, TextView.OnEditorActionListener listener, CharSequence placeholder)
    {
        Message msg = new Message(newId(),new Date().toString(),true, Message.ResponseType.FreeForm,listener);
        msg.setTitle(title);
        addPasswordQuestion(msg, placeholder);
    }

}
