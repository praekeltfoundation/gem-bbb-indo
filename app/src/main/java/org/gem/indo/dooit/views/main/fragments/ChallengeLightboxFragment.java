package org.gem.indo.dooit.views.main.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.gem.indo.dooit.R;

/**
 * Created by Reinhardt on 2017/02/21.
 */

public class ChallengeLightboxFragment extends DialogFragment {

    public static ChallengeLightboxFragment newInstance(){
        ChallengeLightboxFragment fragment = new ChallengeLightboxFragment();

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(android.support.v4.app.DialogFragment.STYLE_NO_FRAME, org.gem.indo.dooit.R.style.AppTheme_PopupOverlay_Semitransparent_Dark);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.challenge_available_lightbox, null);

        return view;
    }
}
