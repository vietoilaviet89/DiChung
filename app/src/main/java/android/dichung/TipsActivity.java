package android.dichung;

import android.dichung.fragment.BlankFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class TipsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        BlankFragment fragment = (BlankFragment) this.getSupportFragmentManager()
                .findFragmentById(R.id.blank_fragment);

    }

}
