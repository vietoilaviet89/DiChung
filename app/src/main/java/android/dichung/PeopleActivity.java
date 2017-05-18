package android.dichung;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.dichung.adapter.InfoListAdapter;
import android.dichung.adapter.RVAdapter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.Vector;

import Modules.Converter;
import Modules.IOData;
import Modules.IODataListener;
import Modules.Permission;
import model.InformationItem;
import model.Student;

public class PeopleActivity extends AppCompatActivity implements RVAdapter.OnItemClickRVrecycleViewListener {

    private RecyclerView rv;
    private RVAdapter rvAdapterForGrid;
    private RVAdapter rvAdapterForLinear;
    private RVAdapter rvCurrent;

    private FloatingActionButton fb;

    private GridLayoutManager glm;
    private LinearLayoutManager llm;
    private RecyclerView.LayoutManager lmCurrent;

    private ImageView iv;
    private IODataListener listener;
    private Vector<Student> students;
    private PopupWindow popupWindow = new PopupWindow();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);
        init();
        listener = new IOData();
        listener.retrieveDatabase(this);
    }

    public void init() {
        fb = (FloatingActionButton) findViewById(R.id.fbChange);
        fb.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = Toast.makeText(PeopleActivity.this, getResources().getString(R.string.type_of_view), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM|Gravity.RIGHT, getWidth()/7, getHeight()/15);
                toast.show();
                return false;
            }
        });


        addEvents();
    }

    private void addEvents() {
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lmCurrent.equals(glm)) {
                    fb.setImageResource(R.drawable.grid_icon);
                    rvCurrent = rvAdapterForLinear;
                    lmCurrent = llm;
                } else {
                    fb.setImageResource(R.drawable.list_icon);
                    rvCurrent = rvAdapterForGrid;
                    lmCurrent = glm;
                }
                rv.setAdapter(rvCurrent);
                rv.setLayoutManager(lmCurrent);
            }
        });
    }

    public void addControls(Vector<Student> _students) {
        students = new Vector<>();
        for (Student student : _students) {
            if (student.isOnline() && !student.getEmail().equals((listener.getEmail()))) {
                students.add(student);
            }
        }

        rvAdapterForGrid = new RVAdapter(students, this, R.layout.square_card_view, this);
        rvAdapterForLinear = new RVAdapter(students, this, R.layout.rect_card_view, this);
        rvCurrent = rvAdapterForLinear;

        rv = (RecyclerView) findViewById(R.id.rvPeople);
        rv.setHasFixedSize(true);

        glm = new GridLayoutManager(this, 3);
        llm = new LinearLayoutManager(this);
        lmCurrent = llm;
        rv.setLayoutManager(lmCurrent);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(rvCurrent);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onItemClick(View itemview, int position) {
        popupWindow.dismiss();
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.activity_detail, (ViewGroup) findViewById(R.id.popup));
        ImageView imageView = (ImageView) layout.findViewById(R.id.profileView);
        ListView listView = (ListView) layout.findViewById(R.id.list_info);
        ImageButton button = (ImageButton) layout.findViewById(R.id.btnFindPath);
        button.setVisibility(View.INVISIBLE);
        ImageButton callButton = (ImageButton) layout.findViewById(R.id.btnCall);
        callButton.setImageDrawable(getDrawable(R.drawable.call_icon));
        ImageButton sendButton = (ImageButton) layout.findViewById(R.id.btnSend);
        sendButton.setImageDrawable(getResources().getDrawable(R.drawable.send_message_icon));

        Vector<InformationItem> items;
        final Student s = students.get(position);
        items = getListData(s);
        listView.setAdapter(new InfoListAdapter(this, items));

        Bitmap bmp = Converter.stringToBitmap(s.getImageCode());
        imageView.setImageBitmap(bmp);

        popupWindow = new PopupWindow(layout
                , Resources.getSystem().getDisplayMetrics().widthPixels
                , Resources.getSystem().getDisplayMetrics().heightPixels * 1/3, true);
        popupWindow.setElevation(24);
        popupWindow.setAnimationStyle(R.style.dialog_animation);
        popupWindow.showAtLocation(layout, Gravity.BOTTOM, 0, 0);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(s.getPhoneNumber().equals("<Không có>")){
                    Toast.makeText(PeopleActivity.this, getResources().getString(R.string.no_phone), Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", s.getPhoneNumber());
                startActivity(smsIntent);
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(PeopleActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    //request permission from user if the app hasn't got the required permission
                    ActivityCompat.requestPermissions(PeopleActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},   //request specific permission from user
                            10);
                    Permission.checkCallPermission(PeopleActivity.this);
                    return;
                }
                if(s.getPhoneNumber().equals("<Không có>")){
                    Toast.makeText(PeopleActivity.this, getResources().getString(R.string.no_phone), Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent callIntent = new Intent(Intent.ACTION_CALL); //use ACTION_CALL class
                callIntent.setData(Uri.parse("tel:" + s.getPhoneNumber()));    //this is the phone number calling
                startActivity(callIntent);  //call activity and make phone call
            }
        });

        callButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = Toast.makeText(PeopleActivity.this, getResources().getString(R.string.call), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.LEFT, getWidth() / 2, getHeight() / 3);
                toast.show();
                return false;
            }
        });

        sendButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = Toast.makeText(PeopleActivity.this, getResources().getString(R.string.send_sms), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.LEFT, getWidth() / 7, getHeight() / 3);
                toast.show();
                return false;
            }
        });
    }

    private Vector<InformationItem> getListData(Student student) {
        Vector<InformationItem> list = new Vector<>();
        InformationItem nameItem = new InformationItem(getResources().getString(R.string.name), student.getName());

        InformationItem dobItem = new InformationItem(getResources().getString(R.string.birthday), student.getDateOfBirth());

        InformationItem genderItem = new InformationItem(getResources().getString(R.string.gender), student.getGender());

        InformationItem majorItem = new InformationItem(getResources().getString(R.string.major), student.getMajor());

        InformationItem phoneNumberItem = new InformationItem(getResources().getString(R.string.phone), student.getPhoneNumber());

        InformationItem desItem = new InformationItem(getResources().getString(R.string.status), student.getDescription());

        list.add(nameItem);
        list.add(dobItem);
        list.add(genderItem);
        list.add(majorItem);
        list.add(phoneNumberItem);
        list.add(desItem);

        return list;
    }

    public int getHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        //int heightPix = (int) Math.ceil(dm.heightPixels * (dm.densityDpi / 160.0));
        return height;
    }

    public int getWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        //int widthtPix = (int) Math.ceil(dm.widthPixels * (dm.densityDpi / 160.0));
        return width;
    }
}
