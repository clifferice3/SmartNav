package cop4331.group16.smartnav;

import java.util.*;

import com.google.android.maps.MapView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.LatLng;

import 	android.location.Location;

public class APIWrapper
{
    GoogleMap map;
    private ArrayList<Marker> locs;

    public APIWrapper()
    {
        map = MapView.getMap();
        locs = new ArrayLst<Marker>();
    }

	public void drawMap(ArrayList<Address> addresses)
	{
        //clear the map of all markers
        for (Maker m : locs)
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
            tempMarker = map.addMarker(tempOpt);
            tempMarker.setVisible(true);
            locs.add(tempMarker);
        }

	}
	
	public Address getCurrentLoc()
	{
		Location curLoc = map.getMyLocation();
        return new Address(curLoc.getLatitude, curLoc.getLongitude());
	}
	
	public ArrayList<String> getDirections(Address start, Address end)
	{
		return null;
	}
	
	public ArrayList<Address> queryPlace(String query, int radius)
	{
		return null;
	}
	
	public double getTime(Address start, Address end)
	{
		return 0.0;
	}
}
