
package com.example.towerwarbot;

import android.app.Activity;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import rikka.shizuku.Shizuku;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE = 1001;

    private final Shizuku.OnRequestPermissionResultListener
        permissionListener = (requestCode, grantResult) -> {
            if (requestCode == REQUEST_CODE) {
                showMessage(
                    grantResult == PackageManager.PERMISSION_GRANTED
                    ? "Shizuku povezan!"
                    : "Dozvola nije odobrena"
                );
            }
        };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Shizuku.addRequestPermissionResultListener(
            permissionListener
        );

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 80, 40, 40);

        Button connect = new Button(this);
        connect.setText("POVEZI SHIZUKU");
        layout.addView(connect);
        connect.setOnClickListener(v -> connectShizuku());

        Button test = new Button(this);
        test.setText("TEST VEZE");
        layout.addView(test);
        test.setOnClickListener(v -> {
            showMessage(
                hasShizukuPermission()
                ? "Shizuku veza je spremna"
                : "Shizuku nije povezan"
            );
        });

        Button tap = new Button(this);
        tap.setText("TEST AUTOMATSKOG DODIRA");
        layout.addView(tap);
        tap.setOnClickListener(v -> testTap());

        setContentView(layout);
    }

    private void testTap() {
        if (!hasShizukuPermission()) {
            showMessage("Prvo povezi Shizuku");
            return;
        }

        showMessage("Pokrecem test dodira");

        new Thread(() -> {
            try {
                Process process = Shizuku.newProcess(
                    new String[] {
                        "sh", "-c", "input tap 300 500"
                    },
                    null,
                    null
                );

                int result = process.waitFor();

                runOnUiThread(() -> showMessage(
                    result == 0
                    ? "Dodir izvrsen!"
                    : "Dodir nije uspio: " + result
                ));

                process.destroy();

            } catch (Exception e) {
                runOnUiThread(() ->
                    showMessage("Greska: " + e.getMessage())
                );
            }
        }).start();
    }

    private void connectShizuku() {
        try {
            if (!Shizuku.pingBinder()) {
                showMessage("Pokreni Shizuku");
                return;
            }

            if (hasShizukuPermission()) {
                showMessage("Shizuku je vec povezan");
                return;
            }

            Shizuku.requestPermission(REQUEST_CODE);

        } catch (Exception e) {
            showMessage("Greska: " + e.getMessage());
        }
    }

    private boolean hasShizukuPermission() {
        try {
            return Shizuku.pingBinder()
                && Shizuku.checkSelfPermission()
                == PackageManager.PERMISSION_GRANTED;
        } catch (Exception e) {
            return false;
        }
    }

    private void showMessage(String message) {
        Toast.makeText(
            this, message, Toast.LENGTH_LONG
        ).show();
    }

    @Override
    protected void onDestroy() {
        Shizuku.removeRequestPermissionResultListener(
            permissionListener
        );
        super.onDestroy();
    }
}
