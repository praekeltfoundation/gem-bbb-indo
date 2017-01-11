package org.gem.indo.dooit.helpers.Connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.widget.Toast;

import org.gem.indo.dooit.R;

/**
 * Created by Reinhardt on 2017/01/09.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
    public static final String BROADCAST_ID = "networkStateChange";
    NetworkChangeListener listener;
    boolean connectionWasPerviouslyLost = false;

    public static NetworkChangeReceiver createNetworkBroadcastReceiver(NetworkChangeListener listener) {
        NetworkChangeReceiver receiver = new NetworkChangeReceiver();
        receiver.setListener(listener);
        return receiver;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

    public static void notifyUserOfNoInternetConnection(Context context) {
        Toast.makeText(context, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
    }

    public void setListener(NetworkChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (TextUtils.equals(BROADCAST_ID, intent.getAction())) {
            if (listener == null) return;
            if (NetworkChangeReceiver.isOnline(context)) {
                connectionWasPerviouslyLost = false;
                listener.onConnectionReestablished();
            } else {
                if (!connectionWasPerviouslyLost) {
                    connectionWasPerviouslyLost = true;
                    listener.onConnectionLost();
                }
            }
        } else {
            context.sendBroadcast(new Intent(BROADCAST_ID));
        }
    }

    public interface NetworkChangeListener {
        public void onConnectionLost();

        public void onConnectionReestablished();
    }
}
