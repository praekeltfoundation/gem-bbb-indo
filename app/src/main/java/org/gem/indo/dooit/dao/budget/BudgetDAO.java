package org.gem.indo.dooit.dao.budget;

import android.support.annotation.Nullable;

import org.gem.indo.dooit.models.budget.Budget;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Wimpie Victor on 2017/03/07.
 */

public class BudgetDAO {

    public void update(Budget budget) {
        Realm realm = null;

        try {
            realm = Realm.getDefaultInstance();

            realm.beginTransaction();
            realm.copyToRealmOrUpdate(budget);
            realm.commitTransaction();
        } catch (Throwable t) {
            if (realm != null && realm.isInTransaction())
                realm.cancelTransaction();
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    @Nullable
    public Budget findFirst() {
        Realm realm = null;

        try {
            realm = Realm.getDefaultInstance();

            RealmResults<Budget> budgets = realm.where(Budget.class)
                    .findAll();
            if (!budgets.isEmpty())
                return realm.copyFromRealm(budgets.first());
            return null;
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    public boolean hasBudget() {
        Realm realm = null;

        try {
            realm = Realm.getDefaultInstance();
            return !realm.where(Budget.class)
                    .findAll()
                    .isEmpty();
        } finally {
            if (realm != null)
                realm.close();
        }
    }

    /**
     * @param budgetId ID of Budget to clear expenses from
     * @return The updated Budget. Returns null if budget wasn't found
     */
    @Nullable
    public static Budget clearExpenses(long budgetId) {
        Realm realm = null;
        Budget budget = null;

        try {
            realm = Realm.getDefaultInstance();

            budget = realm.where(Budget.class)
                    .equalTo(Budget.FIELD_ID, budgetId)
                    .findFirst();

            if (budget == null)
                return null;

            realm.beginTransaction();
            budget.clearExpenses();
            budget = realm.copyFromRealm(budget);
            realm.commitTransaction();
        } catch (Throwable e) {
            if (realm != null && realm.isInTransaction())
                realm.cancelTransaction();
        } finally {
            if (realm != null)
                realm.close();
        }
        return budget;
    }
}
