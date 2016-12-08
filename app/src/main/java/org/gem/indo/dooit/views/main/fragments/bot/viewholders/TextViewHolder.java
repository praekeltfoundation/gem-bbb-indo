package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.bot.param.ParamArg;
import org.gem.indo.dooit.helpers.bot.param.ParamMatch;
import org.gem.indo.dooit.helpers.bot.param.ParamParser;
import org.gem.indo.dooit.controllers.BotController;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class TextViewHolder extends BaseBotViewHolder<Node> {
    @BindView(R.id.item_view_bot_text)
    TextView textView;
    @BindView(R.id.item_view_bot_icon)
    ImageView botIcon;
    @Inject
    Persisted persisted;

    BotAdapter botAdapter;

    public TextViewHolder(View itemView, BotAdapter botAdapter) {
        super(itemView);
        this.botAdapter = botAdapter;
        ((DooitApplication) getContext().getApplicationContext()).component.inject(this);
        ButterKnife.bind(this, itemView);

        // Must assign programmatically for Support Library to wrap before API 21
        textView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_d_bot_dialogue_bkg));
    }

    @Override
    public void populate(Node model) {
        super.populate(model);

        textView.setText(dataModel.getProcessedText());

//        String text = dataModel.getText(getContext());
//        textView.setText(Utils.populateFromPersisted(persisted, botAdapter, text, model.getTextParams()));


        // Bot icon
        RelativeLayout.LayoutParams lp = ((RelativeLayout.LayoutParams) textView.getLayoutParams());
        if (dataModel.isIconHidden()) {
            botIcon.setVisibility(View.GONE);
            lp.setMargins(lp.leftMargin, 0, lp.rightMargin, lp.bottomMargin);
        } else {
            botIcon.setVisibility(View.VISIBLE);
            lp.setMargins(lp.leftMargin, lp.rightMargin, lp.rightMargin, lp.bottomMargin);
        }
        textView.setLayoutParams(lp);
    }

    @Override
    protected void populateModel() {
        ParamMatch args = ParamParser.parse(dataModel.getText(getContext()));
        if (!args.isEmpty() && botAdapter.hasController()) {
            BotController cb = botAdapter.getController();
            for (ParamArg arg : args.getArgs())
                cb.resolveParam(dataModel, BotParamType.byKey(arg.getKey()));
        }
        dataModel.setProcessedText(args.process(dataModel.values.getRawMap()));
    }

    @Override
    public void reset() {
        botIcon.setVisibility(View.VISIBLE);
    }

    /*public Context getContext() {
        return itemView.getContext();
    }*/
}
