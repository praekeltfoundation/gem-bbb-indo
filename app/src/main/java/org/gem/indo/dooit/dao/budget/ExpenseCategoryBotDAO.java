package org.gem.indo.dooit.dao.budget;

import android.support.annotation.NonNull;

import org.gem.indo.dooit.models.bot.ConversationContent;
import org.gem.indo.dooit.models.budget.ExpenseCategory;
import org.gem.indo.dooit.models.enums.BotObjectType;
import org.gem.indo.dooit.models.enums.BotType;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Wimpie Victor on 2017/03/06.
 */

public class ExpenseCategoryBotDAO {

    private static BotObjectType contentType = BotObjectType.EXPENSE_CATEGORIES;

    public void update(BotType botType, @NonNull List<ExpenseCategory> expenses) {
        Realm realm = null;

        try {
            realm = Realm.getDefaultInstance();

            realm.beginTransaction();

            realm.where(ConversationContent.class)
                    .equalTo("botType", botType.getId())
                    .equalTo("contentType", contentType.getId())
                    .findAll()
                    .deleteAllFromRealm();

            realm.copyToRealmOrUpdate(expenses);

            for (ExpenseCategory expense : expenses) {
                ConversationContent content = realm.createObject(ConversationContent.class);
                content.setBotType(botType);
                content.setContentType(contentType);
                content.setContentId(expense.getId());
            }

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

            RealmResults<ConversationContent> contents = realm.where(ConversationContent.class)
                    .equalTo("botType", botType.getId())
                    .equalTo("contentType", contentType.getId())
                    .findAll();
            contents.load();

            if (contents.isEmpty())
                return new ArrayList<>();

            Long[] ids = new Long[contents.size()];
            for (int i = 0; i < contents.size(); i++)
                ids[i] = contents.get(i).getContentId();

            return realm.where(ExpenseCategory.class)
                    .in("id", ids)
                    .findAll();
        } finally {
            if (realm != null)
                realm.close();
        }
    }
}
