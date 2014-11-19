package cop4331.group16.smartnav;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

public class APIWrapper
{
	private final String KEY = "AIzaSyBUi5Vjva_f7AHw6_pOlZ9QS8Z97cQrPZo";
	private final String API = "https://maps.googleapis.com/maps/api";
	private final String DIRECTIONS = "/directions";
	private final String PLACES = "/place";
	private final String DISTANCE_MATRIX = "/distancematrix";
	private final String TEXT_SEARCH = "/textsearch";
	private final String JSON = "/json";
	private final int MAX_PATH_SIZE = 10;
	Location cur;

    static ArrayList<Marker> locs = new ArrayList<Marker>();
    static Polyline lines;

    /**
     * Function: drawMap
     * takes in a list of locations and an encrypted polyLine
     * adds markers to the map at each location in list
     * decrypts and draws polyLine on the map
     */
    public void drawMap(ArrayList<Address> addresses, ArrayList<String> mapLinesEnc, GoogleMap map)
    {
        //clear the map of all markers
        for (Marker m : locs)
        {
            m.remove();
        }

        //remove markers from list of markers on map
        locs.clear();

        //add markers at each location on the map
        for (Address a : addresses)
        {
            MarkerOptions tempOpt = new MarkerOptions();
            tempOpt.position(new LatLng(a.getLatitude(), a.getLongitude()));
            Marker tempMarker = map.addMarker(tempOpt);
            tempMarker.setVisible(true);
            locs.add(tempMarker);
        }

        ArrayList<LatLng> points = new ArrayList<LatLng>();

	       

        for (String s: mapLinesEnc) {
        	
        	 // decrypt the poLyline
	        int index = 0, len = s.length();
	        int lat = 0, lng = 0;
	        
	        while (index < len)
	        {
	            int b, shift = 0, result = 0;
	            do {
	                b = s.charAt(index++) - 63;
	                result |= (b & 0x1f) << shift;
	                shift += 5;
	            } while (b >= 0x20);
	
	            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	            lat += dlat;
	
	            shift = 0;
	            result = 0;
	
	            do {
	                b = s.charAt(index++) - 63;
	                result |= (b & 0x1f) << shift;
	                shift += 5;
	            } while (b >= 0x20);
	
	            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	            lng += dlng;
	
	            LatLng p = new LatLng((int) (((double) lat / 1E5) * 1E6), (int) (((double) lng / 1E5) * 1E6));
	            points.add(p);
	        }
        }

        // add polyLine to graph
        lines.setPoints(points);
        lines.setVisible(true);
    }
	
	
	/**
	 * Returns an array of RouteSections to go through all the places in path
	 * The RouteSections contain steps which each have html instructions and a polyline
	 * path must not contain more than 18 elements
	 */
	public RouteSection[] getDirections(ArrayList<Address> path) throws Exception
	{
		RouteSection[] sections = new RouteSection[path.size() - 1];
		for(int i = 0; i < path.size(); i += MAX_PATH_SIZE - 1)
		{
			ArrayList<Address> currentQuery = new ArrayList<Address>();
			for(int j = 0; j < MAX_PATH_SIZE && i + j < path.size(); j++)
			{
				currentQuery.add(path.get(i + j));
			}
			
			RouteSection[] currentSections = getDirections(currentQuery, currentQuery.size());
			
			for(int k = 0; k < currentSections.length; k++)
			{
				sections[i + k] = currentSections[k];
			}
		}
		
		return sections;
	}
	
	private RouteSection[] getDirections(ArrayList<Address> path, int length) throws Exception
	{
		RouteSection[] sections = new RouteSection[length - 1];
		
		try
		{
			StringBuilder urlString = new StringBuilder(API + DIRECTIONS);
			urlString.append(JSON + "?");
			urlString.append("origin=" + path.get(0).getLatitude() + "," + path.get(0).getLongitude());
			urlString.append("&destination=" + path.get(path.size() - 1).getLatitude() + "," + path.get(path.size() - 1).getLongitude());

			if(length > 2)
			{
				urlString.append("&waypoints=");
				
				for(int i = 1; i < path.size() - 1; i++)
				{
					if(i > 1)
					{
						urlString.append("|");
					}
					
					urlString.append(path.get(i).getLatitude() + "," + path.get(i).getLongitude());
				}
			}
			
			urlString.append("&key=" + KEY);
			
			String response = queryWebService(urlString.toString());
			
			JSONObject json = new JSONObject(response);
			
			if(!json.getString("status").equals("OK"))
			{
				throw new Exception();
			}
			
			JSONArray legs = json.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
			for(int i = 0; i < legs.length(); i++)
			{
				JSONObject leg = legs.getJSONObject(i);
				
				ArrayList<RouteStep> routeSteps = new ArrayList<RouteStep>();
				JSONArray steps = leg.getJSONArray("steps");
				for(int j = 0; j < steps.length(); j++)
				{
					JSONObject step = steps.getJSONObject(j);
					routeSteps.add(new RouteStep(step.getString("html_instructions"), step.getJSONObject("polyline").getString("points")));
				}
				
				sections[i] = new RouteSection(path.get(i), path.get(i + 1), routeSteps);
			}
		}
		catch(Exception e)
		{
			throw new Exception();
		}
		
		return sections;
	}
	
	public ArrayList<Address> queryPlace(String query, Address currentLoc, int radius, int numPlaces) throws Exception
	{
		ArrayList<Address> places = new ArrayList<Address>();
		
		try
		{
			StringBuilder urlString = new StringBuilder(API + PLACES + TEXT_SEARCH);
			urlString.append(JSON + "?");
			urlString.append("query=" + URLEncoder.encode(query, "UTF-8"));
			urlString.append("&key=" + KEY);
			urlString.append("&location=" + currentLoc.getLatitude() + "," + currentLoc.getLongitude());
			urlString.append("&radius=" + radius);
			
			String response = queryWebService(urlString.toString());
			
			JSONObject json = new JSONObject(response);
			
			if(!json.getString("status").equals("OK"))
			{
				throw new Exception();
			}
			
			JSONArray results = json.getJSONArray("results");
			for(int i = 0; i < Math.min(numPlaces, results.length()); i++)
			{
				JSONObject place = results.getJSONObject(i);
				JSONObject location = place.getJSONObject("geometry").getJSONObject("location");
				places.add(new Address(place.getString("name"), location.getDouble("lat"), location.getDouble("lng")));
			}
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			
			throw new Exception();
		}
		
		return places;
	}
	
	public long[][] getTime(ArrayList<Address> start, ArrayList<Address> end) throws Exception
	{
		long[][] matrix = new long[start.size()][end.size()];
		
		try
		{
			StringBuilder urlString = new StringBuilder(API + DISTANCE_MATRIX);
			urlString.append(JSON + "?");
			urlString.append("origins=");
			
			for(int i = 0; i < start.size(); i++)
			{
				if(i > 0)
				{
					urlString.append("|");
				}
				
				urlString.append(start.get(i).getLatitude() + "," + start.get(i).getLongitude());
			}
			
			urlString.append("&destinations=");
			
			for(int j = 0; j < end.size(); j++)
			{
				if(j > 0)
				{
					urlString.append("|");
				}
				
				urlString.append(end.get(j).getLatitude() + "," + end.get(j).getLongitude());
			}
			
			urlString.append("&key=" + KEY);
			
			String response = queryWebService(urlString.toString());
			
			JSONObject json = new JSONObject(response);
			
			if(!json.getString("status").equals("OK"))
			{
				if(json.getString("status").equals("OVER_QUERY_LIMIT"))
				{
					Thread.sleep(2000);
					return getTime(start, end);
				}
				else
				{
					throw new Exception();
				}
			}
			
			JSONArray rows = json.getJSONArray("rows");
			for(int i = 0; i < rows.length(); i++)
			{
				JSONArray elements = rows.getJSONObject(i).getJSONArray("elements");
				for(int j = 0; j < elements.length(); j++)
				{
					JSONObject element = elements.getJSONObject(j);
					if(!element.getString("status").equals("OK"))
					{
						throw new Exception();
					}
					
					matrix[i][j] = element.getJSONObject("duration").getLong("value");
				}
			}
		}
		catch(Exception e)
		{
			throw new Exception();
		}
		
		return matrix;
	}
	
	private String queryWebService(String urlString) throws Exception
	{
		URL url = new URL(urlString);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		
		StringBuilder response = new StringBuilder();
		String line = br.readLine();
		while(line != null)
		{
			response.append(line);
			line = br.readLine();
		}
		
		br.close();
		
		return response.toString();
	}
	 /**
     * Class: LocActivity
     * tries to get the current location until successful
     * sets cur to the currentLocation on success
     */
    class LocActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
        // . . . . . . . . more stuff here 
        LocationRequest locationRequest;
        LocationClient locationClient;

     
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            // . . . . other initialization code
            locationClient = new LocationClient(this, this, this);
    locationRequest = new LocationRequest();
    // Use high accuracy
    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            // Set the update interval to 5 seconds
    locationRequest.setInterval(5);
            // Set the fastest update interval to 1 second
    locationRequest.setFastestInterval(1);
        }
        // . . . . . . . . other methods 
        @Override
        public void onConnected(Bundle bundle) {
            Location location = locationClient.getLastLocation();
            if (location == null)
                locationClient.requestLocationUpdates(locationRequest, this);
            else
            cur = location;       
        }
        // . . . . . . . . other methods
        @Override
        public void onLocationChanged(Location location) {
            locationClient.removeLocationUpdates(this);
            // Use the location here!!!
        }
@Override
public void onConnectionFailed(ConnectionResult arg0) {
// TODO Auto-generated method stub


}
@Override
public void onDisconnected() {
// TODO Auto-generated method stub


}   
    }
}
