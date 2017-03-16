package org.gem.indo.dooit.dao.budget;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.gem.indo.dooit.models.budget.ExpenseCategory;
import org.gem.indo.dooit.models.enums.BotObjectType;
import org.gem.indo.dooit.models.enums.BotType;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

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
                    .findAllSorted(ExpenseCategory.FIELD_ID));
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
                    .findAllSorted(ExpenseCategory.FIELD_ID));
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
                    .findAllSorted(ExpenseCategory.FIELD_ID);

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

            realm.beginTransaction();
            realm.where(ExpenseCategory.class)
                    .equalTo(ExpenseCategory.FIELD_LOCAL_ID, localId)
                    .findFirst()
                    .setSelected(checked);
            realm.commitTransaction();
        } catch (Throwable e) {
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
}
