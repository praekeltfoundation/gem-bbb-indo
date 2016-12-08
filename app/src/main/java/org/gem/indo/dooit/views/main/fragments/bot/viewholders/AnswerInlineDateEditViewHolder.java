package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.greenfrvr.hashtagview.HashtagView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.Utils;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.enums.BotMessageType;
import org.gem.indo.dooit.views.custom.DatePickerFragment;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class AnswerInlineDateEditViewHolder extends BaseBotViewHolder<Answer> {

    @BindView(R.id.item_view_bot_inline_edit_view)
    EditText editText;

    BotAdapter botAdapter;
    HashtagView.TagsClickListener tagsClickListener;

    public AnswerInlineDateEditViewHolder(View itemView, BotAdapter botAdapter, HashtagView.TagsClickListener tagsClickListener) {
        super(itemView);
        this.botAdapter = botAdapter;
        this.tagsClickListener = tagsClickListener;
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void populate(Answer model) {
        super.populate(model);
        editText.setHint(dataModel.getInlineEditHint(getContext()));
        editText.setImeActionLabel("Done", KeyEvent.KEYCODE_ENTER);
        editText.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
        editText.setEnabled(false);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment dateFragment = new DatePickerFragment();
                dateFragment.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "datePicker");
                dateFragment.setOnCompleteListener(new DatePickerFragment.OnCompleteListener() {
                    @Override
                    public void onComplete(int year, int month, int day) {
                        Answer inputAnswer = new Answer();
                        Calendar cal = Calendar.getInstance(Locale.getDefault());
                        cal.set(year, month, day);
                        inputAnswer.setValue(Utils.formatDate(cal.getTime()) + " - " + Utils.weekDiff(cal.getTime().getTime(), Utils.ROUNDWEEK.UP) + " weeks");
                        inputAnswer.setName(dataModel.getName());
                        inputAnswer.setRemoveOnSelect(dataModel.getName());
                        inputAnswer.setNext(dataModel.getNextOnFinish());
                        inputAnswer.setType(BotMessageType.getValueOf(dataModel.getTypeOnFinish()));
                        tagsClickListener.onItemClicked(inputAnswer);
                    }
                });
            }
        });
        if (botAdapter.getItemCount() - 1 == getAdapterPosition()) {
            editText.performClick();
        }

//        .setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (KeyEvent.KEYCODE_ENTER == actionId) {
//                    dismissKeyboard(editText);
//                }
//                return false;
//            }
//        });
    }

    @Override
    protected void populateModel() {

    }
}
