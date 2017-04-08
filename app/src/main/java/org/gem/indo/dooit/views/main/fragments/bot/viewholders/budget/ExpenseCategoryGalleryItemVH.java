package org.gem.indo.dooit.views.main.fragments.bot.viewholders.budget;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.images.DraweeHelper;
import org.gem.indo.dooit.models.budget.ExpenseCategory;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Wimpie Victor on 2017/03/14.
 */

public class ExpenseCategoryGalleryItemVH extends RecyclerView.ViewHolder {

    @BindView(R.id.item_view_bot_expense_category_card)
    View backgroundView;

    @BindView(R.id.item_view_bot_expense_category_card_separator)
    View separatorView;

    @BindView(R.id.item_view_bot_expense_category_card_image)
    SimpleDraweeView imageView;

    @BindView(R.id.item_view_bot_expense_category_card_overlay)
    View overlayView;

    @BindView(R.id.item_view_bot_expense_category_card_title)
    TextView titleView;

    @BindView(R.id.item_view_bot_expense_category_card_check)
    CheckBox checkView;

    private long localId;
    private boolean enabled;
    private boolean checked;
    private OnCheckListener listener;

    public ExpenseCategoryGalleryItemVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        // Must assign programmatically for Support Library to wrap before API 21
        backgroundView.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bkg_carousel_card));
        separatorView.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bkg_carousel_separator));
    }

    public void populate(@NonNull ExpenseCategory category, @NonNull OnCheckListener listener) {
        reset();

        this.listener = listener;

        // Local Id
        this.localId = category.getLocalId();

        // Image
        if (category.hasImageUrl())
            DraweeHelper.setProgressiveUri(imageView, Uri.parse(category.getImageUrl()));

        // Name
        titleView.setText(category.getName());

        // Enabled
        enabled = category.isEnabled();
        backgroundView.setClickable(enabled);
        overlayView.setVisibility(enabled ? View.GONE : View.VISIBLE);
        checkView.setVisibility(enabled ? View.VISIBLE : View.INVISIBLE);

        // Checkbox
        checked = category.isSelected();
        checkView.setChecked(checked);
    }

    public void reset() {
        localId = 0;
        enabled = false;
        checked = false;
        listener = null;
        titleView.setText("");
    }

    @OnClick(R.id.item_view_bot_expense_category_card)
    public void onCardClicked(View view) {
        checked = !checked;
        checkView.setChecked(checked);
        notifyCheck();
    }

    private void notifyCheck() {
        if (listener != null)
            listener.onCheck(localId, checked);
    }

    public interface OnCheckListener {
        void onCheck(long localId, boolean checked);
    }
}
