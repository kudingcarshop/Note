package com.kuding.note.util;

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtil {


    private static  String TAG=PermissionUtil.class.getSimpleName();

    //Default result to {@link OnRequestPermissionsResultCallback#onRequestPermissionsResult(int, String[], int[])}.
    public static final int CHECK_REQUEST_PERMISSION_RESULT = 3;

    /*
     * Add PermissionLifecycleCallbacks on application.oncreate to check permissions.
     */
    public static Application.ActivityLifecycleCallbacks getActivityLifecycleCallbacks(String[] permissions){
        return new PermissionLifecycleCallbacks(permissions);
    }

    /*
     * @param activity The target activity.
     * @param permissions The requested permissions.
     * @param requestCode Application specific request code to match with a result
     *    reported to {@link OnRequestPermissionsResultCallback#onRequestPermissionsResult(
     *    int, String[], int[])}.
     */
    public static void checkAndRequestPermissions(final @NonNull Activity activity,
                                          final @NonNull String[] permissions, final int requestCode){
        List<String> requestList = new ArrayList<String>();
        for(String perm : permissions){
            if(PermissionChecker.checkSelfPermission(activity, perm) != PackageManager.PERMISSION_GRANTED){
                requestList.add(perm);
            }
        }

        if(requestList.size() > 0){
            ActivityCompat.requestPermissions(activity, requestList.toArray(new String[requestList.size()]), requestCode);
        }
    }


    /*
     * Check all permissions when resume the activity.
     */
    static class PermissionLifecycleCallbacks implements Application.ActivityLifecycleCallbacks{

        String[] permissions;

        public PermissionLifecycleCallbacks(@NonNull String[] permissions) {
            this.permissions = permissions;
        }



        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            checkAndRequestPermissions(activity, permissions, CHECK_REQUEST_PERMISSION_RESULT);
            Log.i(TAG,"onActivityCreated");

        }

        @Override
        public void onActivityStarted(Activity activity) {

            Log.i(TAG,"onActivityStarted");

        }

        @Override
        public void onActivityResumed(Activity activity) {

                Log.i(TAG, "onActivityResumed");


        }



        @Override
        public void onActivityPaused(Activity activity) {
            Log.i(TAG, "onActivityPaused");
        }

        @Override
        public void onActivityStopped(Activity activity) {
            Log.i(TAG, "onActivityStopped");
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            Log.i(TAG, "onActivitySaveInstanceState");
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Log.i(TAG, "onActivityDestroyed");
        }
    }

}


