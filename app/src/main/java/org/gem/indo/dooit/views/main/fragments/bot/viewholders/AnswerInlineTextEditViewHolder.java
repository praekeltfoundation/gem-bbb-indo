package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.greenfrvr.hashtagview.HashtagView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.crashlytics.CrashlyticsHelper;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.enums.BotMessageType;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class AnswerInlineTextEditViewHolder extends BaseBotViewHolder<Answer> {

    @BindView(R.id.item_view_bot_inline_edit_view)
    EditText editText;

    BotAdapter botAdapter;
    HashtagView.TagsClickListener tagsClickListener;

    public AnswerInlineTextEditViewHolder(View itemView, BotAdapter botAdapter, HashtagView.TagsClickListener tagsClickListener) {
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
        editText.setImeActionLabel("Done", EditorInfo.IME_ACTION_DONE);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        if (botAdapter.getItemCount() - 1 == getAdapterPosition()) {
            showKeyboard(editText);
        }
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // TODO: `Done` on keyboard still sends 66 (KeyEvent.KEYCODE_ENTER)
                if (EditorInfo.IME_ACTION_DONE == actionId) {
                    String input = v.getText().toString();

                    if (!validate(dataModel.getName(), input))
                        return true;

                    // Keyboard not dismissed on invalid input
                    dismissKeyboard(editText);

                    Answer inputAnswer = new Answer();
                    inputAnswer.setValue(input);
                    inputAnswer.setName(dataModel.getName());

                    if (dataModel.hasInputKey())
                        inputAnswer.setInputKey(dataModel.getInputKey());

                    inputAnswer.setRemoveOnSelect(dataModel.getName());
                    inputAnswer.setNext(dataModel.getNextOnFinish());
                    inputAnswer.setType(BotMessageType.getValueOf(dataModel.getTypeOnFinish()));
                    inputAnswer.setParentName(dataModel.getParentName());
                    tagsClickListener.onItemClicked(inputAnswer);
                    CrashlyticsHelper.log(this.getClass().getSimpleName(), "populate (textedit): ", "text: " + inputAnswer.getValue());
                    return true;
                }
                return false;
            }
        });
    }

    private boolean validate(String name, String input) {
        if (botAdapter == null || !botAdapter.hasController())
            return true;

        return botAdapter.getController().validate(name, input);
    }

    @Override
    protected void populateModel() {

    }

    @Override
    public void reset() {
        editText.setHint(null);
        editText.setText(null);
        editText.setOnEditorActionListener(null);
    }
}
