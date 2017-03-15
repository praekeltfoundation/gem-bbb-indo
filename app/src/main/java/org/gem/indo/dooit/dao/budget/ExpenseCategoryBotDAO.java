package org.gem.indo.dooit.dao.budget;

import android.support.annotation.NonNull;

import org.gem.indo.dooit.models.budget.ExpenseCategory;
import org.gem.indo.dooit.models.enums.BotObjectType;
import org.gem.indo.dooit.models.enums.BotType;

import java.util.List;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;

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
    public OrderedRealmCollection<ExpenseCategory> findAll(BotType botType) {
        Realm realm = null;

        try {
            realm = Realm.getDefaultInstance();

            return realm.where(ExpenseCategory.class)
                    .equalTo(ExpenseCategory.FIELD_BOT_TYPE, botType.getId())
                    .findAll();
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
}
