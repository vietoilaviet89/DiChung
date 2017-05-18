package android.dichung;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import Modules.Converter;
import Modules.IOData;
import Modules.IODataListener;
import Modules.Permission;
import model.Student;
import model.StudentLatLng;

public class ProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public final String TAG = "ProfileActivity";

    private EditText txtPhoneNumber;
    private EditText txtDateOfBirth;
    private Spinner spinnerMajor;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private EditText txtDescription;
    private String itemSelected;
    private String genderOption;
    RadioButton radioMale;
    RadioButton radioFemale;
    private IODataListener in;
    private ImageView imageView;
    private int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupUI(findViewById(R.id.profile));
        init();
        in = new IOData();
        in.retrieveDataAlreadySigned_once(this, in.getUid());

    }

    public void init() {
        Permission.checkStoragePermission(this);
        imageView = (ImageView) findViewById(R.id.avatar);
        txtPhoneNumber = (EditText) findViewById(R.id.txtPhoneNumber);
        txtDateOfBirth = (EditText) findViewById(R.id.txtDateOfBirth);
        txtDescription = (EditText) findViewById(R.id.txtDescription);
        spinnerMajor = (Spinner) findViewById(R.id.spinnerMajor);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioMale = (RadioButton) findViewById(R.id.radioMale);
        radioFemale = (RadioButton) findViewById(R.id.radioFemale);
        txtDateOfBirth.setFocusable(false);
        spinnerMajor.setOnItemSelectedListener(this);
        genderOption = "<Không có>";
    }

    public void displayStudentInfo(Student student) {
        if (student != null) {
            if (!student.getPhoneNumber().equals("<Không có>")) {
                txtPhoneNumber.setText(student.getPhoneNumber());
            }
            if (!student.getDateOfBirth().equals("<Không có>")) {
                txtDateOfBirth.setText(student.getDateOfBirth());
            }
            if (!student.getDescription().equals("<Không có>")) {
                txtDescription.setText(student.getDescription());
            }

            if (student.getGender().equals("Nam")) {
                radioMale.setChecked(true);
            }
            if (student.getGender().equals("Nữ")) {
                radioFemale.setChecked(true);
            }
            genderOption = student.getGender();
            String[] adapter = getResources().getStringArray(R.array.major_array);
            int count = 0;
            for (int i = 0; i < adapter.length; i++) {
                if (student.getMajor().equals(adapter[i])) {
                    count = i;
                }
            }
            spinnerMajor.setSelection(count);

            Bitmap bmp = Converter.stringToBitmap(student.getImageCode());
            imageView.setImageBitmap(bmp);
        }
    }

    public void doBack(View view) {
        finish();
    }

    public void doNext(View view) {

        String name = in.getName();
        String email = in.getEmail();
        StudentLatLng studentLatLng = new StudentLatLng(0, 0);
        String dateOfBirth = txtDateOfBirth.getText().toString();
        if (dateOfBirth.equals("")) {
            dateOfBirth = "<Không có>";
        }
        String phoneNumber = txtPhoneNumber.getText().toString();
        if (phoneNumber.equals("")) {
            txtPhoneNumber.setHintTextColor(Color.RED);
            Toast.makeText(this, getResources().getString(R.string.phone_problem), Toast.LENGTH_SHORT).show();
            return;
        }
        String description = txtDescription.getText().toString();
        if (description.equals("")) {
            description = "<Không có>";
        }

        ImageView imageView = (ImageView) findViewById(R.id.avatar);
        String imageCode = Converter.imageToString(imageView);

        Student student = new Student(name, studentLatLng, email, genderOption, itemSelected, dateOfBirth, description, phoneNumber, true);
        student.setImageCode(imageCode);
        in.pushData(student);

        new AlertDialog.Builder(this)
                .setMessage(getResources().getString(R.string.update_success))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .create()
                .show();

    }

    public void chooseGender(View view) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedId);
        if(selectedId == R.id.radioMale){
            genderOption = "Nam";
        }
        else{
            genderOption = "Nữ";
        }
    }

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                this.getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (view instanceof Spinner) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(ProfileActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        itemSelected = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        itemSelected = "<Không có>";
    }

    public void chooseDate(View view) {
        Calendar newCalendar = Calendar.getInstance();
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                txtDateOfBirth.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    public void changeAvatar(View view) {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_picture)), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {


            Log.i(TAG, "TEST IMAGE CHOOSING FUNCTION");
            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
                bitmap = Converter.getRoundedCornerBitmap(bitmap, 45);

                // Log.d(TAG, String.valueOf(bitmap));
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
