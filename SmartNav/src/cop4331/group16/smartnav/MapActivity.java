package cop4331.group16.smartnav;

import java.util.ArrayList;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MapActivity extends FragmentActivity {
	
	private ArrayList<String> input;
	public static FragmentManager fragmentManager;
	int tripSegment;
	ArrayList<String>[] directions;
	ListView lv;
	ArrayAdapter<String> m_adapter;
	ArrayList<String> m_listItems = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        input = getIntent().getStringArrayListExtra("input_list");
        fragmentManager = getFragmentManager();
        FragmentTransaction ftrans = fragmentManager.beginTransaction();
        
        
        
        
        PathCalculator pathFinder = new PathCalculator();
        ArrayList<Address> optimalAddresses = new ArrayList<Address>();
        try {
			optimalAddresses = pathFinder.calculate(input);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        // TODO: Get directions from Map APIs.
        
        lv = (ListView) findViewById(R.id.list_fragment);
        m_adapter = new ArrayAdapter<String>(this, R.layout.activity_map, m_listItems);
        lv.setAdapter(m_adapter);
        tripSegment = 0;
        Button backButton = (Button) findViewById(R.id.button1);
        backButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(tripSegment > 0)
				{
					tripSegment--;
					updateView();
				}
				// TODO Auto-generated method stub
				System.out.println("Presed back");
			}
		});
        Button nextButton = (Button) findViewById(R.id.button2);
        nextButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(tripSegment < directions.length - 1)
				{
					tripSegment++;
					updateView();
				}
				// TODO Auto-generated method stub
				System.out.println("Presed next");
			}
		});
        
    }
    protected void updateView() {
    	ArrayList<String> toDisplay = directions[tripSegment];
        m_listItems = toDisplay;
        m_adapter.notifyDataSetChanged();
    } 
} 