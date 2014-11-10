package cop4331.group16.smartnav;

import java.util.*;
import java.io.*;
import java.net.*;
import org.json.*;

import com.google.android.maps.MapView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import 	android.location.Location;

public class APIWrapper
{
	private final String KEY = "AIzaSyBUi5Vjva_f7AHw6_pOlZ9QS8Z97cQrPZo";
	private final String API = "https://maps.googleapis.com/maps/api";
	private final String PLACES = "/place";
	private final String TEXT_SEARCH = "/textsearch";
	private final String JSON = "/json";
	
    GoogleMap map;
    private ArrayList<Marker> locs;

    public APIWrapper()
    {
        //map = MapView.getMap();
        locs = new ArrayList<Marker>();
    }
    
	public void drawMap(ArrayList<Address> addresses)
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

	}
	
	public Address getCurrentLoc()
	{
		Location curLoc = map.getMyLocation();
        return new Address(curLoc.getLatitude(), curLoc.getLongitude());
	}
	
	public ArrayList<String> getDirections(Address start, Address end)
	{
		return null;
	}
	
	public ArrayList<Address> queryPlace(String query, Address currentLoc, int radius)
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
			
			URL url = new URL(urlString.toString());
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
			
			JSONObject json = new JSONObject(response.toString());
			
			if(!json.getString("status").equals("OK"))
			{
				return new ArrayList<Address>();
			}
			
			JSONArray results = json.getJSONArray("results");
			for(int i = 0; i < results.length(); i++)
			{
				JSONObject location = results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location");
				places.add(new Address(location.getDouble("lat"), location.getDouble("lng")));
			}
		}
		catch(Exception e)
		{
			return new ArrayList<Address>();
		}
		
		return places;
	}
	
	public double getTime(Address start, Address end)
	{
		return 0.0;
	}
}
