package cop4331.group16.smartnav;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

public class MapActivity extends FragmentActivity {
	DirectionFragment dFrag;
	ArrayList<String> input;
	ArrayList<Address> optimalAddresses;
	int tripSegment;
	ArrayList<String>[] directions;
	ArrayList<String>[] encoded;
	Directions dir;
	RouteSection[] path;
	ListView lv;
	ArrayAdapter<String> m_adapter;
	ArrayList<String> m_listItems = new ArrayList<String>();
	PathCalculator pathFinder;
	APIWrapper api;
	static Address start;
	
	GoogleMap map;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        
        input = getIntent().getStringArrayListExtra("input_list");
        
        PathCalculator pathFinder = new PathCalculator();
        optimalAddresses = new ArrayList<Address>();
        
        GPSTracker tracker = new GPSTracker(this);
        if (tracker.canGetLocation() == false) {
            tracker.showSettingsAlert();
        } else {
            start = new Address("Start Location", tracker.getLatitude(),tracker.getLongitude());
        }
        
        Button backButton = (Button) findViewById(R.id.button1);
        Button nextButton = (Button) findViewById(R.id.button2);
        final TextView navigate = (TextView) findViewById(R.id.location_txt);
        
       
        map = ((MapFragment)this.getFragmentManager().findFragmentById(R.id.map_fragment)).getMap();
        map.setMyLocationEnabled(true);
        
        api = new APIWrapper();
        
        try {
			optimalAddresses = pathFinder.calculate(input, start);
		} catch (Exception e) {
			;
		}
//        APIWrapper api = new APIWrapper();
//        path = new RouteSection[optimalAddresses.size()];
        try {
        	dir = api.getDirections(optimalAddresses);
        	path = dir.getSections();
        	directions = (ArrayList<String>[]) new ArrayList[path.length];
        	encoded = (ArrayList<String>[]) new ArrayList[path.length];
        	for(int i = 0; i<path.length; i++)
        	{
        		directions[i] = new ArrayList<String>();
        		encoded[i] = new ArrayList<String>();
        		for(RouteStep rs : path[i].getSteps())
        		{
        			directions[i].add(Html.fromHtml(rs.getHtmlInstructions()).toString());
        			encoded[i].add(rs.getPolyline());
        		}
        	}
        	
        	map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
        	    @Override
        	    public void onMapLoaded() {
        	    	ArrayList<Address> temp = new ArrayList<Address>();
        	    	temp.add(optimalAddresses.get(0));
        	    	temp.add(optimalAddresses.get(1));
        	    	api.moveMap(dir.getSouthwest(), dir.getNortheast(), map);
        	    	api.drawMap(temp, encoded[0], map);
        	    }
        	});
        	
        	navigate.setText("Step 1: " + optimalAddresses.get(0).getName() + " to " + optimalAddresses.get(1).getName());
			
        } catch (Exception e) {
        	e.printStackTrace();
        }
//	    directions = (ArrayList<String>[]) new ArrayList[2];
//	    directions[0] = new ArrayList<String>(); directions[1] = new ArrayList<String>();
//	    for(int i = 0; i<3; i++)
//	    {
//	    	directions[0].add(""+(char)('A'+i));
//	      	directions[1].add(""+(char)('1'+i));
//	    }
        dFrag = (DirectionFragment) getFragmentManager().findFragmentById(R.id.list_fragment);
        dFrag.updateView(directions[0]);
        tripSegment = 0;
        
        backButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(tripSegment > 0)
				{
					tripSegment--;
					dFrag.updateView(directions[tripSegment]);
					ArrayList<Address> temp = new ArrayList<Address>();
        	    	temp.add(optimalAddresses.get(tripSegment));
        	    	temp.add(optimalAddresses.get(tripSegment + 1));
        	    	api.drawMap(temp, encoded[tripSegment], map);
					navigate.setText("Step " + (tripSegment+1) + ": " + optimalAddresses.get(tripSegment).getName() + " to " + optimalAddresses.get(tripSegment + 1).getName());
				}
				
			}
		});
        
        nextButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(tripSegment < directions.length - 1)
				{
					tripSegment++;
					dFrag.updateView(directions[tripSegment]);
					ArrayList<Address> temp = new ArrayList<Address>();
        	    	temp.add(optimalAddresses.get(tripSegment));
        	    	temp.add(optimalAddresses.get(tripSegment + 1));
        	    	api.drawMap(temp, encoded[tripSegment], map);
					navigate.setText("Step " + (tripSegment+1) + ": " + optimalAddresses.get(tripSegment).getName() + " to " + optimalAddresses.get(tripSegment + 1).getName());
				}
				
			}
		});
        
    }
}
