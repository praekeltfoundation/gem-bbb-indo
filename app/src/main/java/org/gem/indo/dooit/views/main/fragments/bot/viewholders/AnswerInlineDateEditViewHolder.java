package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.greenfrvr.hashtagview.HashtagView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.Utils;
import org.gem.indo.dooit.helpers.crashlytics.CrashlyticsHelper;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.date.WeekCalc;
import org.gem.indo.dooit.models.enums.BotMessageType;
import org.gem.indo.dooit.views.custom.DatePickerFragment;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class AnswerInlineDateEditViewHolder extends BaseBotViewHolder<Answer> {

    @BindView(R.id.item_view_bot_inline_edit_view)
    EditText editText;

    @BindString(R.string.goal_add_number_of_weeks)
    String weekText;

    BotAdapter botAdapter;
    HashtagView.TagsClickListener tagsClickListener;

    public AnswerInlineDateEditViewHolder(View itemView, BotAdapter botAdapter, HashtagView.TagsClickListener tagsClickListener) {
        super(itemView);
        this.botAdapter = botAdapter;
        this.tagsClickListener = tagsClickListener;
        ButterKnife.bind(this, itemView);

        editText.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_d_answer_dialogue_bkg_blue));
    }

    @Override
    public void populate(Answer model) {
        super.populate(model);
        editText.setHint(dataModel.getInlineEditHint(getContext()));
        editText.setImeActionLabel("Done", KeyEvent.KEYCODE_ENTER);
        editText.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
        // The combination of these two flags will allow the edit text to be clicked, but not accept
        // any kayboard input.
        editText.setEnabled(true);
        editText.setFocusable(false);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment dateFragment = new DatePickerFragment();
                dateFragment.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), "datePicker");
                dateFragment.setOnCompleteListener(new DatePickerFragment.OnCompleteListener() {
                    @Override
                    public void onComplete(int year, int month, int day) {
                        Answer inputAnswer = new Answer();

                        Date startDate = WeekCalc.removeTime(new Date());
                        Calendar cal = Calendar.getInstance(Locale.getDefault());
                        cal.set(year, month, day);
                        Date endDate = WeekCalc.removeTime(cal.getTime());

                        int weeks = (int) WeekCalc.weekDiff(startDate, endDate, WeekCalc.Rounding.DOWN);
                        int days = WeekCalc.remainder(startDate, endDate);
                        String weekMsg = String.format(weekText, weeks, days);

                        inputAnswer.setValue(
                                (DateFormat.getInstance().format(cal.getTime()))
                                        .substring(0, (DateFormat.getInstance().format(endDate)).indexOf(" "))
                                        + " - " + weekMsg);
                        //inputAnswer.setValue(DateFormat.getInstance().format(cal.getTime()) + " - " + Utils.weekDiff(cal.getTime().getTime(), Utils.ROUNDWEEK.UP) + " " + weeks);
                        inputAnswer.values.put("date", Utils.formatDate(cal.getTime()));
                        inputAnswer.setText(null);
                        inputAnswer.setName(dataModel.getName());
                        inputAnswer.setRemoveOnSelect(dataModel.getName());
                        inputAnswer.setNext(dataModel.getNextOnFinish());
                        inputAnswer.setType(BotMessageType.getValueOf(dataModel.getTypeOnFinish()));
                        tagsClickListener.onItemClicked(inputAnswer);
                        CrashlyticsHelper.log(this.getClass().getSimpleName(), "populate", "(dateEdit) New date: " + inputAnswer.getValue());
                    }
                });
                dateFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        // Do nothing on dismiss
                        CrashlyticsHelper.log(this.getClass().getSimpleName(), "populate", "Date Picker Dismissed");
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

    @Override
    public void reset() {
        editText.setHint(null);
        editText.setOnClickListener(null);
    }
}
