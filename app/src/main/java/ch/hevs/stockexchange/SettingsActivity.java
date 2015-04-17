package ch.hevs.stockexchange;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import java.util.Locale;


@SuppressWarnings("ALL")
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private Boolean language_Changed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        language_Changed = getIntent().getBooleanExtra("language_changed", false);
        addPreferencesFromResource(R.xml.settings);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onBackPressed() {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("inapp_navigation", language_Changed);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            startActivity(i);
    }

    @Override
    /**
     * This listener creates toasts as soon as the user has changed the settings. It will also
     * restart the activity if the user has changed the language.
     */
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Resources res = getResources();
        if(key.equals("current_language")) {
            // Get the current language from shared preferences
            String lang = sharedPreferences.getString("current_language", "");

            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, null);
            String[] languages = res.getStringArray(R.array.array_languages);

            if(lang.equals("EN")) {
                Toast.makeText(this, String.format(res.getString(R.string.toast_language_change),languages[1]), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, String.format(res.getString(R.string.toast_language_change),languages[0]), Toast.LENGTH_SHORT).show();
            }
            Intent i = new Intent(this, SettingsActivity.class);
            i.putExtra("language_changed", true);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            startActivity(i);
        } else if(key.equals("current_currency")) {
            // Get the current currency from shared preferences
            String curr = sharedPreferences.getString("current_currency", "");
            String[] currency = res.getStringArray(R.array.array_currency);
            switch (curr) {
                case "1":
                    Toast.makeText(this,String.format(res.getString(R.string.toast_currency_change),currency[0]),Toast.LENGTH_SHORT).show();
                    break;
                case "2":
                    Toast.makeText(this,String.format(res.getString(R.string.toast_currency_change),currency[1]),Toast.LENGTH_SHORT).show();
                    break;
                case "3":
                    Toast.makeText(this,String.format(res.getString(R.string.toast_currency_change),currency[2]),Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
