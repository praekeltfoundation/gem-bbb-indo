package org.gem.indo.dooit.views.main.fragments.challenge;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.R;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.ChallengeManager;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.views.DooitActivity;
import org.gem.indo.dooit.views.helpers.activity.DooitActivityBuilder;
import org.gem.indo.dooit.views.main.fragments.challenge.fragments.ChallengeFreeformFragment;
import org.gem.indo.dooit.views.main.fragments.challenge.fragments.ChallengePictureFragment;
import org.gem.indo.dooit.views.main.fragments.challenge.fragments.ChallengeQuizFragment;
import org.gem.indo.dooit.views.main.fragments.challenge.fragments.ChallengeRegisterFragment;

import java.net.ConnectException;
import java.net.UnknownHostException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by frede on 2017/01/16.
 */

public class ChallengeActivity extends DooitActivity {

    public static final String TAG = "ChallengeMain";
    public static final String ARG_CHALLENGE = "challenge";
    public static final String ARG_PAGE = "challenge_page";
    public static final String ARG_PARTICIPANT = "participant";
    private FragmentManager fragmentManager = null;

    @Inject
    ChallengeManager challengeManager;

    @Inject
    Persisted persisted;

    @BindView(R.id.fragment_challenge_container)
    FrameLayout container;

    BaseChallenge challenge;

    private Subscription challengeSubscription;

    public ChallengeActivity(){
        // Empty constructor
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_challenge);
        ((DooitApplication) getApplication()).component.inject(this);
        ButterKnife.bind(this);
        loadChallenge();
    }

    private Fragment createEmptyFragment(ChallengeFragmentState state) {
        if (state == null) return null;
        switch (state) {
            case FREEFORM:
                return new ChallengeFreeformFragment();
            case PICTURE:
                return new ChallengePictureFragment();
            case QUIZ:
                return new ChallengeQuizFragment();
            case REGISTER:
                return new ChallengeRegisterFragment();
            default:
                return null;
        }
    }


    /****************
     * Load helpers *
     ****************/

    private void loadTypeFragment(BaseChallenge challenge, boolean hasActive) {
        if (challenge != null) {
            this.challenge = challenge;

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
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
                    Toast.makeText(this, R.string.challenge_persisted_challenge_thrown_out, Toast.LENGTH_SHORT).show();
                    challengeSubscription = challengeManager.retrieveCurrentChallenge(false, new DooitErrorHandler() {
                        @Override
                        public void onError(final DooitAPIError error) {
                            runOnUiThread(new Runnable() {
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
                        runOnUiThread(new Runnable() {
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
                    runOnUiThread(new Runnable() {
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


    /*************************
     * State-keeping methods *
     *************************/

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            Fragment f = getSupportFragmentManager().getFragment(outState,challenge.toString());
            if (f != null) {
                f.onSaveInstanceState(outState);
            }
        }
        super.onSaveInstanceState(outState);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            ChallengeFragmentState page = null;
            try {
                page = (ChallengeFragmentState) savedInstanceState.getSerializable(ARG_PAGE);
            } catch (Exception e) {
                Log.d(TAG, "Could not load saved challenge state");
                e.printStackTrace();
            } finally {
                challenge = savedInstanceState.getParcelable(ARG_CHALLENGE);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment f = getSupportFragmentManager().getFragment(savedInstanceState,challenge.toString());
                if (f == null) {
                    f = createEmptyFragment(page);
                    if (f == null) {
                        loadChallenge();
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    public static class Builder extends DooitActivityBuilder<ChallengeActivity.Builder> {
        protected Builder(Context context) {
            super(context);
        }

        public static ChallengeActivity.Builder create(Context context) {
            ChallengeActivity.Builder builder = new ChallengeActivity.Builder(context);
            return builder;
        }

        @Override
        protected Intent createIntent(Context context) {
            return new Intent(context, ChallengeActivity.class);
        }
    }
}
