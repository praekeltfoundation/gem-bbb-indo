package org.gem.indo.dooit.dao.budget;

import org.gem.indo.dooit.models.budget.Budget;

import io.realm.Realm;

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
}
