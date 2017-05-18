package android.dichung;


import android.content.Intent;
import android.dichung.adapter.SettingListAdapter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Vector;

import Modules.IOData;
import Modules.IODataListener;
import model.SettingItem;

public class SettingActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Vector<SettingItem> items = getListData();
        listView = (ListView) findViewById(R.id.listViewSetting);
        listView.setAdapter(new SettingListAdapter(this, items));

        // Khi người dùng click vào các ListItem
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = listView.getItemAtPosition(position);
                SettingItem item = (SettingItem) o;
                String choice = item.getItemName();

                if(choice.equals(getResources().getString(R.string.tips))){
                    readTips();
                    return;
                }
                if(choice.equals(getResources().getString(R.string.about))){
                    readAbout();
                    return;
                }
                if(choice.equals(getResources().getString(R.string.log_out))){
                    logOut();
                    return;
                }
                if(choice.equals(getResources().getString(R.string.account_information))){
                    editProfile();
                    return;
                }
                if(choice.equals(getResources().getString(R.string.language))){
                    changeLanguage();
                    return;
                }
                if(choice.equals(getResources().getString(R.string.quit))){
                    quit();
                    return;
                }

            }
        });

    }

    private void changeLanguage() {
        startActivity(new Intent(this, LanguageActivity.class));
    }

    private void readTips() {
        startActivity(new Intent(this, TipsActivity.class));
    }

    private void readAbout() {
        startActivity(new Intent(this, AboutActivity.class));
    }

    public void editProfile(){
        startActivity(new Intent(this, ProfileActivity.class));
    }

    public void quit() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public void logOut() {
        IODataListener listener = new IOData();
        Toast.makeText(SettingActivity.this, getResources().getString(R.string.bye) + listener.getName(), Toast.LENGTH_LONG).show();
        listener.updateOfflineByUid();
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private Vector<SettingItem> getListData() {
        Vector<SettingItem> list = new Vector<>();
        SettingItem tipItem = new SettingItem(getResources().getString(R.string.tips), "tips_icon");
        SettingItem aboutItem = new SettingItem(getResources().getString(R.string.about), "about_icon");
        SettingItem logOutItem = new SettingItem(getResources().getString(R.string.log_out), "log_out_icon");
        SettingItem ProfileItem = new SettingItem(getResources().getString(R.string.account_information), "account_information_icon");
        SettingItem languageItem = new SettingItem(getResources().getString(R.string.language), "language_icon");
        SettingItem quitItem = new SettingItem(getResources().getString(R.string.quit), "quit_icon");

        list.add(tipItem);
        list.add(aboutItem);
        list.add(logOutItem);
        list.add(ProfileItem);
        list.add(languageItem);
        list.add(quitItem);

        return list;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}


