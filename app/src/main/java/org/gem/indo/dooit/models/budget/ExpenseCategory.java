package org.gem.indo.dooit.models.budget;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.gem.indo.dooit.helpers.strings.StringHelper;
import org.gem.indo.dooit.models.enums.BotType;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Holds the content of an expense.
 * <p>
 * Created by Wimpie Victor on 2017/03/03.
 */

public class ExpenseCategory extends RealmObject {

    public static final String FIELD_ID = "id";
    public static final String FIELD_LOCAL_ID = "localId";
    public static final String FIELD_BOT_TYPE = "botType";
    public static final String FIELD_SELECTED = "selected";

    /**
     * Because an {@link ExpenseCategory} only lives for the length of the conversation, and can't
     * be shared between conversations, it has a local only id.
     */
    @PrimaryKey
    private long localId;

    /**
     * The ID as provided by the server. A user's expense is linked to a category using this field.
     */
    private long id;

    @Nullable
    private String name;

    @SerializedName("image_url")
    @Nullable
    private String imageUrl;

    /**
     * A local field for use in the Expense carousel.
     */
    private boolean selected;

    /**
     * A local field that links this category to a bot conversation.
     */
    private int botType;

    //////////////////
    // Constructors //
    //////////////////

    public ExpenseCategory() {
        // Required empty constructor
    }


    //////////////
    // Local ID //
    //////////////

    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }

    ////////
    // ID //
    ////////

    public long getId() {
        return id;
    }

    //////////
    // Name //
    //////////

    @Nullable
    public String getName() {
        return name;
    }

    ///////////////
    // Image URL //
    ///////////////

    @Nullable
    public String getImageUrl() {
        return imageUrl;
    }

    public boolean hasImageUrl() {
        return !StringHelper.isEmpty(imageUrl);
    }

    //////////////
    // Selected //
    //////////////

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    ///////////////////////////
    // Bot Conversation Type //
    ///////////////////////////

    @Nullable
    public BotType getBotType() {
        return BotType.byId(botType);
    }

    public void setBotType(@NonNull BotType botType) {
        this.botType = botType.getId();
    }
}
