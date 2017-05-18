package android.dichung;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

import Modules.Converter;
import model.Student;
import model.StudentLatLng;

public class InfoActivity extends AppCompatActivity {

    public final int SIGN_IN_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLanguage();

        //Check network connection
        if (!isOnline()) {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.error))
                    .setMessage(getResources().getString(R.string.internet_problem))
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .create()
                    .show();
            return;
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setTheme(R.style.AppTheme)
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );

        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            Toast.makeText(this, getResources().getString(R.string.welcome) + " " + FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getDisplayName(),
                    Toast.LENGTH_SHORT)
                    .show();
            startMain();
            finish();
            return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this,
                        getResources().getString(R.string.successfully_signed_in),
                        Toast.LENGTH_SHORT)
                        .show();

                startMain();
                finish();
            } else {
                // Close the app
                finish();
            }
        }

    }

    public void startMain() {
        Student student = new Student(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()
                , new StudentLatLng(0, 0)
                , FirebaseAuth.getInstance().getCurrentUser().getEmail()
                , "<Không có>"
                , "<Không có>"
                , "<Không có>"
                , "<Không có>"
                , "<Không có>", false);
        String code = Converter.drawableToString(getResources().getDrawable(R.drawable.unknown_person_icon));
        student.setImageCode(code);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("studentObj", student);
        startActivity(intent);
        finish();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void setLanguage() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("language_setting", Context.MODE_PRIVATE);

        if (sharedPreferences != null) {
            String languageToLoad = sharedPreferences.getString("language", "en");
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }
    }

}
