package org.gem.indo.dooit.views.main.fragments.challenge;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.ChallengeManager;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.RequestCodes;
import org.gem.indo.dooit.helpers.SquiggleBackgroundHelper;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.models.challenge.PictureChallenge;
import org.gem.indo.dooit.views.main.fragments.MainFragment;
import org.gem.indo.dooit.views.main.fragments.challenge.fragments.ChallengeNoneFragment;
import org.gem.indo.dooit.views.main.fragments.challenge.fragments.ChallengePictureFragment;
import org.gem.indo.dooit.views.main.fragments.challenge.fragments.ChallengeRegisterFragment;
import org.gem.indo.dooit.views.main.fragments.challenge.interfaces.HasChallengeFragmentState;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;

public class ChallengeFragment extends MainFragment {
    public static final String TAG = "ChallengeMain";
    public static final String ARG_CHALLENGE = "challenge";
    public static final String ARG_PAGE = "challenge_page";

    @Inject
    ChallengeManager challengeManager;

    @Inject
    Persisted persisted;

//    @BindView(R.id.fragment_challenge_loadingprogress)
//    ProgressBar progressBar;
//
//    @BindView(R.id.fragment_challenge_loadingtext)
//    TextView progressText;

    @BindView(R.id.fragment_challenge_container)
    FrameLayout container;

    BaseChallenge challenge;

    ChallengeFragmentState page = ChallengeFragmentState.NONE;

    Unbinder unbinder;
    private Observable<BaseChallenge> challengeSubscription;

    public ChallengeFragment() {
        // Required empty public constructor
    }

    public static ChallengeFragment newInstance() {
        ChallengeFragment fragment = new ChallengeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((DooitApplication) getActivity().getApplication()).component.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(org.gem.indo.dooit.R.layout.fragment_challenge, container, false);
        unbinder = ButterKnife.bind(this, view);
        SquiggleBackgroundHelper.setBackground(getContext(), R.color.grey_back, R.color.grey_fore, container);
//        progressBar.setVisibility(View.VISIBLE);
//        progressText.setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public void onDestroyView() {
//        progressBar.setVisibility(View.GONE);
//        progressText.setVisibility(View.GONE);
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    private void loadTypeFragment(BaseChallenge challenge, boolean hasActive) {
        if (challenge != null) {
            ChallengeFragment.this.challenge = challenge;

            if (getActivity() != null) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment fragment = ChallengeRegisterFragment.newInstance(challenge, hasActive);
                ft.add(R.id.fragment_challenge_container, fragment, "fragment_challenge");
                ft.commit();
            }
        } else {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            Fragment fragment = ChallengeNoneFragment.newInstance();
            ft.add(R.id.fragment_challenge_container, fragment, "fragment_challenge");
            ft.commit();
        }
    }

    private void loadChallenge() {
        if (persisted.hasCurrentChallenge()) {
            try {
                challenge = (BaseChallenge) persisted.getCurrentChallenge();
                if (challenge.getDeactivationDate().isBeforeNow()) {
                    persisted.clearCurrentChallenge();
                } else {
                    loadTypeFragment(challenge, true);
                    return;
                }
            } catch (Exception e) {
                Log.d(TAG, "Could not load persisted challenge");
                persisted.clearCurrentChallenge();
            }
        }

        challengeSubscription = challengeManager.retrieveCurrentChallenge(false, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {
                Toast.makeText(getContext(), "Could not retrieve challenge", Toast.LENGTH_SHORT).show();
            }
        });

        challengeSubscription
                .subscribe(new Action1<BaseChallenge>() {
                    @Override
                    public void call(BaseChallenge challenge) {
                        loadTypeFragment(challenge, false);
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (progressBar != null) {
//                                    progressBar.setVisibility(View.GONE);
//                                }
//                                if (progressText != null) {
//                                    progressText.setVisibility(View.GONE);
//                                }
//
//                            }
//                        });
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (page == ChallengeFragmentState.NONE) {
            loadChallenge();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (challengeSubscription != null)
            challengeSubscription.doOnUnsubscribe(new Action0() {
                @Override
                public void call() {

                }
            });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(org.gem.indo.dooit.R.menu.menu_main, menu);
        getActivity().getMenuInflater().inflate(org.gem.indo.dooit.R.menu.menu_main_challenge, menu);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            Fragment f = getActivity().getSupportFragmentManager().findFragmentByTag("fragment_challenge");
            if (f instanceof HasChallengeFragmentState) {
                outState.putSerializable(ARG_PAGE, ((HasChallengeFragmentState) f).getFragmentState());
                f.onSaveInstanceState(outState);
            }
//            outState.putParcelable(ARG_PARTICIPANT, participant);
            outState.putParcelable(ARG_CHALLENGE, challenge);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            try {
                page = (ChallengeFragmentState) savedInstanceState.getSerializable(ARG_PAGE);
            } catch (Exception e) {
                Log.d(TAG, "Could not load saved challenge state");
                page = ChallengeFragmentState.NONE;
            }
            if (page == null)
                page = ChallengeFragmentState.NONE;
            challenge = savedInstanceState.getParcelable(ARG_CHALLENGE);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            Fragment f;
            switch (page) {
                case PICTURE:
                    f = new ChallengePictureFragment();
                    break;
                default:
                    loadChallenge();
                    return;
            }
            f.setArguments(savedInstanceState);
            ft.replace(R.id.fragment_challenge_container, f, "fragment_challenge");
            ft.commit();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RequestCodes.RESPONSE_GALLERY_REQUEST_CHALLENGE_IMAGE:
            case RequestCodes.RESPONSE_CAMERA_REQUEST_CHALLENGE_IMAGE:
                if (getActivity() != null) {
                    Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("fragment_challenge");
                    if (fragment != null) {
                        fragment.onActivityResult(requestCode, resultCode, data);
                    }
                }
                break;
        }
    }
}
