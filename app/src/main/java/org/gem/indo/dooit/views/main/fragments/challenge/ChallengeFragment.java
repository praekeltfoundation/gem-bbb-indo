package org.gem.indo.dooit.views.main.fragments.challenge;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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
import org.gem.indo.dooit.helpers.SquiggleBackgroundHelper;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.views.main.fragments.MainFragment;
import org.gem.indo.dooit.views.main.fragments.challenge.fragments.ChallengeFreeformFragment;
import org.gem.indo.dooit.views.main.fragments.challenge.fragments.ChallengeNoneFragment;
import org.gem.indo.dooit.views.main.fragments.challenge.fragments.ChallengePictureFragment;
import org.gem.indo.dooit.views.main.fragments.challenge.fragments.ChallengeQuizFragment;
import org.gem.indo.dooit.views.main.fragments.challenge.fragments.ChallengeRegisterFragment;

import java.net.ConnectException;
import java.net.UnknownHostException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscription;
import rx.functions.Action1;

public class ChallengeFragment extends MainFragment {

    /*************
     * Variables *
     *************/

    public static final String TAG = "ChallengeMain";
    public static final String ARG_CHALLENGE = "challenge";
    public static final String ARG_PAGE = "challenge_page";
    public static final String ARG_PARTICIPANT = "participant";

    @Inject
    ChallengeManager challengeManager;

    @Inject
    Persisted persisted;

    @BindView(R.id.fragment_challenge_container)
    FrameLayout container;

    BaseChallenge challenge;

    Unbinder unbinder;
    private Subscription challengeSubscription;


    /****************
     * Constructors *
     ****************/

    public ChallengeFragment() {
        // Required empty public constructor
    }

    public static ChallengeFragment newInstance() {
        ChallengeFragment fragment = new ChallengeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    /**************************
     * Child fragment helpers *
     **************************/

    private boolean childFragmentExists() {
        if (getActivity() == null) return false;
        return getChildFragmentManager().findFragmentByTag("fragment_challenge") != null;
    }

    private Fragment getChildFragment() {
        if (getActivity() == null) return null;
        return getChildFragmentManager().findFragmentByTag("fragment_challenge");
    }

    private Fragment createEmptyChildFragment(ChallengeFragmentState state) {
        if (state == null) return new ChallengeNoneFragment();
        switch (state) {
            case FREEFORM:
                return new ChallengeFreeformFragment();
            case PICTURE:
                return new ChallengePictureFragment();
            case QUIZ:
                return new ChallengeQuizFragment();
            case REGISTER:
                return new ChallengeRegisterFragment();
            case NONE:
                return new ChallengeNoneFragment();
            default:
                return null;
        }
    }
    /************************
     * Life-cycle overrides *
     ************************/

    @Override
    public void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((DooitApplication) getActivity().getApplication()).component.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge, container, false);
        SquiggleBackgroundHelper.setBackground(getContext(), R.color.grey_back, R.color.grey_fore, view);
        unbinder = ButterKnife.bind(this, view);
        SquiggleBackgroundHelper.setBackground(getContext(), R.color.grey_back, R.color.grey_fore, container);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadChallenge();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (challengeSubscription != null)
            challengeSubscription.unsubscribe();
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        getActivity().getMenuInflater().inflate(R.menu.menu_main_challenge, menu);
    }

    /*************************
     * State-keeping methods *
     *************************/

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            if (getActivity() != null && childFragmentExists()) {
                Fragment f = getChildFragment();
                if (f != null) {
                    f.onSaveInstanceState(outState);
                }
            }
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ChallengeFragmentState page = ChallengeFragmentState.NONE;
        if (savedInstanceState != null) {
            page = ChallengeFragmentState.NONE;
            try {
                page = (ChallengeFragmentState) savedInstanceState.getSerializable(ARG_PAGE);
            } catch (Exception e) {
                Log.d(TAG, "Could not load saved challenge state");
                e.printStackTrace();
            } finally {
                challenge = savedInstanceState.getParcelable(ARG_CHALLENGE);
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                Fragment f = getChildFragment();
                if (f == null) {
                    f = createEmptyChildFragment(page);
                    if (f == null) {
                    } else {
                        f.setArguments(savedInstanceState);
                        ft.replace(R.id.fragment_challenge_container, f, "fragment_challenge");
                        ft.commit();
                    }
                } else {
                    ft.replace(R.id.fragment_challenge_container, f, "fragment_challenge");
                    ft.commit();
                }
            }
        }
        else {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            Fragment f = getChildFragment();
            f = createEmptyChildFragment(page);
            f.setArguments(savedInstanceState);
            ft.replace(R.id.fragment_challenge_container, f, "fragment_challenge");
            ft.commit();
        }
    }

    private void loadTypeFragment(BaseChallenge challenge, boolean hasActive) {
        if (challenge != null) {
            this.challenge = challenge;

            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            Fragment fragment = ChallengeRegisterFragment.newInstance(challenge, hasActive);
            ft.replace(R.id.fragment_challenge_container, fragment, "fragment_challenge");
            ft.commit();
        }
    }

    public void loadChallenge() {
        if (persisted.hasCurrentChallenge()) {
            try {
                challenge = persisted.getCurrentChallenge();
                if (challenge.getDeactivationDate().isBeforeNow()) {
                    //persisted challenge has expired
                    persisted.clearCurrentChallenge();
                    Toast.makeText(getContext(), R.string.challenge_persisted_challenge_thrown_out, Toast.LENGTH_SHORT).show();
                    challengeSubscription = challengeManager.retrieveCurrentChallenge(false, new DooitErrorHandler() {
                        @Override
                        public void onError(final DooitAPIError error) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(error.getCause() instanceof ConnectException ||
                                            error.getCause() instanceof UnknownHostException){
                                        Toast.makeText(context, R.string.challenge_could_not_connect_to_server, Toast.LENGTH_SHORT).show();
                                    }else if(error.getCause() instanceof HttpException && (((HttpException) error.getCause()).code()) == 404){
                                        //This means no challenge could be found on the server, for now just do nothing
                                    }
                                }
                            });
                        }
                    }).subscribe(new Action1<BaseChallenge>() {
                        @Override
                        public void call(BaseChallenge challenge) {
                            loadTypeFragment(challenge, false);
                        }
                    });
                } else {
                    loadTypeFragment(challenge, true);
                }
            } catch (Exception e) {
                Log.d(TAG, "Could not load persisted challenge");
                persisted.clearCurrentChallenge();
                Snackbar.make(container, R.string.challenge_persisted_challenge_thrown_out, Snackbar.LENGTH_LONG);
                challengeSubscription = challengeManager.retrieveCurrentChallenge(false, new DooitErrorHandler() {
                    @Override
                    public void onError(final DooitAPIError error) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(error.getCause() instanceof ConnectException ||
                                        error.getCause() instanceof UnknownHostException){
                                    Toast.makeText(context, R.string.challenge_could_not_connect_to_server, Toast.LENGTH_SHORT).show();
                                }else if(error.getCause() instanceof HttpException && (((HttpException) error.getCause()).code()) == 404){
                                    //This means no challenge could be found on the server, for now just do nothing
                                }
                            }
                        });
                    }
                }).subscribe(new Action1<BaseChallenge>() {
                    @Override
                    public void call(BaseChallenge challenge) {
                        loadTypeFragment(challenge, false);
                    }
                });
            }
        }else{

            challengeSubscription = challengeManager.retrieveCurrentChallenge(false, new DooitErrorHandler() {
                @Override
                public void onError(final DooitAPIError error) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(error.getCause() instanceof ConnectException ||
                                    error.getCause() instanceof UnknownHostException){
                                Toast.makeText(context, R.string.challenge_could_not_connect_to_server, Toast.LENGTH_SHORT).show();
                            }else if(error.getCause() instanceof HttpException && (((HttpException) error.getCause()).code()) == 404){
                                //This means no challenge could be found on the server, for now just do nothing
                            }
                        }
                    });
                }
            }).subscribe(new Action1<BaseChallenge>() {
                @Override
                public void call(BaseChallenge challenge) {
                    loadTypeFragment(challenge, false);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getChildFragment();
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            Fragment f = ChallengeDoneFragment.newInstance(null);
            ft.replace(R.id.fragment_challenge_container, f, "fragment_challenge");
            ft.commit();
        }
    }
}
