package com.nike.dooit.helpers.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Werner Scheffer on 2016/03/03.
 */
@Singleton
public class PermissionsHelper {
    public static final String D_READ_CONTACTS = Manifest.permission.READ_CONTACTS;
    public static final String D_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    public static final String D_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String D_INTERNET = Manifest.permission.INTERNET;
    public static final String D_ACCESS_NETWORD_STATE = Manifest.permission.ACCESS_NETWORK_STATE;
    public static final String D_VIBRATE = Manifest.permission.VIBRATE;
    public static final String D_GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS;
    public static final String D_WAKE_LOCK = Manifest.permission.WAKE_LOCK;
    public static final String D_MODIFY_AUDIO_SETTINGS = Manifest.permission.MODIFY_AUDIO_SETTINGS;
    public static final String D_READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    public static final String D_WRITE_CONTACTS = Manifest.permission.WRITE_CONTACTS;
    public static final String D_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String D_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String D_CAMERA = Manifest.permission.CAMERA;

    private static ArrayList<PermissionRequest> permissionRequests = new ArrayList<PermissionRequest>();
    HashMap<String, ArrayList<PermissionCallback>> permissionListeners;

    @Inject
    public PermissionsHelper() {
    }

    public static boolean needPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Returns true if the Activity has access to a all given permission.
     */
    public static boolean needPermission(Activity activity, String[] permissions) {
        for (String permission : permissions) {
            if (needPermission(activity, permission)) {
                return false;
            }
        }
        return true;
    }

    public void askForPermission(Activity activity, String permission) {
        askForPermission(activity, new String[]{permission}, new PermissionCallback() {
            @Override
            public void permissionGranted() {

            }

            @Override
            public void permissionRefused() {

            }
        });
    }

    public void askForPermission(Activity activity, String permission, PermissionCallback permissionCallback) {
        askForPermission(activity, new String[]{permission}, permissionCallback);
    }

    public void askForPermission(Activity activity, String[] permissions, PermissionCallback permissionCallback) {
        if (permissionCallback == null) {
            return;
        }
        if (needPermission(activity, permissions)) {
            permissionCallback.permissionGranted();
            return;
        }
        PermissionRequest permissionRequest = new PermissionRequest(new ArrayList<String>(Arrays.asList(permissions)), permissionCallback);
        permissionRequests.add(permissionRequest);

        ActivityCompat.requestPermissions(activity,
                permissions,
                permissionRequest.getRequestCode());
    }

    public void askForPermission(Fragment fragment, String permission) {
        askForPermission(fragment.getActivity(), permission);
    }

    public void askForPermission(Fragment fragment, String permission, PermissionCallback permissionCallback) {
        askForPermission(fragment.getActivity(), new String[]{permission}, permissionCallback);
    }

    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionRequest requestResult = new PermissionRequest(requestCode);
        if (permissionRequests.contains(requestResult)) {
            PermissionRequest permissionRequest = permissionRequests.get(permissionRequests.indexOf(requestResult));
            if (verifyPermissions(grantResults)) {
                //Permission has been granted
                permissionRequest.getPermissionCallback().permissionGranted();
            } else {
                permissionRequest.getPermissionCallback().permissionRefused();
            }
            if (permissionListeners != null) {
                for (String permission : permissions) {
                    if (permissionListeners.containsKey(permission)) {
                        for (PermissionCallback permissionCallback : permissionListeners.get(permission)) {
                            if (verifyPermissions(grantResults)) {
                                //Permission has been granted
                                permissionCallback.permissionGranted();
                            } else {
                                permissionCallback.permissionRefused();
                            }
                        }
                    }
                }
            }
            permissionRequests.remove(requestResult);
            return true;
        }
        return false;
    }

    public void addlistenerForPermissionResult(String permissions, PermissionCallback permissionCallback) {
        ArrayList<PermissionCallback> listeners;
        if (permissionListeners == null)
            permissionListeners = new HashMap<>();
        if (permissionListeners.containsKey(permissions))
            listeners = permissionListeners.get(permissions);
        else
            listeners = new ArrayList<>();
        listeners.add(permissionCallback);
        permissionListeners.put(permissions, listeners);
    }

    /**
     * Check that all given permissions have been granted by verifying that each entry in the
     * given array is of the value {@link PackageManager#PERMISSION_GRANTED}.
     */
    public boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void askPermission(Activity activity, int requestCode, String permission) {
        if (needPermission(activity, permission))
            ActivityCompat.requestPermissions(activity,
                    new String[]{permission},
                    requestCode);
    }

    /*
 * If we override other methods, lets do it as well, and keep name same as it is already weird enough.
 * Returns true if we should show explanation why we need this permission.
 */
    public boolean shouldShowRequestPermissionRationale(Activity activity, String permissions) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions);
    }

    public boolean shouldShowRequestPermissionRationale(Fragment fragment, String permissions) {
        return fragment.shouldShowRequestPermissionRationale(permissions);
    }
}