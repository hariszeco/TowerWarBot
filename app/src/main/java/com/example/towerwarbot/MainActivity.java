
package com.example.towerwarbot;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 80, 40, 40);

        Button settings = new Button(this);
        settings.setText("Omoguci pristup");
        layout.addView(settings);

        settings.setOnClickListener(v ->
            startActivity(new Intent(
                Settings.ACTION_ACCESSIBILITY_SETTINGS
            ))
        );

        Button test = new Button(this);
        test.setText("TEST DODIRA");
        layout.addView(test);

        test.setOnClickListener(v -> {
            boolean started =
                GameAccessibilityService.testTap();

            Toast.makeText(this,
                started ? "Dodir poslan" :
                          "Ukljuci Accessibility servis",
                Toast.LENGTH_LONG).show();
        });

        setContentView(layout);
    }
}
