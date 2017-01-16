package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.greenfrvr.hashtagview.HashtagView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.enums.BotMessageType;
import org.gem.indo.dooit.views.helpers.activity.CurrencyHelper;
import org.gem.indo.dooit.views.helpers.activity.NumberTextWatcher;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class AnswerInlineNumberEditViewHolder extends BaseBotViewHolder<Answer> {

    private static final String DEFAULT = "0";

    @BindView(R.id.item_view_bot_inline_edit_currency_container)
    View background;

    @BindView(R.id.item_view_bot_inline_edit_view)
    EditText editText;

    @BindView(R.id.item_view_bot_inline_edit_currency_view)
    TextView currencySymbol;

    private BotAdapter botAdapter;
    private HashtagView.TagsClickListener tagsClickListener;
    private NumberTextWatcher watcher;

    public AnswerInlineNumberEditViewHolder(View itemView, BotAdapter botAdapter, HashtagView.TagsClickListener tagsClickListener) {
        super(itemView);
        this.botAdapter = botAdapter;
        this.tagsClickListener = tagsClickListener;
        ButterKnife.bind(this, itemView);

        background.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_d_answer_dialogue_bkg));
    }

    @Override
    public void populate(Answer model) {
        super.populate(model);

        watcher = new NumberTextWatcher(editText);

        currencySymbol.setText(CurrencyHelper.getCurrencySymbol());
        editText.setText("");
        editText.addTextChangedListener(watcher);
        editText.setHint(dataModel.getInlineEditHint(getContext()));
        editText.setImeActionLabel("Done", EditorInfo.IME_ACTION_DONE);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (botAdapter.getItemCount() - 1 == getAdapterPosition()) {
            showKeyboard(editText);
        }
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (EditorInfo.IME_ACTION_DONE == actionId) {
                    dismissKeyboard(editText);
                    char separator = ((DecimalFormat) NumberFormat.getCurrencyInstance()).getDecimalFormatSymbols().getGroupingSeparator();
                    String stringSeparator = String.valueOf(separator);

                    String input = (v.getText().toString()).replace(stringSeparator,"");
                    Answer inputAnswer = new Answer();

                    inputAnswer.setValue(!TextUtils.isEmpty(input) ? input : DEFAULT);
                    inputAnswer.setName(dataModel.getName());
                    inputAnswer.setRemoveOnSelect(dataModel.getName());
                    inputAnswer.setNext(dataModel.getNextOnFinish());
                    inputAnswer.setType(BotMessageType.getValueOf(dataModel.getTypeOnFinish()));
                    tagsClickListener.onItemClicked(inputAnswer);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void populateModel() {

    }

    @Override
    public void reset() {
        editText.setText(null);
        editText.removeTextChangedListener(watcher);
        watcher = null;
        editText.setHint(null);
        editText.setOnEditorActionListener(null);
    }
}
