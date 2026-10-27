
package com.example.towerwarbot;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.view.accessibility.AccessibilityEvent;

public class GameAccessibilityService extends AccessibilityService {

    private static GameAccessibilityService instance;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
    }

    public static boolean testTap() {
        if (instance == null) return false;

        Path path = new Path();
        path.moveTo(300, 500);

        GestureDescription.StrokeDescription stroke =
            new GestureDescription.StrokeDescription(
                path, 0, 100
            );

        GestureDescription.Builder builder =
            new GestureDescription.Builder();
        builder.addStroke(stroke);

        return instance.dispatchGesture(
            builder.build(), null, null
        );
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    public void onDestroy() {
        instance = null;
        super.onDestroy();
    }
}
