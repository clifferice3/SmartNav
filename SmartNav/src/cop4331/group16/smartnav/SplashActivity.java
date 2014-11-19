package cop4331.group16.smartnav;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class SplashActivity extends Activity {

   // Splash screen timer
   private static int SPLASH_TIME_OUT = 1500;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
       getActionBar().hide();
       setContentView(R.layout.activity_splash);

       new Handler().postDelayed(new Runnable() {

           /*
            * Showing splash screen with a timer. This will be useful when you
            * want to show case your app logo / company
            */

           @Override
           public void run() {
               // This method will be executed once the timer is over
               // Start your app main activity
               Intent i = new Intent(SplashActivity.this, InputActivity.class);
               startActivity(i);

               // close this activity
               finish();
           }
       }, SPLASH_TIME_OUT);
   }

}