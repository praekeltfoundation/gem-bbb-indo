package com.nike.dooit.views.main.fragments.bot.viewholders;

import android.content.Context;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.greenfrvr.hashtagview.HashtagView;
import com.nike.dooit.R;
import com.nike.dooit.models.bot.Answer;
import com.nike.dooit.views.main.fragments.bot.adapters.BotAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class AnswerInlineEditViewHolder extends BaseBotViewHolder<Answer> {
    @BindView(R.id.item_view_bot_inline_edit_view)
    EditText editText;
    BotAdapter botAdapter;
    HashtagView.TagsClickListener tagsClickListener;

    public AnswerInlineEditViewHolder(View itemView, BotAdapter botAdapter, HashtagView.TagsClickListener tagsClickListener) {
        super(itemView);
        this.botAdapter = botAdapter;
        this.tagsClickListener = tagsClickListener;
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void populate(Answer model) {
        this.dataModel = model;
        editText.setHint(dataModel.getInlineEditHint(getContext()));
        editText.setImeActionLabel("Done", KeyEvent.KEYCODE_ENTER);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        if (botAdapter.getItemCount() - 1 == getAdapterPosition()) {
            showKeyboard(editText);
        }
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (KeyEvent.KEYCODE_ENTER == actionId) {
                    dismissKeyboard(editText);
                    Answer inputAnswer = new Answer();
                    inputAnswer.setValue(v.getText().toString());
                    inputAnswer.setName(v.getText().toString());
                    inputAnswer.setRemoveOnSelect(dataModel.getName());
                    inputAnswer.setNext(dataModel.getNext());
                    tagsClickListener.onItemClicked(inputAnswer);
                    return true;
                }
                return false;
            }
        });
    }

    public Context getContext() {
        return itemView.getContext();
    }
}
