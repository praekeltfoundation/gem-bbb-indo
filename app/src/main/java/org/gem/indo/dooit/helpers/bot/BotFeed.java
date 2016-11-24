package org.gem.indo.dooit.helpers.bot;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.gem.indo.dooit.models.bot.BaseBotModel;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Bernhard Müller on 11/7/2016.
 */

public class BotFeed<T extends BaseBotModel> {

    private static final String TAG = BotFeed.class.getName();

    private HashMap<String, T> data = new HashMap<>();
    private Context context;
    private ArrayList<String> indexMap = new ArrayList<>();

    public BotFeed(Context context) {
        this.context = context;
    }

    public void parse(int rawResource, Class<T> typeClass) {
        InputStream is = context.getResources().openRawResource(rawResource);
        Gson gson = new Gson();

        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            ListParameterizedType type = new ListParameterizedType(typeClass);
            ArrayList<T> listItems = gson.fromJson(reader, type);
            data.clear();
            for (T item : listItems) {
                data.put(item.getName(), item);
                indexMap.add(item.getName());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error loading feed", e);
        } finally {
            try {
                is.close();
            } catch (Exception e) {
            }
        }
    }

    public T getFirstItem() {
        if (data.size() > 0) {
            return data.get(indexMap.get(0));
        } else {
            return null;
        }
    }

    public ArrayList<T> getItemList(ArrayList<String> keys) {
        ArrayList<T> itemList = new ArrayList<>();
        for (String key : keys) {
            itemList.add(data.get(key));
        }
        return itemList;
    }

    public T getItem(String key) {
        return data.get(key);
    }
}
