package org.gem.indo.dooit.dao.budget;

import org.gem.indo.dooit.models.budget.Expense;

import io.realm.Realm;

/**
 * Created by Wimpie Victor on 2017/04/08.
 */

public class ExpenseDAO {

    public static void delete(long expenseId) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.where(Expense.class)
                    .equalTo("id", expenseId)
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
