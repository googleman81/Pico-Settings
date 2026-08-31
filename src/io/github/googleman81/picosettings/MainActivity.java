package io.github.googleman81.picosettings;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A deliberately small, offline bridge to Android settings panels hidden by
 * the PICO shell. It requests no Android permissions and stores no data.
 */
public final class MainActivity extends Activity {
    private static final String ACTION_PRIVATE_DNS_SETTINGS =
            "android.settings.PRIVATE_DNS_SETTINGS";

    private LinearLayout content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(createInterface());
    }

    private View createInterface() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.rgb(13, 15, 18));

        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        int outerPadding = dp(28);
        content.setPadding(outerPadding, dp(24), outerPadding, dp(28));
        scroll.addView(content, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT));

        TextView title = new TextView(this);
        title.setText(R.string.app_name);
        title.setTextColor(Color.WHITE);
        title.setTextSize(25);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        content.addView(title, matchWidth());

        TextView subtitle = new TextView(this);
        subtitle.setText(R.string.subtitle);
        subtitle.setTextColor(Color.rgb(174, 181, 190));
        subtitle.setTextSize(15);
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subtitleParams = matchWidth();
        subtitleParams.setMargins(0, dp(6), 0, dp(20));
        content.addView(subtitle, subtitleParams);

        addSettingsButton(R.string.full_settings, Settings.ACTION_SETTINGS, false);
        addSettingsButton(R.string.private_dns, ACTION_PRIVATE_DNS_SETTINGS, true);
        addSettingsButton(R.string.wifi, Settings.ACTION_WIFI_SETTINGS, true);
        addSettingsButton(R.string.vpn, Settings.ACTION_VPN_SETTINGS, true);
        addSettingsButton(R.string.apps, Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS, true);
        addSettingsButton(R.string.developer_options,
                Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS, true);
        addSettingsButton(R.string.display, Settings.ACTION_DISPLAY_SETTINGS, true);
        addSettingsButton(R.string.device_info, Settings.ACTION_DEVICE_INFO_SETTINGS, true);

        TextView footer = new TextView(this);
        footer.setText(R.string.footer);
        footer.setTextColor(Color.rgb(128, 136, 146));
        footer.setTextSize(12);
        footer.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams footerParams = matchWidth();
        footerParams.setMargins(0, dp(18), 0, 0);
        content.addView(footer, footerParams);

        return scroll;
    }

    private void addSettingsButton(int label, String action, boolean fallBack) {
        Button button = new Button(this);
        button.setText(label);
        button.setTextSize(16);
        button.setAllCaps(false);
        button.setFocusable(true);
        button.setMinHeight(dp(54));
        button.setOnClickListener(view -> openSettings(action, fallBack));

        LinearLayout.LayoutParams params = matchWidth();
        params.setMargins(0, dp(5), 0, dp(5));
        content.addView(button, params);
    }

    private void openSettings(String action, boolean fallBack) {
        try {
            startActivity(new Intent(action));
        } catch (ActivityNotFoundException | SecurityException exception) {
            if (fallBack) {
                Toast.makeText(this, R.string.panel_unavailable, Toast.LENGTH_SHORT).show();
                try {
                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                } catch (ActivityNotFoundException | SecurityException ignored) {
                    Toast.makeText(this, R.string.settings_unavailable, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, R.string.settings_unavailable, Toast.LENGTH_LONG).show();
            }
        }
    }

    private LinearLayout.LayoutParams matchWidth() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
