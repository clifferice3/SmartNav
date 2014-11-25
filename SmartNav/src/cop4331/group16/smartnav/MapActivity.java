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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

public class MapActivity extends FragmentActivity 
{
	// Declare path calculation variables
	ArrayList<String> input;
	ArrayList<Address> optimalAddresses;
	PathCalculator pathFinder;
	APIWrapper api;
	static Address start;
	
	// Declare direction variables
	ArrayList<String>[] directions;
	ArrayList<String>[] encoded;
	ArrayList<String> encodedStraight;
	Directions dir;
	RouteSection[] path;
	
	// Declare variables used to control the direction
	DirectionFragment dFrag;
	ListView lv;
	ArrayAdapter<String> m_adapter;
	ArrayList<String> m_listItems = new ArrayList<String>();
	int tripSegment;
	
	// Declare the map used
	GoogleMap map;
	
	
	/* This method is called on the creation of this activity
	 * It calculates the optimal path and directions, and initializes
	 * the navigational user interface.
	 */
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
  
    	// Call the parent method to handle system-necessary actions
    	super.onCreate(savedInstanceState);
    	
    	// Set the user interface layout to the XML file provided in the project resources
        setContentView(R.layout.activity_map);
        
        // Store the necessary UI tools
        final Button backButton = (Button) findViewById(R.id.button1);
        final Button nextButton = (Button) findViewById(R.id.button2);
        final TextView navigate = (TextView) findViewById(R.id.location_txt);
        
        // Get the input from InputActivity
        input = getIntent().getStringArrayListExtra("input_list");
        
        // Create objects needed to calculate and store the optimal path
        PathCalculator pathFinder = new PathCalculator();
        optimalAddresses = new ArrayList<Address>();
        
        // Use the GPS Tracker class to obtain the user's current location
        GPSTracker tracker = new GPSTracker(this);
        if (tracker.canGetLocation() == false) 
        {        	
            tracker.showSettingsAlert();
        } else 
        {
            start = new Address("Start Location", tracker.getLatitude(),tracker.getLongitude());
        }
        
        // Initialize the map object, set the user location marker to visible
        map = ((MapFragment)this.getFragmentManager().findFragmentById(R.id.map_fragment)).getMap();
        map.setMyLocationEnabled(true);
        
        // Initialize the APIWrapper
        api = new APIWrapper();
        
        // Attempt to calculate the path. if there is an error, stop calculation and notify the user
        try 
        {
			optimalAddresses = pathFinder.calculate(input, start);
		} catch (Exception e) 
        {
			navigate.setText("Error calculating path. " + e.getMessage());
			return;
		}
        
        // Attempt to find directions. If there is an error, stop calculation and notify the user.
        try 
        {
        	// Get the resultant directions object
        	dir = api.getDirections(optimalAddresses);
        	
        	// Get the RouteSteps associated with the new directions
        	path = dir.getSections();
        	
        	// Initialize directional storage variables
        	directions = (ArrayList<String>[]) new ArrayList[path.length+1];
        	encoded = (ArrayList<String>[]) new ArrayList[path.length];
        	encodedStraight = new ArrayList<String>();
        	
        	// Fill the variables with data from the path object
        	for(int i = 0; i<path.length; i++)
        	{
        		directions[i+1] = new ArrayList<String>();
        		encoded[i] = new ArrayList<String>();
        		for(RouteStep rs : path[i].getSteps())
        		{
        			directions[i+1].add(Html.fromHtml(rs.getHtmlInstructions()).toString());
        			encoded[i].add(rs.getPolyline());
        			encodedStraight.add(rs.getPolyline());
        		}
        	}
        } catch (Exception e)
        {
        	navigate.setText("Error calculating path. " + e.getMessage());
			return;
       	}
        
        // Set the first list the user sees to a list of the names of optimal locations
        directions[0] = new ArrayList<String>();
    	for (int i = 0; i<optimalAddresses.size(); i++)
    	{
    		directions[0].add((i+1) + ". " +optimalAddresses.get(i).getName());
    	}
        	
        // Wait until the map is loaded to draw the full path and set its bounds
        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() 
       	{
        	   @Override
        	   public void onMapLoaded() 
        	   {
        		   api.moveMap(dir.getSouthwest(), dir.getNortheast(), map);
        		   api.drawMap(optimalAddresses, encodedStraight, map);
        	    }
        });
        
        // Set the initial value of the information panel
        navigate.setText("Optimal Locations");
			
        // Initialize the directions fragment and set its initial values
        dFrag = (DirectionFragment) getFragmentManager().findFragmentById(R.id.list_fragment);
        dFrag.updateView(directions[0]);
        tripSegment = 0;
        
        // Set the listener to update the map and directions on a back button click
        backButton.setOnClickListener(new View.OnClickListener() 
        {
			
			@Override
			public void onClick(View v) 
			{
				if(tripSegment > 1)
				{
					// Update the segment value
					tripSegment--;
					
					// Update the directions view
					dFrag.updateView(directions[tripSegment]);
					
					// Update the map segment
					ArrayList<Address> temp = new ArrayList<Address>();
        	    	temp.add(optimalAddresses.get(tripSegment-1));
        	    	temp.add(optimalAddresses.get(tripSegment));
        	    	api.drawMap(temp, encoded[tripSegment-1], map);
        	    	
        	    	// Update the segment information
					navigate.setText("Step " + (tripSegment) + ": " + optimalAddresses.get(tripSegment-1).getName() + " to " + optimalAddresses.get(tripSegment).getName());
				} else if (tripSegment == 1)
				{
					// Update segment
					tripSegment--;
					
					// Restore original values
					dFrag.updateView(directions[tripSegment]);
					api.drawMap(optimalAddresses, encodedStraight, map);
					navigate.setText("Optimal Locations");
				}
				
			}
		});
        
        // Set the listener to update the map and directions on a next button click
        nextButton.setOnClickListener(new View.OnClickListener() 
        {			
			@Override
			public void onClick(View v) 
			{
				if(tripSegment < directions.length - 1)
				{
					// Update segment tracker
					tripSegment++;
					
					// Update directions list
					dFrag.updateView(directions[tripSegment]);
					
					// Update map segment
					ArrayList<Address> temp = new ArrayList<Address>();
        	    	temp.add(optimalAddresses.get(tripSegment-1));
        	    	temp.add(optimalAddresses.get(tripSegment));
        	    	api.drawMap(temp, encoded[tripSegment-1], map);
        	    	
        	    	// Update route info
					navigate.setText("Step " + (tripSegment) + ": " + optimalAddresses.get(tripSegment-1).getName() + " to " + optimalAddresses.get(tripSegment).getName());
				}				
			}
		});
        
    }
}
