package com.firdian.catatanshift;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONObject;

/** Keeps the system alarm clock in sync with the reminder list the app computes. */
final class AlarmScheduler {
    static final String PREFS = "alarms";
    private static final String KEY_JSON = "json";
    private static final String KEY_COUNT = "count";
    private static final int MAX = 200;
    static final int TEST_CODE = 9999;

    private AlarmScheduler() {}

    static void save(Context ctx, String json) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_JSON, json).apply();
        apply(ctx);
    }

    static boolean canExact(Context ctx) {
        AlarmManager am = ctx.getSystemService(AlarmManager.class);
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S || am.canScheduleExactAlarms();
    }

    /** Cancels every alarm set earlier and schedules the saved list again. */
    static int apply(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        AlarmManager am = ctx.getSystemService(AlarmManager.class);
        int previous = sp.getInt(KEY_COUNT, 0);
        for (int i = 0; i < previous; i++) {
            am.cancel(pending(ctx, i, null, null));
        }
        int count = 0;
        long now = System.currentTimeMillis();
        try {
            JSONArray arr = new JSONArray(sp.getString(KEY_JSON, "[]"));
            for (int i = 0; i < arr.length() && count < MAX; i++) {
                JSONObject o = arr.getJSONObject(i);
                long t = o.getLong("t");
                if (t <= now) continue;
                set(ctx, am, t, pending(ctx, count, o.optString("title"), o.optString("body")));
                count++;
            }
        } catch (Exception ignored) {
        }
        sp.edit().putInt(KEY_COUNT, count).apply();
        return count;
    }

    static int scheduledCount(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(KEY_COUNT, 0);
    }

    static void test(Context ctx) {
        AlarmManager am = ctx.getSystemService(AlarmManager.class);
        Intent i = new Intent(ctx, AlarmReceiver.class)
                .putExtra("title", "Tes alarm Catatan Shift")
                .putExtra("body", "Alarm bekerja. Seperti inilah pengingat absen dan lembur Anda.");
        PendingIntent pi = PendingIntent.getBroadcast(ctx, TEST_CODE, i,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        set(ctx, am, System.currentTimeMillis() + 10_000, pi);
    }

    private static void set(Context ctx, AlarmManager am, long t, PendingIntent pi) {
        if (canExact(ctx)) {
            PendingIntent show = PendingIntent.getActivity(ctx, 0,
                    new Intent(ctx, MainActivity.class), PendingIntent.FLAG_IMMUTABLE);
            am.setAlarmClock(new AlarmManager.AlarmClockInfo(t, show), pi);
        } else {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, t, pi);
        }
    }

    private static PendingIntent pending(Context ctx, int code, String title, String body) {
        Intent i = new Intent(ctx, AlarmReceiver.class);
        if (title != null) i.putExtra("title", title);
        if (body != null) i.putExtra("body", body);
        return PendingIntent.getBroadcast(ctx, code, i,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
