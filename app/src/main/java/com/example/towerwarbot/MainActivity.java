
package com.example.towerwarbot;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import rikka.shizuku.Shizuku;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE = 1001;

    private IBinder tapService;
    private boolean binding = false;

    private final Shizuku.OnRequestPermissionResultListener
            permissionListener = (requestCode, result) -> {
        if (requestCode == REQUEST_CODE) {
            showMessage(
                result == PackageManager.PERMISSION_GRANTED
                ? "Shizuku povezan!"
                : "Dozvola nije odobrena"
            );
        }
    };

    private final Shizuku.UserServiceArgs serviceArgs =
        new Shizuku.UserServiceArgs(
            new ComponentName(
                "com.example.towerwarbot",
                TapService.class.getName()
            )
        )
        .daemon(false)
        .processNameSuffix("tap_service")
        .debuggable(false)
        .version(1);

    private final ServiceConnection serviceConnection =
        new ServiceConnection() {

            @Override
            public void onServiceConnected(
                    ComponentName name,
                    IBinder binder) {

                tapService = binder;
                binding = false;
                showMessage("Servis za dodir povezan!");
            }

            @Override
            public void onServiceDisconnected(
                    ComponentName name) {

                tapService = null;
                binding = false;
                showMessage("Servis za dodir prekinut");
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
        test.setOnClickListener(v -> showMessage(
            hasShizukuPermission()
            ? "Shizuku veza je spremna"
            : "Shizuku nije povezan"
        ));

        Button service = new Button(this);
        service.setText("POVEZI SERVIS ZA DODIR");
        layout.addView(service);
        service.setOnClickListener(v -> bindTapService());

        Button tap = new Button(this);
        tap.setText("TEST AUTOMATSKOG DODIRA");
        layout.addView(tap);
        tap.setOnClickListener(v -> testTap());

        setContentView(layout);
    }

    private void bindTapService() {
        if (!hasShizukuPermission()) {
            showMessage("Prvo povezi Shizuku");
            return;
        }

        if (tapService != null && tapService.isBinderAlive()) {
            showMessage("Servis je vec povezan");
            return;
        }

        if (binding) {
            showMessage("Povezivanje je u toku");
            return;
        }

        try {
            binding = true;
            Shizuku.bindUserService(
                serviceArgs,
                serviceConnection
            );
            showMessage("Povezujem servis...");
        } catch (Exception e) {
            binding = false;
            showMessage("Greska: " + e.getMessage());
        }
    }

    private void testTap() {
        if (tapService == null ||
                !tapService.isBinderAlive()) {
            showMessage("Prvo povezi servis za dodir");
            return;
        }

        new Thread(() -> {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();

            try {
                data.writeInt(300);
                data.writeInt(500);

                boolean success = tapService.transact(
                    TapService.TAP,
                    data,
                    reply,
                    0
                );

                if (!success) {
                    throw new Exception(
                        "Servis nije prihvatio komandu"
                    );
                }

                reply.readException();
                int result = reply.readInt();

                runOnUiThread(() -> showMessage(
                    result == 0
                    ? "Dodir izvrsen!"
                    : "Dodir nije uspio: " + result
                ));

            } catch (Exception e) {
                runOnUiThread(() -> showMessage(
                    "Greska: " + e.getMessage()
                ));
            } finally {
                reply.recycle();
                data.recycle();
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
            this,
            message,
            Toast.LENGTH_LONG
        ).show();
    }

    @Override
    protected void onDestroy() {
        Shizuku.removeRequestPermissionResultListener(
            permissionListener
        );

        if (tapService != null || binding) {
            try {
                Shizuku.unbindUserService(
                    serviceArgs,
                    serviceConnection,
                    false
                );
            } catch (Exception ignored) {
            }
        }

        super.onDestroy();
    }
}
