package org.gem.indo.dooit.controllers;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import org.gem.indo.dooit.DooitApplication;
import org.gem.indo.dooit.api.DooitAPIError;
import org.gem.indo.dooit.api.DooitErrorHandler;
import org.gem.indo.dooit.api.managers.BudgetManager;
import org.gem.indo.dooit.api.managers.ChallengeManager;
import org.gem.indo.dooit.api.managers.GoalManager;
import org.gem.indo.dooit.api.managers.SurveyManager;
import org.gem.indo.dooit.api.managers.TipManager;
import org.gem.indo.dooit.dao.budget.BudgetDAO;
import org.gem.indo.dooit.dao.budget.ExpenseCategoryBotDAO;
import org.gem.indo.dooit.helpers.Persisted;
import org.gem.indo.dooit.helpers.images.DraweeHelper;
import org.gem.indo.dooit.models.Tip;
import org.gem.indo.dooit.models.budget.Budget;
import org.gem.indo.dooit.models.budget.ExpenseCategory;
import org.gem.indo.dooit.models.challenge.BaseChallenge;
import org.gem.indo.dooit.models.enums.BotObjectType;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.models.goal.Goal;
import org.gem.indo.dooit.models.goal.GoalPrototype;
import org.gem.indo.dooit.models.survey.CoachSurvey;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;
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
    BudgetManager budgetManager;

    @Inject
    ChallengeManager challengeManager;

    @Inject
    GoalManager goalManager;

    @Inject
    SurveyManager surveyManager;

    @Inject
    TipManager tipManager;

    @Inject
    Persisted persisted;

    private final BotType botType;
    private final List<BotObjectType> requirements;
    private final Handler handler;
    private List<Observable> sync = new ArrayList<>();

    private Long surveyId;

    private RequirementResolver(Context context, BotType botType, List<BotObjectType> requirements) {
        ((DooitApplication) context.getApplicationContext()).component.inject(this);
        this.botType = botType;
        this.requirements = requirements;
        handler = new Handler(Looper.getMainLooper());
    }

    @SuppressWarnings("unchecked")
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
                    retrieveGoalPrototypeImagesIfProtorypesArePersisted();
                    break;
                case TIP:
                    retrieveTip();
                    break;
                case SURVEY:
                    retrieveSurvey();
                    break;
                case EXPENSE_CATEGORIES:
                    retrieveExpenseCategories();
                    break;
                case BUDGET:
                    retrieveBudget();
                    break;
            }

        if (sync.size() > 0) {
            // Requirements outstanding
            // TODO: Does an HTTP exception in flatmap cause it to stop processing?
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
                public void call(final Object o) {
                    if (o instanceof List) {
                        if (((List) o).size() == 0) return;
                        if (((List) o).get(0) instanceof Goal)
                            persisted.saveConvoGoals(botType, (List<Goal>) o);
                        else if (((List) o).get(0) instanceof Tip)
                            persisted.saveConvoTip((Tip) ((List) o).get(0));
                        else if (((List) o).get(0) instanceof GoalPrototype) {
                            persisted.saveGoalProtos((List<GoalPrototype>) o);
                            retrieveGoalPrototypeImagesIfProtorypesArePersisted();
                        } else if (((List) o).get(0) instanceof CoachSurvey)
                            // Survey was retrieved by bot type
                            persisted.saveConvoSurvey(botType, ((List<CoachSurvey>) o).get(0));
                        else if (((List) o).get(0) instanceof ExpenseCategory) {
                            List<ExpenseCategory> categories = (List<ExpenseCategory>) o;
                            // Bot Conversation is frontend specific.
                            for (ExpenseCategory category : categories)
                                category.setBotType(botType);
                            new ExpenseCategoryBotDAO().insert(botType, (List<ExpenseCategory>) o);
                        } else if (((List) o).get(0) instanceof Budget) {
                            new BudgetDAO().update((Budget) ((List) o).get(0));
                        }
                    } else if (o instanceof BaseChallenge) {
                        persisted.saveConvoChallenge(botType, (BaseChallenge) o);
                    } else if (o instanceof CoachSurvey) {
                        persisted.saveConvoSurvey(botType, (CoachSurvey) o);
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
        if (persisted.hasConvoGoals(botType)) {
            BaseChallenge loadedChallenge = persisted.loadConvoChallenge(botType);
            if (loadedChallenge != null && loadedChallenge.getDeactivationDate().isBeforeNow()) {
                //persisted challenge has expired
                persisted.clearCurrentChallenge();
            }
            challenge.subscribe(new Action1<BaseChallenge>() {
                @Override
                public void call(BaseChallenge retrievedChallenge) {
                    persisted.saveConvoChallenge(botType, retrievedChallenge);
                }
            });
        } else {
            sync.add(challenge);
        }
    }

    private void retrieveGoalPrototypes() {
        Observable<List<GoalPrototype>> protos = goalManager.retrieveGoalPrototypes(new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        });
        if (persisted.hasGoalProtos())
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

    private void retrieveGoalPrototypeImagesIfProtorypesArePersisted() {
        if (persisted.hasGoalProtos()) {
            List<GoalPrototype> goalPrototypes = persisted.loadGoalProtos();
            for (GoalPrototype prototype : goalPrototypes) {
                if (prototype.hasImageUrl()) {
                    DraweeHelper.cacheImage(Uri.parse(prototype.getImageUrl()));
                }
            }
        }
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

    private void retrieveSurvey() {
        if (surveyId == null) {
            // Retrieve survey by type
            Observable survey = surveyManager.retrieveSurveys(botType, new DooitErrorHandler() {
                @Override
                public void onError(DooitAPIError error) {

                }
            });
            if (persisted.hasConvoSurvey(botType))
                survey.subscribe(new Action1<List<CoachSurvey>>() {
                    @Override
                    public void call(List<CoachSurvey> coachSurveys) {
                        if (coachSurveys.isEmpty()) return;
                        persisted.saveConvoSurvey(botType, coachSurveys.get(0));
                    }
                });
            else
                sync.add(survey);
        } else {
            // Retrieve survey by id
            // Used when opening specific surveys.
            Observable survey = surveyManager.retrieveSurvey(surveyId, botType, new DooitErrorHandler() {
                @Override
                public void onError(DooitAPIError error) {

                }
            });
            if (persisted.hasConvoSurvey(botType)
                    // Ensure the saved survey has the same ID as the requested survey
                    && persisted.loadConvoSurvey(botType).getId() == surveyId)
                survey.subscribe(new Action1<CoachSurvey>() {
                    @Override
                    public void call(CoachSurvey coachSurvey) {
                        if (coachSurvey == null) return;
                        persisted.saveConvoSurvey(botType, coachSurvey);
                    }
                });
            else
                sync.add(survey);
        }
    }

    @SuppressWarnings("unchecked")
    public void retrieveExpenseCategories() {
        Observable observable = budgetManager.retrieveExpenseCategories(new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        });

        // If the conversation has any categories, we don't download new ones. They will be cleared
        // at the end of the conversation.
        if (!new ExpenseCategoryBotDAO().exists(botType))
            sync.add(observable);
    }

    public void retrieveBudget() {
        Observable<List<Budget>> ob = budgetManager.retrieveBudgets(new DooitErrorHandler() {
            @Override
            public void onError(DooitAPIError error) {

            }
        });

        if (new BudgetDAO().hasBudget()) {
            ob.subscribe(new Action1<List<Budget>>() {
                @Override
                public void call(List<Budget> budgets) {
                    if (budgets != null && !budgets.isEmpty()) {
                        final Budget newBudget = budgets.get(0);
                        Realm mainRealm = Realm.getDefaultInstance();
                        mainRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmResults<Budget> existingBudgets = realm
                                        .where(Budget.class)
                                        .notEqualTo("id", newBudget.getId())
                                        .findAll();
                                existingBudgets.deleteAllFromRealm();
                            }
                        });
                        new BudgetDAO().update(newBudget);
                    }
                }
            });
        } else {
            sync.add(ob);
        }
    }

    public void setSurveyId(long surveyId) {
        this.surveyId = surveyId;
    }

    public interface Callback {
        void onStart();

        void onDone();
    }

    public static class Builder {

        private List<BotObjectType> requirements = new ArrayList<>();
        private Context context;
        private BotType botType;

        private Long surveyId;

        public Builder(Context context, BotType botType) {
            this.context = context;
            this.botType = botType;
        }

        public Builder require(BotObjectType requirement) {
            requirements.add(requirement);
            return this;
        }

        public Builder setSurveyId(Long id) {
            surveyId = id;
            return this;
        }

        public RequirementResolver build() {
            RequirementResolver resolver = new RequirementResolver(context, botType, requirements);
            if (surveyId != null)
                resolver.setSurveyId(surveyId);
            return resolver;
        }
    }
}
