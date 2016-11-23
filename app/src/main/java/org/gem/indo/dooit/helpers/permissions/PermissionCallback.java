package org.gem.indo.dooit.helpers.permissions;

/**
 * Created by Bernhard Müller on 10/14/2016.
 */
public interface PermissionCallback {
    public void permissionGranted();

    public void permissionRefused();
}