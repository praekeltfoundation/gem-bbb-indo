package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.gem.indo.dooit.Constants;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.enums.BotQuickAnswerBackground;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class AnswerViewHolder extends BaseBotViewHolder<Answer> {

    @BindView(R.id.item_view_bot_answer_text)
    TextView textView;

    @BindView(R.id.item_view_bot_answer_tail)
    ImageView tailView;

    private BotQuickAnswerBackground background;

    public AnswerViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        setBackground(BotQuickAnswerBackground.PRIMARY);
    }

    ///////////////////////
    // BaseBotViewHolder //
    ///////////////////////

    @Override
    public void populate(Answer model) {
        super.populate(model);

        // Text
        if (dataModel.hasProcessedText())
            textView.setText(dataModel.getProcessedText());
        else
            textView.setText(dataModel.getValue());

        // Event
        if (Constants.DEBUG)
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), getModel().getNext(), Toast.LENGTH_LONG).show();
                }
            });

        // Background
        setBackground(dataModel.getBackground());
    }

    @Override
    protected void populateModel() {

    }

    ////////////////
    // Background //
    ////////////////

    private void setBackground(@NonNull BotQuickAnswerBackground background) {
        Context context = itemView.getContext();
        if (context == null)
            return;

        // Keep the vectors from being rasterised again
        if (this.background == background)
            return;

        this.background = background;

        // Must assign programmatically for Support Library to wrap before API 21
        textView.setBackground(ContextCompat.getDrawable(context, background.getBackgroundResource()));

        // Vector needs to be rasterised into a bitmap
//        VectorDrawableCompat drawable = VectorDrawableCompat.create(context.getResources(),
//                background.getTailRes(), null);
        tailView.setImageDrawable(ContextCompat.getDrawable(context, background.getTailRes()));
    }
}
