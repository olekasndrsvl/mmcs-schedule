package com.alexanderl.mmcs_schedule;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String PREFS_NAME = "SchedulePrefs";
    private static final String KEY_GROUP_ID = "selected_group_id";
    private static final String KEY_GROUP_NAME = "selected_group_name";
    private static final String KEY_GROUP_SELECTED = "group_selected";

    private SharedPreferences prefs;

    public PreferencesManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveSelectedGroup(int groupId, String groupName) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_GROUP_ID, groupId);
        editor.putString(KEY_GROUP_NAME, groupName);
        editor.putBoolean(KEY_GROUP_SELECTED, true);
        editor.apply();
    }

    public int getSelectedGroupId() {
        return prefs.getInt(KEY_GROUP_ID, 1);
    }

    public String getSelectedGroupName() {
        return prefs.getString(KEY_GROUP_NAME, "<group>");
    }

    public boolean isGroupSelected() {
        return prefs.getBoolean(KEY_GROUP_SELECTED, false);
    }

    public void clearSelectedGroup() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_GROUP_ID);
        editor.remove(KEY_GROUP_NAME);
        editor.putBoolean(KEY_GROUP_SELECTED, false);
        editor.apply();
    }
}