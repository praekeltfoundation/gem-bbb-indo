package org.gem.indo.dooit.dao.budget;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.gem.indo.dooit.helpers.crashlytics.CrashlyticsHelper;
import org.gem.indo.dooit.models.budget.ExpenseCategory;
import org.gem.indo.dooit.models.enums.BotObjectType;
import org.gem.indo.dooit.models.enums.BotType;

import java.util.List;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Wimpie Victor on 2017/03/06.
 */

public class ExpenseCategoryBotDAO {

    private static BotObjectType contentType = BotObjectType.EXPENSE_CATEGORIES;

    public void insert(@NonNull BotType botType, @NonNull List<ExpenseCategory> categories) {
        Realm realm = null;

        try {
            realm = Realm.getDefaultInstance();

            realm.beginTransaction();

            Number lastId = realm.where(ExpenseCategory.class)
                    .equalTo(ExpenseCategory.FIELD_BOT_TYPE, botType.getId())
                    .max(ExpenseCategory.FIELD_ID);
            long nextId = 1;
            if (lastId != null)
                nextId = lastId.longValue() + 1;

            for (ExpenseCategory category : categories)
                if (category.getLocalId() == 0)
                    category.setLocalId(nextId++);

            realm.copyToRealm(categories);

            realm.commitTransaction();
        } catch (Throwable e) {
            if (realm != null && realm.isInTransaction())
                realm.cancelTransaction();
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    @NonNull
    public List<ExpenseCategory> findAll(BotType botType) {
        Realm realm = null;

        try {
            realm = Realm.getDefaultInstance();

            return realm.copyFromRealm(realm.where(ExpenseCategory.class)
                    .equalTo(ExpenseCategory.FIELD_BOT_TYPE, botType.getId())
                    .findAllSorted(
                            ExpenseCategory.FIELD_ORDER, Sort.ASCENDING,
                            ExpenseCategory.FIELD_ID, Sort.ASCENDING
                    ));
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    /**
     * Returns the selected {@link ExpenseCategory}s for the given bot conversation type.
     */
    @NonNull
    public List<ExpenseCategory> findSelected(BotType botType) {
        Realm realm = null;

        try {
            realm = Realm.getDefaultInstance();

            return realm.copyFromRealm(realm.where(ExpenseCategory.class)
                    .equalTo(ExpenseCategory.FIELD_BOT_TYPE, botType.getId())
                    .equalTo(ExpenseCategory.FIELD_SELECTED, true)
                    .findAllSorted(
                            ExpenseCategory.FIELD_ORDER, Sort.ASCENDING,
                            ExpenseCategory.FIELD_ID, Sort.ASCENDING
                    ));
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    /**
     * Returns the {@link ExpenseCategory} for the given bot conversation type with a specific ID.
     */
    @NonNull
    public ExpenseCategory findById(BotType botType, long id) {
        Realm realm = null;

        try {
            realm = Realm.getDefaultInstance();

            return realm.copyFromRealm(realm.where(ExpenseCategory.class)
                    .equalTo(ExpenseCategory.FIELD_BOT_TYPE, botType.getId())
                    .equalTo(ExpenseCategory.FIELD_ID, id)
                    .findFirst());
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    /**
     * Returns the unselected {@link ExpenseCategory}s for the given bot conversation type.
     */
    @NonNull
    public List<ExpenseCategory> findUnselected(BotType botType) {
        Realm realm = null;

        try {
            realm = Realm.getDefaultInstance();

            return realm.copyFromRealm(realm.where(ExpenseCategory.class)
                    .equalTo(ExpenseCategory.FIELD_BOT_TYPE, botType.getId())
                    .equalTo(ExpenseCategory.FIELD_SELECTED, false)
                    .findAllSorted(
                            ExpenseCategory.FIELD_ORDER, Sort.ASCENDING,
                            ExpenseCategory.FIELD_ID, Sort.ASCENDING
                    ));
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    @Nullable
    public ExpenseCategory findNext(BotType botType) {
        Realm realm = null;

        try {
            realm = Realm.getDefaultInstance();

            RealmResults<ExpenseCategory> categories = realm.where(ExpenseCategory.class)
                    .equalTo(ExpenseCategory.FIELD_BOT_TYPE, botType.getId())
                    .equalTo(ExpenseCategory.FIELD_SELECTED, true)
                    .equalTo(ExpenseCategory.FIELD_ENTERED, false)
                    .findAllSorted(
                            ExpenseCategory.FIELD_ORDER, Sort.ASCENDING,
                            ExpenseCategory.FIELD_ID, Sort.ASCENDING
                    );

            if (categories.isEmpty())
                return null;
            else
                return realm.copyFromRealm(categories.first());
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    public boolean hasNext(BotType botType) {
        Realm realm = null;

        try {
            realm = Realm.getDefaultInstance();

            return realm.where(ExpenseCategory.class)
                    .equalTo(ExpenseCategory.FIELD_BOT_TYPE, botType.getId())
                    .equalTo(ExpenseCategory.FIELD_SELECTED, true)
                    .equalTo(ExpenseCategory.FIELD_ENTERED, false)
                    .findAll()
                    .isEmpty();
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    /**
     * Clears the selected and entered flags on {@link ExpenseCategory} instances for a given
     * conversation.
     *
     * @param botType The type of the current conversation
     */
    public void clearAllState(BotType botType) {
        Realm realm = null;

        try {
            realm = Realm.getDefaultInstance();

            OrderedRealmCollection<ExpenseCategory> categories = realm.where(ExpenseCategory.class)
                    .equalTo(ExpenseCategory.FIELD_BOT_TYPE, botType.getId())
                    .findAll();

            realm.beginTransaction();
            for (ExpenseCategory category : categories) {
                category.setSelected(false);
                category.setEntered(false);
            }
            // FIXME: Realm exception. Can't commit non-existing write
            realm.commitTransaction();
        } catch (Throwable e) {
            if (realm != null && realm.isInTransaction())
                realm.cancelTransaction();
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    public boolean exists(@NonNull BotType botType) {
        Realm realm = null;

        try {
            realm = Realm.getDefaultInstance();

            return !realm.where(ExpenseCategory.class)
                    .equalTo(ExpenseCategory.FIELD_BOT_TYPE, botType.getId())
                    .findAll()
                    .isEmpty();
        } catch (Throwable e) {
            return false;
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    public void setSelected(long localId, boolean checked) {
        Realm realm = null;

        try {
            realm = Realm.getDefaultInstance();

            ExpenseCategory category = realm.where(ExpenseCategory.class)
                    .equalTo(ExpenseCategory.FIELD_LOCAL_ID, localId)
                    .findFirst();
            realm.beginTransaction();
            category.setSelected(checked);
            realm.commitTransaction();
        } catch (Throwable e) {
            if (realm != null && realm.isInTransaction())
                realm.cancelTransaction();
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    public void setEntered(BotType botType, long categoryId, boolean entered) {
        Realm realm = null;

        try {
            realm = Realm.getDefaultInstance();

            ExpenseCategory category = realm.where(ExpenseCategory.class)
                    .equalTo(ExpenseCategory.FIELD_BOT_TYPE, botType.getId())
                    .equalTo(ExpenseCategory.FIELD_ID, categoryId)
                    .findFirst();
            realm.beginTransaction();
            category.setEntered(entered);
            realm.commitTransaction();
        } catch (Exception e) {
            CrashlyticsHelper.logException(e);
            if (realm != null && realm.isInTransaction())
                realm.cancelTransaction();
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    public void clear(@NonNull BotType botType) {
        Realm realm = null;

        try {
            realm = Realm.getDefaultInstance();

            realm.beginTransaction();
            realm.where(ExpenseCategory.class)
                    .equalTo(ExpenseCategory.FIELD_BOT_TYPE, botType.getId())
                    .findAll()
                    .deleteAllFromRealm();
            realm.commitTransaction();
        } catch (Throwable e) {
            if (realm != null && realm.isInTransaction())
                realm.cancelTransaction();
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    public void clearAll() {
        Realm realm = null;

        try {
            realm = Realm.getDefaultInstance();

            RealmResults<ExpenseCategory> results = realm.where(ExpenseCategory.class)
                    .findAll();

            realm.beginTransaction();
            results.deleteAllFromRealm();
            realm.commitTransaction();
        } catch (Throwable e) {
            if (realm != null && realm.isInTransaction())
                realm.cancelTransaction();
        } finally {
            if (realm != null)
                realm.close();
        }
    }
}
