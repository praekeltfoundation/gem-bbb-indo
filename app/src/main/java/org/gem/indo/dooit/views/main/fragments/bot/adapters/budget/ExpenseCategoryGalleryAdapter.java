package org.gem.indo.dooit.views.main.fragments.bot.adapters.budget;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.dao.budget.ExpenseCategoryBotDAO;
import org.gem.indo.dooit.helpers.crashlytics.CrashlyticsHelper;
import org.gem.indo.dooit.models.budget.ExpenseCategory;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.budget.ExpenseCategoryGalleryItemVH;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Wimpie Victor on 2017/03/14.
 */

public class ExpenseCategoryGalleryAdapter extends RecyclerView.Adapter<ExpenseCategoryGalleryItemVH>
        implements ExpenseCategoryGalleryItemVH.OnCheckListener {

    public static String TAG = "ExpenseCategoryGalleryAdapter";

    private List<ExpenseCategory> data;
    private BotType botType;

    /**
     * @param botType Required to manage conversation specific DB entities.
     */
    public ExpenseCategoryGalleryAdapter(@NonNull BotType botType) {
        Set<Long> categoryIds = new TreeSet<>();
        List<ExpenseCategory> categories = ExpenseCategoryBotDAO.findAll(botType);
        this.data = new ArrayList<>();
        for (ExpenseCategory category : categories) {
            if (!categoryIds.contains(category.getId())) {
                this.data.add(category);
                categoryIds.add(category.getId());
            } else {
                CrashlyticsHelper.log(
                        TAG,
                        "constructor",
                        String.format("%s appearead multiple times.", category.getName())
                );
            }
        }
        this.botType = botType;
    }

    //////////////////////////
    // RecyclerView.Adapter //
    //////////////////////////

    @Override
    public ExpenseCategoryGalleryItemVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_bot_expense_category_card, parent, false);
        return new ExpenseCategoryGalleryItemVH(view);
    }

    @Override
    public void onBindViewHolder(ExpenseCategoryGalleryItemVH holder, int position) {
        holder.populate(data.get(position), this);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onCheck(long localId, boolean checked) {
        ExpenseCategoryBotDAO.setSelected(localId, checked);
    }
}
