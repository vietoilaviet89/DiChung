package Modules;

import android.content.Context;
import android.dichung.MapsActivity;
import android.dichung.PeopleActivity;
import android.dichung.ProfileActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Vector;

import model.Student;

/**
 * Created by 123456789 on 4/10/2017.
 */

public class IOData implements IODataListener{
    private GoogleMap mMap;

    @Override
    public void retrieveDatabase(final Context context){
        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(FirebaseAuth.getInstance().getCurrentUser()==null){
                    return;
                }

                if(context instanceof MapsActivity){
                    MapsActivity mapsActivity = (MapsActivity) context;
                    Vector<Student> listS = new Vector<Student>();
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        listS.add(dsp.getValue(Student.class)); //add result into array list
                    }
                    mapsActivity.displayNearbyStudents(listS);
                }

                if(context instanceof PeopleActivity){
                    PeopleActivity peopleActivity = (PeopleActivity) context;
                    Vector<Student> listS = new Vector<Student>();
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        listS.add(dsp.getValue(Student.class)); //add result into array list
                    }
                    peopleActivity.addControls(listS);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    @Override
    public void retrieveDatabase_once(final Context context) {
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(FirebaseAuth.getInstance().getCurrentUser()==null){
                    return;
                }

                MapsActivity mapsActivity = (MapsActivity) context;
                Vector<Student> listS = new Vector<Student>();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    listS.add(dsp.getValue(Student.class)); //add result into array list
                }
                mapsActivity.displayNearbyStudents(listS);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void pushData(Student student){
        if(FirebaseAuth.getInstance().getCurrentUser()== null){
            return;
        }
        FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(student);
    }

    @Override
    public void retrieveDataAlreadySigned_once(final Context context, String uid){

        FirebaseDatabase.getInstance().getReference(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(FirebaseAuth.getInstance().getCurrentUser()==null){
                    return;
                }
                Student student = dataSnapshot.getValue(Student.class);
                if(context instanceof MapsActivity){
                    MapsActivity mapsActivity = (MapsActivity) context;
                    mapsActivity.startAlreadySigned(student);
                }
                if(context instanceof ProfileActivity){
                    ProfileActivity profileActivity = (ProfileActivity) context;
                    profileActivity.displayStudentInfo(student);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void retrieveDataAlreadySigned(final Context context, String uid){

        FirebaseDatabase.getInstance().getReference(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(FirebaseAuth.getInstance().getCurrentUser()==null){
                    return;
                }

                Student student = dataSnapshot.getValue(Student.class);
                if(context instanceof MapsActivity){
                    MapsActivity mapsActivity = (MapsActivity) context;
                    mapsActivity.startAlreadySigned(student);
                }
                if(context instanceof ProfileActivity){
                    ProfileActivity profileActivity = (ProfileActivity) context;
                    profileActivity.displayStudentInfo(student);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void updateOnlineByUid(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference(uid).child("online").setValue(true);
    }

    @Override
    public void updateOfflineByUid(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference(uid).child("online").setValue(false);
        FirebaseAuth.getInstance().signOut();

    }

    public boolean isActive(){
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            return false;
        }
        return true;
    }

    @Override
    public String getName() {
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            return null;
        }
        return FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    }

    @Override
    public String getEmail() {
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            return null;
        }
        return FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }

    @Override
    public String getUid() {
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            return null;
        }
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

}
