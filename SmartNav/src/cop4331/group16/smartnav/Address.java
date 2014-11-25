package cop4331.group16.smartnav;

/**
 * This class is used to represent a specific location that the user could visit.
 */
public class Address
{
	private String name;		// Name of the location
	private double latitude;	// Latitude and longitude coordinates of the location
	private double longitude;
	
	// Constructor
	public Address(String n, double lat, double lon)
	{
		name = n;
		latitude = lat;
		longitude = lon;
	}
	
	// Getter methods
	
	public String getName()
	{
		return name;
	}
	
	public double getLatitude()
	{
		return latitude;
	}
	
	public double getLongitude()
	{
		return longitude;
	}
}
