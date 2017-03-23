package org.gem.indo.dooit.views.main.fragments;

import android.app.DialogFragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.helpers.Utils;
import org.gem.indo.dooit.helpers.images.DraweeHelper;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.views.main.MainActivity;
import org.gem.indo.dooit.views.main.MainViewPagerPositions;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Reinhardt on 2017/02/21.
 */

public class ChallengeLightboxFragment extends DialogFragment {

    static final String CHALLENGE = "challenge";
    Unbinder unbinder = null;
    BaseChallenge challenge;


    @BindView(R.id.challenge_image)
    SimpleDraweeView challengeImage;

    @BindView(R.id.fragment_challenge_sub_title_text_view)
    TextView challengeSubtitle;

    @BindView(R.id.fragment_challenge_name_text_view)
    TextView challengeTitle;

    @BindView(R.id.fragment_challenge_expire_date_text_view)
    TextView challengeDate;

    @BindView(R.id.fragment_challenge_register_button)
    Button startChallengeButton;

    @BindView(R.id.challenge_available_dialog_close)
    ImageButton closeButton;

    @BindString(R.string.challenge_deadline_message)
    String deadlineMessage;

    public static ChallengeLightboxFragment newInstance(BaseChallenge challenge) {
        ChallengeLightboxFragment fragment = new ChallengeLightboxFragment();
        Bundle args = new Bundle();
        args.putParcelable(CHALLENGE, challenge);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DooitApplication) getActivity().getApplication()).component.inject(this);
        setStyle(android.support.v4.app.DialogFragment.STYLE_NO_FRAME, org.gem.indo.dooit.R.style.AppTheme_PopupOverlay_Semitransparent_Dark);
        Bundle args = this.getArguments();
        challenge = args.getParcelable(CHALLENGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.challenge_available_lightbox, null);
        ButterKnife.bind(this, view);

        String imgUrl = challenge.getImageURL();
        if (!TextUtils.isEmpty(imgUrl)) {
            DraweeHelper.setProgressiveUri(
                    challengeImage,
                    Uri.parse(imgUrl)
            );
        }
        if(challenge.getSubtitle() != null && !challenge.getSubtitle().equals("")) {
            challengeSubtitle.setText(challenge.getSubtitle());
        }else{
            challengeSubtitle.setVisibility(View.GONE);
        }

        challengeTitle.setText(challenge.getName());
        challengeDate.setText(deadlineMessage + " " + Utils.formatDateToLocal(challenge.getDeactivationDate().toDate()) +
                " " + challenge.getDeactivationDate().toLocalTime().toString("HH:mm"));

        return view;
    }

    @OnClick(R.id.challenge_available_dialog_close)
    public void closePopup(View v) {
        this.dismiss();
    }

    @OnClick(R.id.fragment_challenge_register_button)
    public void startChallenge(View v) {
        ((MainActivity) getActivity()).startPage(MainViewPagerPositions.CHALLENGE);
        this.dismiss();
    }
}
