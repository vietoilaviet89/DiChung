package android.dichung;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.Locale;

public class LanguageActivity extends AppCompatActivity {

    private CheckBox vi;
    private CheckBox en;
    private CheckBox la;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        init();
    }

    private void init() {
        vi = (CheckBox) findViewById(R.id.vietnamese_box);
        en = (CheckBox) findViewById(R.id.english_box);
        la = (CheckBox) findViewById(R.id.laos_box);
        setLanguage();
    }

    public void doBack(View view){
        finish();
    }

    public void doChange(View view){
        if(vi.isChecked()){
            String languageToLoad = "vi"; // your language
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

            //save language
            SharedPreferences sharedPreferences= this.getSharedPreferences("language_setting", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("language", languageToLoad);
            editor.apply();

            //reset
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage( getBaseContext().getPackageName() );
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
        else if(en.isChecked()){
            String languageToLoad = "en"; // your language
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());

            //save language
            SharedPreferences sharedPreferences= this.getSharedPreferences("language_setting", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("language", languageToLoad);
            editor.apply();

            //reset
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage( getBaseContext().getPackageName() );
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
        else if(la.isChecked()){
            Toast.makeText(this, getResources().getString(R.string.not_supported), Toast.LENGTH_SHORT).show();
            return;
        }

        else {
            Toast.makeText(this, getResources().getString(R.string.change_problem), Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void checkVi(View view){
        if(vi.isChecked()){
            en.setChecked(false);
            la.setChecked(false);
        }
    }

    public void checkEn(View view){
        if(en.isChecked()) {
            vi.setChecked(false);
            la.setChecked(false);
        }
    }

    public void checkLa(View view){
        if(la.isChecked()) {
            vi.setChecked(false);
            en.setChecked(false);
        }
    }

    public void setLanguage() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("language_setting", Context.MODE_PRIVATE);

        if (sharedPreferences != null) {
            String languageToLoad = sharedPreferences.getString("language", "en");
            if(languageToLoad.equals("en")){
                en.setChecked(true);
            }
            if(languageToLoad.equals("vi")){
                vi.setChecked(true);
            }
        }
    }
}
