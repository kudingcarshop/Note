/* Copyright (C) 2016 Tcl Corporation Limited */
package com.kuding.note.util;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;

import com.kuding.note.data.Note;

import java.util.ArrayList;

/**
 * reset alarms of reminders after reboot
 */
public class ResetAlarmsService extends IntentService {

    private static final String TAG = "ResetAlarmsService";
    private static final String[] PROJECTION = new String[] {
            Note.Reminder.COLUMN_NOTE_ID,
            Note.Reminder.COLUMN_TIME
    };

    private class ReminderEntry {
        private long mNoteId;
        private long mAlarmTime;

        private void setNoteId(long noteId) {
            mNoteId = noteId;
        }
        private void setAlarmTime(long alarmTime) {
            mAlarmTime = alarmTime;
        }
    }

    public ResetAlarmsService() {
        super("ResetAlarmsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        ArrayList<ReminderEntry> reminders = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = getContentResolver().query(Note.Reminder.CONTENT_URI, PROJECTION,
                        null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    ReminderEntry reminder = new ReminderEntry();
                    reminder.setNoteId(cursor.getLong(0));
                    reminder.setAlarmTime(cursor.getLong(1));
                    if (reminder.mAlarmTime > System.currentTimeMillis()) {
                        reminders.add(reminder);
                    }
                    KudingLog.d(TAG,"reboot: noteId = " + reminder.mNoteId + ", alarmTime = "
                                + reminder.mAlarmTime);

                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            KudingLog.e(TAG, "occur exception in onHandleIntent");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        for (ReminderEntry reminder : reminders) {
            Intent i = new Intent(this, ReminderReceiver.class);
            i.setData(ContentUris.withAppendedId(Note.CONTENT_URI, reminder.mNoteId));
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            PendingIntent p = PendingIntent.getBroadcast(this, 0, i, 0);
            am.set(AlarmManager.RTC_WAKEUP, reminder.mAlarmTime, p);
        }
    }
}
