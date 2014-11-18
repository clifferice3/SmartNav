package cop4331.group16.smartnav;

import java.util.ArrayList;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MapActivity extends FragmentActivity {
	
	private ArrayList<String> input;
	int tripSegment;
	ArrayList<String>[] directions;
	ListView lv;
	ArrayAdapter<String> m_adapter;
	ArrayList<String> m_listItems = new ArrayList<String>();
	GoogleMap map;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        input = getIntent().getStringArrayListExtra("input_list");
        map = ((MapFragment)this.getFragmentManager().findFragmentById(R.id.map_fragment)).getMap();
        map.setMyLocationEnabled(true);
        
//        PathCalculator pathFinder = new PathCalculator();
//        ArrayList<Address> optimalAddresses = new ArrayList<Address>();
//        try {
//			optimalAddresses = pathFinder.calculate(input);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        APIWrapper api = new APIWrapper();
//        RouteSection[] routes = new RouteSection[0];
//        try {
//        	routes = api.getDirections(optimalAddresses);
//        	directions = new ArrayList[routes.length];
//        	for(int i = 0; i<routes.length; i++)
//        	{
//        		directions[i] = new ArrayList<String>();
//        		for(RouteStep rs : routes[i].getSteps())
//        		{
//        			directions[i].add(rs.getHtmlInstructions());
//        		}
//        	}
//        } catch (Exception e) {
//        	e.printStackTrace();
//        }
        directions = new ArrayList[2];
        directions[0] = new ArrayList<String>(); directions[1] = new ArrayList<String>();
        for(int i = 0; i<3; i++)
        {
        	directions[0].add(""+(char)('A'+i));
        	directions[1].add(""+(char)('1'+i));
        }
        lv = (ListView) findViewById(R.id.directions);
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
					Toast.makeText(MapActivity.this, directions[tripSegment].get(0), Toast.LENGTH_SHORT).show();
					updateView();
				}
				// TODO Auto-generated method stub
				System.out.println("Pressed back");
			}
		});
        Button nextButton = (Button) findViewById(R.id.button2);
        nextButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(tripSegment < directions.length - 1)
				{
					tripSegment++;
					Toast.makeText(MapActivity.this, directions[tripSegment].get(0), Toast.LENGTH_SHORT).show();
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