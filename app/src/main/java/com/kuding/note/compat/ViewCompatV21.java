package com.kuding.note.compat;

import android.annotation.TargetApi;
import android.view.View;

/**
 * Created by user on 18-5-31.
 */

public class ViewCompatV21 extends ViewCompat {

    @Override
    @TargetApi(21)
    public void setTranslationZ(View view, float z) {
        if(view != null && z > 0){
            view.setTranslationZ(z);
        }
    }
}
