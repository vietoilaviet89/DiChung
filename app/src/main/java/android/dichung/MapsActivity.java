package android.dichung;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.dichung.adapter.InfoListAdapter;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Vector;

import Modules.Converter;
import Modules.DirectionFinder;
import Modules.DirectionFinderListener;
import Modules.IOData;
import Modules.IODataListener;
import Modules.Permission;
import model.InformationItem;
import model.MarkerStudent;
import model.Route;
import model.Student;
import model.StudentLatLng;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    public final String TAG = "MapsActivity";

    private static GoogleMap mMap;
    private Vector<Route> comparisons = new Vector<Route>();
    private TextView duration;
    private TextView distance;
    FloatingActionButton refreshButton;
    FloatingActionButton mapTypeButton;

    // Mã yêu cầu uhỏi người dùng cho phép xem vị trí hiện tại của họ (***).
    // Giá trị mã 8bit (value < 256).
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    //Vị trí Học viện
    public static final String ptit_position = "20.980642, 105.787943";
    protected DirectionFinderListener listener;
    protected IODataListener ioDataListener;
    //Vị trí hiện tại
    protected Location myLocation;
    protected String myPosition;
    protected String friend_position;
    //List students nhận từ Server
    //List students nằm trong khoảng circleArea
    protected Vector<MarkerStudent> nearbyStudents;
    private SupportMapFragment mapFragment;
    //Bán kính cho phép (km)
    public final double circleArea = 3;
    protected Student me;
    private ProgressDialog progressDialog;
    private ProgressDialog progressDialog2;
    private PopupWindow popupWindow = new PopupWindow();
    protected String temp;
    protected long mLastClickTime = 0;
    protected int countstyle = 0;
    protected Marker currentMarker;
    protected Vector<Marker> nearbyMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        init();
        try {
            locationUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            //permission
        }
        ioDataListener = new IOData();
        ioDataListener.retrieveDatabase(this);
        ioDataListener.retrieveDataAlreadySigned(this, ioDataListener.getUid());
    }

    @SuppressLint("NewApi")
    public void init() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        me = getInputData("studentObj");
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        duration = (TextView) findViewById(R.id.tvDuration);
        distance = (TextView) findViewById(R.id.tvDistance);
        mapTypeButton = (FloatingActionButton) findViewById(R.id.mapType);
        refreshButton = (FloatingActionButton) findViewById(R.id.refresh);
        refreshButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                toastForRefresh(getResources().getString(R.string.refresh), 0);
                return false;
            }
        });
    }

    public void setMapType(View view){
        if(mMap.getMapType() == GoogleMap.MAP_TYPE_TERRAIN){
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mapTypeButton.setImageDrawable(getDrawable(R.drawable.terrain_icon));
        }
        else{
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            mapTypeButton.setImageDrawable(getDrawable(R.drawable.satelite_icon));
        }
    }


    //Lấy dữ liệu người dùng đã khởi tạo
    public Student getInputData(String objName) {
        Intent intent = getIntent();
        Student _student = (Student) intent.getSerializableExtra(objName);
        return _student;
    }

    //Khởi tạo lại Map
    public void onRefresh(View view) {
        //refresh.requestFocus();
        //this to prevent clicking for times in a second
        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        onRefreshDetail();
    }

    public void onRefreshDetail() {

        countstyle++;
        mMap.clear();
        listener = null;
        comparisons = new Vector<Route>();
        distance.setText(getResources().getString(R.string.km_));
        duration.setText(getResources().getString(R.string.min));
        onMapReady(mMap);
    }

    public void findDirection(String friend_position) {
        try {
            this.friend_position = friend_position;
            myPosition = myLocation.getLatitude() + ", " + myLocation.getLongitude();

            if (comparisons.size() < 4) {
                progressDialog = ProgressDialog.show(this, getResources().getString(R.string.wait), getResources().getString(R.string.loading1_2), false);

            }
            //0
            listener = new DirectionFinder(this, mMap, myPosition, ptit_position);
            listener.execute(false, true);

            //1
            listener = new DirectionFinder(this, mMap, friend_position, ptit_position);
            listener.execute(false, true);

            //2
            listener = new DirectionFinder(this, mMap, myPosition, friend_position);
            listener.execute(false, true);

            //3
            listener = new DirectionFinder(this, mMap, friend_position, myPosition);
            listener.execute(false, true);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    //Phân tích đường đi nào ngắn nhất và hiển thị
    public void analysisSolution(Route route) throws UnsupportedEncodingException {
        comparisons.add(route);

        if (comparisons.size() == 4) {
            progressDialog.dismiss();
            if (comparisons.get(2).distance.value + comparisons.get(1).distance.value <= comparisons.get(3).distance.value + comparisons.get(0).distance.value) {

                listener = new DirectionFinder(this, mMap, myPosition, friend_position);
                listener.onDirectionFinderStart(getResources().getString(R.string.loading2_2));
                listener.execute(true, false);

                listener = new DirectionFinder(this, mMap, friend_position, ptit_position);
                listener.onDirectionFinderStart(getResources().getString(R.string.loading2_2));
                listener.execute(true, false);

                distance.setText(Math.round(((float) (comparisons.get(2).distance.value + comparisons.get(1).distance.value) / 1000) * 100.0) / 100.0 + " " + getResources().getString(R.string.km));
                duration.setText(Math.round(((float) (comparisons.get(2).duration.value + comparisons.get(1).duration.value) / 60) * 10.0) / 10.0 + " " + getResources().getString(R.string.mins));
            } else {
                listener = new DirectionFinder(this, mMap, friend_position, myPosition);
                listener.onDirectionFinderStart(getResources().getString(R.string.loading2_2));
                listener.execute(true, false);

                listener = new DirectionFinder(this, mMap, myPosition, ptit_position);
                listener.onDirectionFinderStart(getResources().getString(R.string.loading2_2));
                listener.execute(true, false);

                distance.setText(Math.round(((float) (comparisons.get(3).distance.value + comparisons.get(0).distance.value) / 1000) * 100.0) / 100.0 + " " + getResources().getString(R.string.km));
                duration.setText(Math.round(((float) (comparisons.get(3).duration.value + comparisons.get(0).duration.value) / 60) * 10.0) / 10.0 + " " + getResources().getString(R.string.mins));

            }
        }
    }

    //Vẽ đường đi và hiển thị marker điểm đầu điểm cuối
    public void draw(DirectionFinder listener) {
        listener.onDirectionFinderSuccess();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                myLocation = location;
            }
        });
        //mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        // Add a marker in ptit and move the camera
        LatLng ptitLatlng = new LatLng(20.980642, 105.787943);
        String code = Converter.drawableToString(getResources().getDrawable(R.drawable.ptit_icon));
        Bitmap ptitBmp = Bitmap.createScaledBitmap(Converter.stringToBitmap(code), 100, 100, true);
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(ptitBmp))
                .position(ptitLatlng)
                .title("Học Viện Công Nghệ Bưu Chính Viễn Thông"));
        //Map.moveCamera(CameraUpdateFactory.newLatLngZoom(ptitLatlng, 18));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Permission.checkLocationPermission(this);
            toastForRefresh(getResources().getString(R.string.refresh), 1);
            return;
        }


        //checkStoragePermission();
        progressDialog2 = ProgressDialog.show(this, getResources().getString(R.string.wait), getResources().getString(R.string.loading), false);

        mMap.setMyLocationEnabled(true);
        if (myLocation == null) {
            myLocation = getLastKnownLocation();
        }
        try {
            if (myLocation == null) {
                Log.d(TAG, "NULL");
            }
            LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

            ioDataListener.retrieveDatabase_once(this);
            ioDataListener.retrieveDataAlreadySigned_once(this, ioDataListener.getUid());

            if (countstyle % 2 == 0) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)             // Sets the center of the map to location user
                        .zoom(17)                   // Sets the zoom
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            } else {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng)             // Sets the center of the map to location user
                        .zoom(18)                   // Sets the zoom
                        .bearing(60)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

        } catch (NullPointerException ex) {
            ex.printStackTrace();
            Toast.makeText(this, getResources().getString(R.string.gps_problem), Toast.LENGTH_SHORT).show();
            progressDialog2.dismiss();
        }

        //Sự kiện click vào marker info trên bản đồ để kết nối
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                onInfoWindowClickEvent(marker.getTitle());
            }
        });

    }

    @SuppressLint("NewApi")
    public void onInfoWindowClickEvent(String title) {
        try {
            popupWindow.dismiss();
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.activity_detail, (ViewGroup) findViewById(R.id.popup));
            ImageView imageView = (ImageView) layout.findViewById(R.id.profileView);
            ListView listView = (ListView) layout.findViewById(R.id.list_info);
            ImageButton button = (ImageButton) layout.findViewById(R.id.btnFindPath);
            button.setImageDrawable(getDrawable(R.drawable.direction_icon));
            ImageButton callButton = (ImageButton) layout.findViewById(R.id.btnCall);
            callButton.setImageDrawable(getDrawable(R.drawable.call_icon));
            ImageButton sendButton = (ImageButton) layout.findViewById(R.id.btnSend);
            sendButton.setImageDrawable(getResources().getDrawable(R.drawable.send_message_icon));

            popupWindow = new PopupWindow(layout
                    , Resources.getSystem().getDisplayMetrics().widthPixels
                    , Resources.getSystem().getDisplayMetrics().heightPixels * 1 / 3
                    , true);

            popupWindow.setElevation(24);
            popupWindow.setAnimationStyle(R.style.dialog_animation);

            temp = title;

            MarkerStudent m = getMarkerStudentByEmail(title);
            Vector<InformationItem> items;
            if (m != null) {
                final Student s = m.getStudent();
                items = getListData(s);
                listView.setAdapter(new InfoListAdapter(this, items));
                button.setEnabled(true);
                callButton.setEnabled(true);
                sendButton.setEnabled(true);

                Bitmap bmp = Converter.stringToBitmap(s.getImageCode());
                imageView.setImageBitmap(bmp);

                popupWindow.showAtLocation(layout, Gravity.BOTTOM, 0, 0);

                sendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(s.getPhoneNumber().equals("<Không có>")){
                            Toast.makeText(MapsActivity.this, getResources().getString(R.string.no_phone), Toast.LENGTH_SHORT).show();
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
                        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            //request permission from user if the app hasn't got the required permission
                            ActivityCompat.requestPermissions(MapsActivity.this,
                                    new String[]{Manifest.permission.CALL_PHONE},   //request specific permission from user
                                    10);
                            Permission.checkCallPermission(MapsActivity.this);
                            return;
                        }
                        if(s.getPhoneNumber().equals("<Không có>")){
                            Toast.makeText(MapsActivity.this, getResources().getString(R.string.no_phone), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent callIntent = new Intent(Intent.ACTION_CALL); //use ACTION_CALL class
                        callIntent.setData(Uri.parse("tel:" + s.getPhoneNumber()));    //this is the phone number calling
                        startActivity(callIntent);  //call activity and make phone call
                    }
                });

                button.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Toast toast = Toast.makeText(MapsActivity.this, getResources().getString(R.string.shortest_route), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM | Gravity.LEFT, getWidth() / 2, getHeight() / 3);
                        toast.show();
                        return false;
                    }
                });

                callButton.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Toast toast = Toast.makeText(MapsActivity.this, getResources().getString(R.string.call), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM | Gravity.LEFT, getWidth() / 2, getHeight() / 3);
                        toast.show();
                        return false;
                    }
                });

                sendButton.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Toast toast = Toast.makeText(MapsActivity.this, getResources().getString(R.string.send_sms), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM | Gravity.LEFT, getWidth() / 7, getHeight() / 3);
                        toast.show();
                        return false;
                    }
                });

            } else {
                button.setEnabled(false);
                callButton.setEnabled(false);
                sendButton.setEnabled(false);
                if (title.equals("Học Viện Công Nghệ Bưu Chính Viễn Thông")) {
                    imageView.setImageDrawable(getDrawable(R.drawable.ptit_icon));
                } else if (title.equals(getResources().getString(R.string.me))) {
                    items = getListData(me);
                    listView.setAdapter(new InfoListAdapter(this, items));
                    Bitmap bmp = Converter.stringToBitmap(me.getImageCode());
                    imageView.setImageBitmap(bmp);

                } else {
                    return;
                }
                Log.i(TAG, "TEST HERE !");

                popupWindow.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
            }
        } catch (Exception e) {
        }
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

    public MarkerStudent getMarkerStudentByEmail(String email) {
        for (MarkerStudent i : nearbyStudents) {
            if (email.equals(i.getMarker().getTitle())) {
                return i;
            }
        }
        return null;
    }

    public void doFindDirection(View view) {

        MarkerStudent m = getMarkerStudentByEmail(temp);

        LatLng sLatLng = new LatLng(m.getStudent().getPosition().getLat(), m.getStudent().getPosition().getLng());
        LatLng mLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        double dis = DirectionFinder.calculationByDistance(sLatLng, mLatLng);
        Log.i(TAG, "Cách " + m.getStudent().getEmail() + " khoảng: " + dis + " km");
        if (dis > circleArea) {
            //students trong bán kính cho phép
            new AlertDialog.Builder(this)
                    .setMessage(getResources().getString(R.string.direction_recommend))
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create()
                    .show();
            return;
        }
        popupWindow.dismiss();
        onRefreshDetail();
        findDirection(m.getStudent().getPosition().getLat() + ", " + m.getStudent().getPosition().getLng());


    }

    //Cập nhật thông tin người dùng lên server
    public void updateInfoOnServer(Student student) {
        IOData in = new IOData();
        in.pushData(student);
    }

    //Lấy tọa độ của vị trí hiện tại
    public Location getLastKnownLocation() {

        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location loc = mLocationManager.getLastKnownLocation(provider);
            if (loc == null) {
                continue;
            }
            if (bestLocation == null || loc.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = loc;
            }
        }

        return bestLocation;
    }

    //
    public void startAlreadySigned(Student student) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (myLocation == null) {
            return;
        }
        if (student != null) {
            me = student;
            me.setPosition(new StudentLatLng(myLocation.getLatitude(), myLocation.getLongitude()));
            me.setOnline(true);
            updateInfoOnServer(me);

            if (currentMarker != null) {
                currentMarker.remove();
            }
            //Display my marker realtime
            Bitmap mybmp = Converter.stringToBitmap(me.getImageCode());
            mybmp = Bitmap.createScaledBitmap(mybmp, getHeight() / 19, getHeight() / 19, true);
            currentMarker = mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(mybmp))
                    .position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
                    .title(getResources().getString(R.string.me)));
            currentMarker.showInfoWindow();
            //--------------------------
            if (me.getImageCode() != null) {
                progressDialog2.dismiss();
            }
        } else {
            me.setPosition(new StudentLatLng(myLocation.getLatitude(), myLocation.getLongitude()));
            me.setOnline(true);
            updateInfoOnServer(me);

        }
    }

    //Lấy list students trong bán kính cho phép
    public void displayNearbyStudents(Vector<Student> _students) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        if (nearbyMarkers != null) {
            for (Marker m : nearbyMarkers) {
                m.remove();
            }
        }

        nearbyStudents = new Vector<MarkerStudent>();
        nearbyMarkers = new Vector<Marker>();

        for (Student student : _students) {
            //delete marker student each time changes
            if (student.isOnline() && !student.getEmail().equals((me.getEmail()))) {

                Bitmap bmp = Converter.stringToBitmap(student.getImageCode());
                bmp = Bitmap.createScaledBitmap(bmp, getHeight() / 19, getHeight() / 19, true);

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                        .position(new LatLng(student.getPosition().getLat(), student.getPosition().getLng()))
                        .title(student.getEmail()));
                nearbyMarkers.add(marker);
                nearbyStudents.add(new MarkerStudent(student, marker));

            }
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.i(TAG, "CHECK " + location.getLongitude());
            myLocation = location;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public void locationUpdate() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                if (location != null) {
                    Log.i(TAG, "CHECK " + location.getLongitude());
                    myLocation = location;
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, locationListener);
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

    public void toastForRefresh(String message, int duration) {
        Toast toast = Toast.makeText(MapsActivity.this, message, duration);
        toast.setGravity(Gravity.BOTTOM | Gravity.LEFT, getWidth() / 8, getHeight() / 13);
        toast.show();
    }

}
