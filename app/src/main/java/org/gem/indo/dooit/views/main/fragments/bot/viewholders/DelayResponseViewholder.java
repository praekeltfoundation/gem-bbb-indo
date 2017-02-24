package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.view.View;
import com.github.channguyen.adv.AnimatedDotsView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.bot.Node;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Reinhardt on 2017/02/24.
 */

public class DelayResponseViewholder extends BaseBotViewHolder<Node> {

    @BindView(R.id.delay_dots)
    AnimatedDotsView dots;

    @Override
    protected void populateModel() {
    }

    public DelayResponseViewholder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        dots.startAnimation();

    }
}
