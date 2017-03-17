package org.gem.indo.dooit.dao.budget;

import android.support.annotation.Nullable;

import org.gem.indo.dooit.models.budget.Budget;

import java.util.List;

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
}
