package org.gem.indo.dooit.views.main.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.models.challenge.BaseChallenge;

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

    @BindView(R.id.challenge_available_dialog_close)
    ImageButton closeButton;

    public static ChallengeLightboxFragment newInstance(BaseChallenge challenge){
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

        challenge = savedInstanceState.getParcelable(CHALLENGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.challenge_available_lightbox, null);
        ButterKnife.bind(this, view);
        //unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.challenge_available_dialog_close)
    public void closePopup(View v){
        getActivity().getFragmentManager().popBackStack();
    }
}
