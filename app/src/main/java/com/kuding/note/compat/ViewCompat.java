package com.kuding.note.compat;

import android.os.Build;
import android.view.View;

/**
 * Created by user on 18-5-31.
 */

public abstract class ViewCompat {

    private static ViewCompat mViewCompat;

    public abstract void setTranslationZ(View view, float z);

    public static ViewCompat getInstance(){
        if(mViewCompat == null){
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH){
                mViewCompat = new ViewCompatV21();
            }
        }

        return mViewCompat;
    }
}
