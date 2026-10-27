
package com.example.towerwarbot;

import android.os.IBinder;
import android.system.Os;

public class TapService extends android.os.Binder {

    public TapService() {
    }

    public void tap(int x, int y) throws Exception {
        Process process = new ProcessBuilder(
            "sh", "-c", "input tap " + x + " " + y
        ).start();

        int result = process.waitFor();

        if (result != 0) {
            throw new Exception("Dodir nije uspio: " + result);
        }
    }

    public void destroy() {
    }
}
