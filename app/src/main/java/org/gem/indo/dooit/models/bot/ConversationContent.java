package org.gem.indo.dooit.models.bot;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.gem.indo.dooit.models.enums.BotObjectType;
import org.gem.indo.dooit.models.enums.BotType;

import io.realm.RealmObject;

/**
 * Links Model objects to be used by conversations in the Bot.
 *
 * Created by Wimpie Victor on 2017/03/06.
 */

public class ConversationContent extends RealmObject {

    private int botType;

    private int contentType;

    private long contentId;

    public ConversationContent() {
        // Required empty constructor
    }

    //////////////
    // Bot Type //
    //////////////

    @Nullable
    public BotType getBotType() {
        return BotType.byId(botType);
    }

    public void setBotType(@NonNull BotType botType) {
        this.botType = botType.getId();
    }

    //////////////////
    // Content Type //
    //////////////////

    @Nullable
    public BotObjectType getContentType() {
        return BotObjectType.byId(contentType);
    }

    public void setContentType(@NonNull BotObjectType contentType) {
        this.contentType = contentType.getId();
    }

    ////////////////
    // Content ID //
    ////////////////


    public long getContentId() {
        return contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }
}
