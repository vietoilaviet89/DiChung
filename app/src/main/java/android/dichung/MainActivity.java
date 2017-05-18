package android.dichung;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import model.Student;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {

    private TabHost tabHost;
    private Student student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        student = getInputData("studentObj");
        init();
    }

    public Student getInputData(String objName){
        Intent intent = getIntent();
        Student _student = (Student) intent.getSerializableExtra(objName);
        return _student;
    }


    public void init(){
        //Assign id to Tabhost.
        tabHost = (TabHost)findViewById(android.R.id.tabhost);

        //Creating tab menu.
        TabHost.TabSpec TabMenu1 = tabHost.newTabSpec("First tab");
        TabHost.TabSpec TabMenu2 = tabHost.newTabSpec("Second tab");
        TabHost.TabSpec TabMenu3 = tabHost.newTabSpec("Third tab");

        //Setting up tab 1 name.
        TabMenu1.setIndicator(getResources().getString(R.string.map));
        //Set tab 1 activity to tab 1 menu.
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("studentObj", student);
        TabMenu1.setContent(intent);


        //Setting up tab 2 name.
        TabMenu2.setIndicator(getResources().getString(R.string.people));
        intent = new Intent(this, PeopleActivity.class);
        TabMenu2.setContent(intent);

        //Setting up tab 2 name.
        TabMenu3.setIndicator(getResources().getString(R.string.setting));
        intent = new Intent(this, SettingActivity.class);
        TabMenu3.setContent(intent);

        //
        tabHost.addTab(TabMenu1);
        tabHost.addTab(TabMenu2);
        tabHost.addTab(TabMenu3);
    }
}
