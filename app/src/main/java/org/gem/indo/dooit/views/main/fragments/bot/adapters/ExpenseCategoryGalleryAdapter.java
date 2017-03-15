package org.gem.indo.dooit.views.main.fragments.bot.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.budget.ExpenseCategory;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.views.main.fragments.bot.viewholders.ExpenseCategoryGalleryItemViewHolder;

import java.util.List;

import io.realm.Realm;

/**
 * Created by Wimpie Victor on 2017/03/14.
 */

public class ExpenseCategoryGalleryAdapter extends RecyclerView.Adapter<ExpenseCategoryGalleryItemViewHolder>
        implements ExpenseCategoryGalleryItemViewHolder.OnCheckListener {

    private List<ExpenseCategory> data;
    private BotType botType;

    /**
     * @param botType Required to manage conversation specific DB entities.
     */
    public ExpenseCategoryGalleryAdapter(List<ExpenseCategory> data,
                                         @NonNull BotType botType) {
        this.data = data;
        this.botType = botType;
    }

    //////////////////////////
    // RecyclerView.Adapter //
    //////////////////////////

    @Override
    public ExpenseCategoryGalleryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_bot_expense_category_card, parent, false);
        return new ExpenseCategoryGalleryItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExpenseCategoryGalleryItemViewHolder holder, int position) {
        holder.populate(data.get(position), this);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onCheck(long localId, boolean checked) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();

            realm.beginTransaction();
            realm.where(ExpenseCategory.class)
                    .equalTo(ExpenseCategory.FIELD_ID, localId)
                    .findFirst()
                    .setSelected(checked);
            realm.commitTransaction();
        } catch (Throwable e) {
            if (realm != null && realm.isInTransaction())
                realm.cancelTransaction();
        } finally {
            if (realm != null)
                realm.close();
        }
    }
}
