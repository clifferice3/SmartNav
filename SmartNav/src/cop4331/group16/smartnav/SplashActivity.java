package cop4331.group16.smartnav;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        // Skipping this for now. Just going directly to InputActivity
        Intent intent = new Intent(this, InputActivity.class);
        startActivity(intent);
    }
}
