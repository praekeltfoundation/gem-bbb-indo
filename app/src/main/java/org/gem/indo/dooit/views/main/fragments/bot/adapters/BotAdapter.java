package org.gem.indo.dooit.views.main.fragments.bot.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.greenfrvr.hashtagview.HashtagView;

import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.enums.BotMessageType;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.AnswerImageSelectViewHolder;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.AnswerImageViewHolder;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.AnswerInlineDateEditViewHolder;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.AnswerInlineNumberEditViewHolder;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.AnswerInlineTextEditViewHolder;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.AnswerTextCurrencyViewHolder;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.AnswerViewHolder;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.BaseBotViewHolder;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.GoalReminderViewHolder;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.GoalVerificationViewHolder;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.TextViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class BotAdapter extends RecyclerView.Adapter<BaseBotViewHolder> {
    Context context;
    ArrayList<BaseBotModel> dataSet = new ArrayList<>();
    HashtagView.TagsClickListener tagsClickListener;

    public BotAdapter(Context context, HashtagView.TagsClickListener tagsClickListener) {
        this.context = context;
        this.tagsClickListener = tagsClickListener;
    }

    @Override
    public BaseBotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (BotMessageType.getValueOf(viewType)) {
            case TEXT:
                return new TextViewHolder(LayoutInflater.from(context).inflate(org.gem.indo.dooit.R.layout.item_view_bot_text, parent, false), this);
            case GOALSELECTION:
                return new TextViewHolder(LayoutInflater.from(context).inflate(org.gem.indo.dooit.R.layout.item_view_bot_text, parent, false), this);
            case ANSWER:
                return new AnswerViewHolder(LayoutInflater.from(context).inflate(org.gem.indo.dooit.R.layout.item_view_bot_answer, parent, false));
            case INLINETEXT:
                return new AnswerInlineTextEditViewHolder(LayoutInflater.from(context).inflate(org.gem.indo.dooit.R.layout.item_view_bot_inline_edit, parent, false), this, tagsClickListener);
            case CAMERAUPLOAD:
            case GALLERYUPLOAD:
                return new AnswerImageSelectViewHolder(LayoutInflater.from(context).inflate(org.gem.indo.dooit.R.layout.item_view_bot_select_image, parent, false), this, tagsClickListener);
            case IMAGE:
                return new AnswerImageViewHolder(LayoutInflater.from(context).inflate(org.gem.indo.dooit.R.layout.item_view_bot_image, parent, false), this, tagsClickListener);
            case INLINENUMBER:
                return new AnswerInlineNumberEditViewHolder(LayoutInflater.from(context).inflate(org.gem.indo.dooit.R.layout.item_view_bot_inline_edit, parent, false), this, tagsClickListener);
            case INLINECURRENCY:
                return new AnswerInlineNumberEditViewHolder(LayoutInflater.from(context).inflate(org.gem.indo.dooit.R.layout.item_view_bot_inline_edit_currency, parent, false), this, tagsClickListener);
            case INLINEDATE:
                return new AnswerInlineDateEditViewHolder(LayoutInflater.from(context).inflate(org.gem.indo.dooit.R.layout.item_view_bot_inline_edit, parent, false), this, tagsClickListener);
            case TEXTCURRENCY:
                return new AnswerTextCurrencyViewHolder(LayoutInflater.from(context).inflate(org.gem.indo.dooit.R.layout.item_view_bot_answer, parent, false), this);
            case GOALVERIFICATION:
                return new GoalVerificationViewHolder(LayoutInflater.from(context).inflate(org.gem.indo.dooit.R.layout.item_view_bot_text, parent, false), this);
            case GOALREMINDER:
                return new GoalReminderViewHolder(LayoutInflater.from(context).inflate(org.gem.indo.dooit.R.layout.item_view_bot_text, parent, false));
            case UNDEFINED:
            default:
                return new TextViewHolder(LayoutInflater.from(context).inflate(org.gem.indo.dooit.R.layout.item_view_bot_text, parent, false), this);
        }
    }

    @Override
    public void onBindViewHolder(BaseBotViewHolder holder, int position) {
        holder.populate(getItem(position));
    }

    @Override
    public int getItemViewType(int position) {
        BaseBotModel model = getItem(position);
        return BotMessageType.getValueOf(model.getType()).getValue();
    }

    public BaseBotModel getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void clear() {
        int size = dataSet.size();
        dataSet.clear();
        notifyItemRangeRemoved(0, size);
    }

    public synchronized void setItem(BaseBotModel item) {
        clear();
        int pos = dataSet.size();
        dataSet.add(pos, item);
        notifyItemInserted(pos);
    }

    public synchronized void addItem(BaseBotModel item) {
        int pos = dataSet.size();
        dataSet.add(pos, item);
        notifyItemInserted(pos);
    }

    public synchronized void addItems(List<BaseBotModel> items) {
        int size = dataSet.size();
        dataSet.clear();
        notifyItemRangeRemoved(0, size);
        for (BaseBotModel item : items) {
            int pos = dataSet.size();
            dataSet.add(pos, item);
            notifyItemInserted(pos);
        }
    }

    public ArrayList<BaseBotModel> getDataSet() {
        return dataSet;
    }

    public void removeItem(BaseBotModel model) {
        int index = dataSet.indexOf(model);
        if (index != -1) {
            dataSet.remove(model);
            notifyItemRemoved(index);
        }
    }
}
