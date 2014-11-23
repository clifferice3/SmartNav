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
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * This class performs operations on Google Map objects and performs queries to various Google APIs.
 */
public class APIWrapper
{
	// Parameters to Google APIs
	private final String KEY = "AIzaSyBUi5Vjva_f7AHw6_pOlZ9QS8Z97cQrPZo";
	private final String API = "https://maps.googleapis.com/maps/api";
	private final String DIRECTIONS = "/directions";
	private final String PLACES = "/place";
	private final String DISTANCE_MATRIX = "/distancematrix";
	private final String TEXT_SEARCH = "/textsearch";
	private final String JSON = "/json";
	
	private final int PADDING = 50;				// Amount of padding used in Google Map
	
	private final long INFINITY = (long)1E16;	// Infinity value for places that have no path between them
	
	// Objects used on the Google Map
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
    	if(lines != null)
    	{
    		lines.remove();
    	}
    	
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
            MarkerOptions tempOpt = new MarkerOptions().title(a.getName()).icon(BitmapDescriptorFactory.defaultMarker());
            tempOpt.position(new LatLng(a.getLatitude(), a.getLongitude()));
            Marker tempMarker = map.addMarker(tempOpt);
            tempMarker.setVisible(true);
            locs.add(tempMarker);
        }
        
	    PolylineOptions lineOptions = new PolylineOptions().width(5).color(Color.BLUE);

        for (String s: mapLinesEnc)
        {
        	
        	 // decrypt the poLyline
	        int index = 0, len = s.length();
	        int lat = 0, lng = 0;
	        
	        while (index < len)
	        {
	            int b, shift = 0, result = 0;
	            do
	            {
	                b = s.charAt(index++) - 63;
	                result |= (b & 0x1f) << shift;
	                shift += 5;
	            } while (b >= 0x20);
	
	            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	            lat += dlat;
	
	            shift = 0;
	            result = 0;
	
	            do
	            {
	                b = s.charAt(index++) - 63;
	                result |= (b & 0x1f) << shift;
	                shift += 5;
	            } while (b >= 0x20);
	
	            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	            lng += dlng;
	
	            LatLng p = new LatLng(lat / 1e5, lng / 1e5);
	            lineOptions.add(p);
	        }
        }

        // add polyLine to graph
        lines = map.addPolyline(lineOptions);
        lines.setVisible(true);
    }
	
    /**
     * Positions map to show the box indicated by its southwest and northeast corners.
     */
    public void moveMap(Address southwest, Address northeast, GoogleMap map)
    {
    	LatLng sw = new LatLng(southwest.getLatitude(), southwest.getLongitude());
    	LatLng ne = new LatLng(northeast.getLatitude(), northeast.getLongitude());
    	
    	LatLngBounds bounds = new LatLngBounds(sw, ne);
    	
    	// Get the camera movement
    	CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, PADDING);
    	
    	// Update map
    	map.moveCamera(update);
    }
	
	/**
	 * Returns information about the directions to go through all the places in the path using Google Directions API.
	 * There must not be more than 9 places in path.
	 */
	public Directions getDirections(ArrayList<Address> path) throws Exception
	{
		RouteSection[] sections = new RouteSection[path.size() - 1];
		Address southwest;
		Address northeast;
		
		try
		{
			// Create url
			StringBuilder urlString = new StringBuilder(API + DIRECTIONS);
			urlString.append(JSON + "?");
			urlString.append("origin=" + path.get(0).getLatitude() + "," + path.get(0).getLongitude());
			urlString.append("&destination=" + path.get(path.size() - 1).getLatitude() + "," + path.get(path.size() - 1).getLongitude());

			if(path.size() > 2)
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
			
			// Call Google Directions web service
			String response = queryWebService(urlString.toString());
			
			JSONObject json = new JSONObject(response);
			
			// Error checking
			if(json.getString("status").equals("ZERO_RESULTS"))
			{
				throw new Exception("No path found.");
			}
			else if(json.getString("status").equals("MAX_WAYPOINTS_EXCEEDED") || json.getString("status").equals("OVER_QUERY_LIMIT"))
			{
				throw new Exception("Over query limit.");
			}
			else if(!json.getString("status").equals("OK"))
			{
				throw new Exception();
			}
			
			// Parse JSON response for needed information about the directions
			JSONObject route = json.getJSONArray("routes").getJSONObject(0);
			JSONArray legs = route.getJSONArray("legs");
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
			
			JSONObject bounds = route.getJSONObject("bounds");
			JSONObject sw = bounds.getJSONObject("southwest");
			JSONObject ne = bounds.getJSONObject("northeast");
			southwest = new Address("Southwest", sw.getDouble("lat"), sw.getDouble("lng"));
			northeast = new Address("Northeast", ne.getDouble("lat"), ne.getDouble("lng"));
		}
		catch(Exception e)
		{
			throw e;
		}
		
		return new Directions(sections, southwest, northeast);
	}
	
	/**
	 * Uses Google Places API to find the closest numPlaces places, preferably within radius of the current location,
	 * that match the query.
	 */
	public ArrayList<Address> queryPlace(String query, Address currentLoc, int radius, int numPlaces) throws Exception
	{
		ArrayList<Address> places = new ArrayList<Address>();
		
		try
		{
			// Create url
			StringBuilder urlString = new StringBuilder(API + PLACES + TEXT_SEARCH);
			urlString.append(JSON + "?");
			urlString.append("query=" + URLEncoder.encode(query, "UTF-8"));
			urlString.append("&key=" + KEY);
			urlString.append("&location=" + currentLoc.getLatitude() + "," + currentLoc.getLongitude());
			urlString.append("&radius=" + radius);
			
			// Call Google Places web service
			String response = queryWebService(urlString.toString());
			
			JSONObject json = new JSONObject(response);
			
			// Error checking
			if(json.getString("status").equals("ZERO_RESULTS"))
			{
				throw new Exception("No results found for location " + query + ".");
			}
			else if(json.getString("status").equals("OVER_QUERY_LIMIT"))
			{
				throw new Exception("Over query limit.");
			}
			else if(!json.getString("status").equals("OK"))
			{
				throw new Exception();
			}
			
			// Parse JSON response for places
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
			throw e;
		}
		
		return places;
	}
	
	/**
	 * Uses Google Distance Matrix API to calculate the distance between every pair of starting address and ending address.
	 */
	public long[][] getTime(ArrayList<Address> start, ArrayList<Address> end) throws Exception
	{
		long[][] matrix = new long[start.size()][end.size()];
		
		try
		{
			// Create url
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
			
			// Call Google Distance Matrix web service
			String response = queryWebService(urlString.toString());
			
			JSONObject json = new JSONObject(response);
			
			// Error checking
			if(json.getString("status").equals("MAX_ELEMENTS_EXCEEDED") || json.getString("status").equals("OVER_QUERY_LIMIT"))
			{
				throw new Exception("Over query limit.");
			}
			else if(!json.getString("status").equals("OK"))
			{
				throw new Exception();
			}
			
			// Parse JSON response for distances
			JSONArray rows = json.getJSONArray("rows");
			for(int i = 0; i < rows.length(); i++)
			{
				JSONArray elements = rows.getJSONObject(i).getJSONArray("elements");
				for(int j = 0; j < elements.length(); j++)
				{
					JSONObject element = elements.getJSONObject(j);
					
					// Additional error checking
					if(element.getString("status").equals("ZERO_RESULTS"))
					{
						matrix[i][j] = INFINITY;
						continue;
					}
					else if(!element.getString("status").equals("OK"))
					{
						throw new Exception();
					}
					
					matrix[i][j] = element.getJSONObject("duration").getLong("value");
				}
			}
		}
		catch(Exception e)
		{
			throw e;
		}
		
		return matrix;
	}
	
	/**
	 * Creates an asynchronous thread to query the web service as specified by urlString
	 */
	private String queryWebService(String urlString) throws Exception
	{
		WebServiceTask task = new WebServiceTask();
		
		// Call web service and wait for response
		String response = task.execute(urlString).get();
		
		// Error checking
		if(task.e != null)
		{
			throw task.e;
		}
		
		return response;
	}
	
	/**
	 * An asynchronous task to call a web service as specified by a string and return the JSON response
	 */
	private class WebServiceTask extends AsyncTask<String, Void, String>
	{
		private Exception e;
		
		protected String doInBackground(String... urlString)
		{
			String ret = "";
			
			try
			{
				// Get http connection
				URL url = new URL(urlString[0]);
				HttpURLConnection connection = (HttpURLConnection)url.openConnection();
				
				// Get input stream
				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				
				// Pull response out of stream
				StringBuilder response = new StringBuilder();
				String line = br.readLine();
				while(line != null)
				{
					response.append(line);
					line = br.readLine();
				}
				
				br.close();
				
				ret = response.toString();
			}
			catch(Exception ex)
			{
				e = ex;
			}
			
			return ret;
		}
	}
}
