package cop4331.group16.smartnav;

public class Address
{
	private double latitude;
	private double longitude;
	
	public Address(double lat, double lon)
	{
		latitude = lat;
		longitude = lon;
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
