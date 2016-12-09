package org.gem.indo.dooit.controllers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.ChallengeManager;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.api.managers.TipManager;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.models.Tip;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.models.enums.BotObjectType;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.models.goal.Goal;
import org.gem.indo.dooit.models.goal.GoalPrototype;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Temporary helper to download resources from the server required for a bot conversation to work.
 * <p>
 * Created by Wimpie Victor on 2016/12/09.
 */

public class RequirementResolver {

    @Inject
    GoalManager goalManager;

    @Inject
    ChallengeManager challengeManager;

    @Inject
    TipManager tipManager;

    @Inject
    Persisted persisted;

    private BotType botType;
    private List<Observable> sync = new ArrayList<>();
    private List<BotObjectType> requirements;
    private Handler handler;

    private RequirementResolver(Context context, BotType botType, List<BotObjectType> requirements) {
        ((DooitApplication) context.getApplicationContext()).component.inject(this);
        this.botType = botType;
        this.requirements = requirements;
        handler = new Handler(Looper.getMainLooper());
    }

    public void resolve(final Callback callback) {
        // Retrieve requirements from persistence, or add them to the list to be downloaded.
        for (BotObjectType type : requirements)
            switch (type) {
                case CHALLENGE:
                    retrieveChallenge();
                    break;
                case GOALS:
                    retrieveGoals();
                    break;
                case GOAL_PROTOTYPES:
                    retrieveGoalPrototypes();
                    break;
                case TIP:
                    retrieveTip();
                    break;
            }

        if (sync.size() > 0) {
            // Requirements outstanding
            notifyStart(callback);
            Observable.from(sync).flatMap(new Func1<Observable, Observable<?>>() {
                @Override
                public Observable<?> call(Observable observable) {
                    return observable;
                }
            }).doAfterTerminate(new Action0() {
                @Override
                public void call() {
                    notifyDone(callback);
                }
            }).subscribe(new Action1<Object>() {
                @Override
                public void call(Object o) {
                    if (o instanceof List) {
                        if (((List) o).size() == 0) return;
                        if (((List) o).get(0) instanceof Goal)
                            persisted.saveConvoGoals(botType, (List<Goal>) o);
                        else if (((List) o).get(0) instanceof Tip)
                            persisted.saveConvoTip((Tip) ((List) o).get(0));
                        else if (((List) o).get(0) instanceof GoalPrototype)
                            persisted.saveGoalProtos((List<GoalPrototype>) o);
                    } else if (o instanceof BaseChallenge) {
                        persisted.saveConvoChallenge(botType, (BaseChallenge) o);
                    }
                }
            });
        } else
            // All requirements were in persistence
            notifyDone(callback);
    }

    private void notifyStart(final Callback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onStart();
            }
        });
    }

    private void notifyDone(final Callback callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onDone();
            }
        });
    }

    private void retrieveChallenge() {
        Observable challenge = challengeManager.retrieveCurrentChallenge(true, new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        });
        if (persisted.hasConvoGoals(botType))
            challenge.subscribe(new Action1<BaseChallenge>() {
                @Override
                public void call(BaseChallenge retrievedChallenge) {
                    persisted.saveConvoChallenge(botType, retrievedChallenge);
                }
            });
        else
            sync.add(challenge);
    }

    private void retrieveGoalPrototypes() {
        Observable<List<GoalPrototype>> protos = goalManager.retrieveGoalPrototypes(new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        });
        if (persisted.hasConvoGoals(botType))
            protos.subscribe(new Action1<List<GoalPrototype>>() {
                @Override
                public void call(List<GoalPrototype> goalPrototypes) {
                    if (goalPrototypes.size() == 0) return;
                    persisted.saveGoalProtos(goalPrototypes);
                }
            });
        else
            sync.add(protos);
    }

    private void retrieveGoals() {
        Observable<List<Goal>> goals = goalManager.retrieveGoals(new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        });
        if (persisted.hasConvoGoals(botType))
            goals.subscribe(new Action1<List<Goal>>() {
                @Override
                public void call(List<Goal> retrievedGoals) {
                    persisted.saveConvoGoals(botType, retrievedGoals);
                }
            });
        else
            sync.add(goals);
    }

    private void retrieveTip() {
        Observable tip = tipManager.retrieveTips(new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        });
        if (persisted.hasConvoGoals(botType))
            tip.subscribe(new Action1<List<Tip>>() {
                @Override
                public void call(List<Tip> retrieveTips) {
                    if (retrieveTips.size() == 0) return;
                    persisted.saveConvoTip(retrieveTips.get(0));
                }
            });
        else
            sync.add(tip);
    }

    public interface Callback {
        void onStart();

        void onDone();
    }

    public static class Builder {

        private List<BotObjectType> requirements = new ArrayList<>();
        private Context context;
        private BotType botType;

        public Builder(Context context, BotType botType) {
            this.context = context;
            this.botType = botType;
        }

        public Builder require(BotObjectType requirement) {
            requirements.add(requirement);
            return this;
        }

        public RequirementResolver build() {
            return new RequirementResolver(context, botType, requirements);
        }
    }
}
