package com.rr.rgem.gem.views;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by chris on 9/13/2016.
 */
public class ConversationalBase {
    final RelativeLayout contentLayout;

    private View preview;
    private int idGen  =1;
    protected int newId(){
        return ++idGen;
    }
    private <T extends View> T assignId(T t){
        t.setId(idGen++);
        return t;
    }
    public static class OptionButtonSentence {
        final ConversationalBase conversation;
        final Context context;
        private View top;
        private int added = 0;
        private int total = 0;
        protected OptionButtonSentence(ConversationalBase conversation){
            this.conversation = conversation;
            this.context = conversation.contentLayout.getContext();
            this.top = conversation.preview;
        }
        public void addOptionButton(CharSequence text, View.OnClickListener clickListener){

            Button button = new Button(this.context);
            button.setText(text);
            button.setOnClickListener(clickListener);
            ++added;
            ++total;
            if(added == 1){
                conversation.addRightView(button,text);
            }else if(added > 2){
                conversation.addToLastLeftBelow(button,top,text);
                top = conversation.preview;
                added = 0;
            }else{
                conversation.addToLastLeftBelow(button,top,text);
            }
        }
    }
    public OptionButtonSentence createOptionButtonSentence(){
        return new OptionButtonSentence(this);
    }
    public ConversationalBase(RelativeLayout contentLayout){
        this.contentLayout = contentLayout;
    }

    /// for rey
    /*
    public void addTextAnswer(CharSequence placeholder){
        /// i.e. this.addRightView(...);
        final EditText textField = new EditText(contentLayout.getContext());
        textField.setHint(placeholder);
        textField.setImeOptions(EditorInfo.IME_ACTION_DONE);
        textField.setSingleLine(true);
        this.addBottomView(textField, placeholder);
        textField.requestFocus();

        textField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    TextView answer = new TextView(contentLayout.getContext());
                    answer.setText(v.getText());
                    ConversationalBase.this.addRightView(answer, answer.getText());
                    v.setText("");
                    return true;
                }
                return false;
            }
        });
    }
    */

    public void addLeftView(View l,CharSequence name){
        assignId(l);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        if(preview!=null)
            params.addRule(RelativeLayout.BELOW, preview.getId());

        params.leftMargin = 10;
        params.topMargin = 50;
        params.bottomMargin = 50;
        contentLayout.addView(l, params);
        preview = l;

    }
    public void addRightView(View r,CharSequence name){
        assignId(r);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        if(preview!=null)
            params.addRule(RelativeLayout.BELOW,preview.getId());
        params.rightMargin = 10;
        preview = r;
        contentLayout.addView(r, params);
    }

    public void addBottomView(View b, CharSequence name)
    {
        assignId(b);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
        /*if(preview!=null)
            params.addRule(RelativeLayout.BELOW,preview.getId());*/

        params.leftMargin = 10;
        params.rightMargin = 10;
        params.topMargin = 50;
        params.bottomMargin = 50;
        contentLayout.addView(b, params);
    }

    private void addToLastLeftBelow(View r,View t,CharSequence name){
        assignId(r);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if(preview!=null)
            params.addRule(RelativeLayout.LEFT_OF,preview.getId());
        params.addRule(RelativeLayout.BELOW,t.getId());
        //params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        params.rightMargin = 10;
        preview = r;
        contentLayout.addView(r, params);
    }
}
