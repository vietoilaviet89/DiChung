package Modules;

import android.content.Context;

import model.Student;

/**
 * Created by 123456789 on 4/17/2017.
 */

public interface IODataListener {
    void retrieveDatabase(final Context context);
    void retrieveDatabase_once(final Context context);
    void pushData(Student student);
    void retrieveDataAlreadySigned(Context context, String uid);
    void retrieveDataAlreadySigned_once(final Context context, String uid);
    void updateOnlineByUid();
    void updateOfflineByUid();
    boolean isActive();
    String getName();
    String getEmail();
    String getUid();
    }
