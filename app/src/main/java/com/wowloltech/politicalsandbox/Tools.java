package com.wowloltech.politicalsandbox;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Tools {
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    public static boolean isSaved = true;
    public static DatabaseHelper dbHelper;
    public static MainFragment mainFragment;

    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    public static View getViewByTag(ViewGroup root, String tag) {
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                return getViewByTag((ViewGroup) child, tag);
            }

            final Object tagObj = child.getTag();
            if (tagObj != null && tagObj.equals(tag)) {
                return child;
            }

        }
        return null;
    }

    public static ArrayList<Player> createPrefilledList(int size) {
        ArrayList<Player> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(null);
        }
        return list;
    }
}
