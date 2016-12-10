package org.gem.indo.dooit.views.main.fragments.bot.viewholders;

import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.greenfrvr.hashtagview.HashtagView;

import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.Node;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.models.enums.BotMessageType;
import org.gem.indo.dooit.models.enums.BotObjectType;
import org.gem.indo.dooit.models.enums.BotParamType;
import org.gem.indo.dooit.views.main.MainActivity;
import org.gem.indo.dooit.views.main.MainViewPagerPositions;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Wimpie Victor on 2016/12/09.
 */

public class ChallengeBotViewHolder extends BaseBotViewHolder<Node> {

    @BindView(R.id.item_view_bot_challenge)
    View background;

    @BindView(R.id.item_view_bot_challenge_image)
    SimpleDraweeView imageView;

    @BindView(R.id.item_view_bot_challenge_image_sub_title)
    TextView subTitleView;

    @BindView(R.id.item_view_bot_challenge_image_title)
    TextView titleView;

    private BotAdapter botAdapter;
    private HashtagView.TagsClickListener listener;

    public ChallengeBotViewHolder(View itemView, BotAdapter botAdapter, HashtagView.TagsClickListener listener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.botAdapter = botAdapter;
        this.listener = listener;

        background.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bkg_carousel_card));
    }

    @Override
    public void populate(Node model) {
        super.populate(model);

        final long id = dataModel.values.getLong(BotParamType.CHALLENGE_ID.getKey());
        Uri imageUri = Uri.parse(dataModel.values.getString(BotParamType.CHALLENGE_IMAGE_URL.getKey()));
        String title = dataModel.values.getString(BotParamType.CHALLENGE_TITLE.getKey());
        String subtitle = dataModel.values.getString(BotParamType.CHALLENGE_SUBTITLE.getKey());

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(imageUri)
                .setProgressiveRenderingEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(imageView.getController())
                .build();
        imageView.setController(controller);

        titleView.setText(title);
        if (TextUtils.isEmpty(subtitle))
            subTitleView.setVisibility(View.GONE);
        else {
            subTitleView.setVisibility(View.VISIBLE);
            subTitleView.setText(subtitle);
        }

        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answer answer = new Answer();
                answer.setName(dataModel.getName());
                answer.setType(BotMessageType.BLANKANSWER);
                answer.setValue(Long.toString(id));
                answer.setText(null);
                listener.onItemClicked(answer);

                MainActivity activity = (MainActivity) itemView.getContext();
                if (activity != null)
                    activity.startPage(MainViewPagerPositions.CHALLENGE);
            }
        });
    }

    @Override
    protected void populateModel() {
        BaseChallenge challenge = (BaseChallenge) botAdapter.getController().getObject(BotObjectType.CHALLENGE);
        dataModel.values.put(BotParamType.CHALLENGE_ID.getKey(), challenge.getId());
        dataModel.values.put(BotParamType.CHALLENGE_IMAGE_URL.getKey(), challenge.getImageURL());
        dataModel.values.put(BotParamType.CHALLENGE_TITLE.getKey(), challenge.getName());
        dataModel.values.put(BotParamType.CHALLENGE_SUBTITLE.getKey(), challenge.getSubtitle());
    }

    @Override
    public void reset() {
        subTitleView.setVisibility(View.VISIBLE);
    }
}
