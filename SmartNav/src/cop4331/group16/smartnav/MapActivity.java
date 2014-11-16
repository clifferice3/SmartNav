package cop4331.group16.smartnav;

import java.util.ArrayList;
import android.app.Activity;
import android.os.Bundle;

public class MapActivity extends Activity {
	
	private ArrayList<String> input;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        input = getIntent().getStringArrayListExtra("input_list");
        
        
    }
}