/* Copyright (C) 2016 Tcl Corporation Limited */
package com.kuding.note.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if ("android.intent.action.BOOT_COMPLETED".equals(action)) {
            Intent resetAlarms = new Intent(context, ResetAlarmsService.class);
            context.startService(resetAlarms);
        }
    }
}
