package cop4331.group16.smartnav;

public class Address
{
	private String name;
	private double latitude;
	private double longitude;
	
	public Address(String n, double lat, double lon)
	{
		name = n;
		latitude = lat;
		longitude = lon;
	}
	
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
