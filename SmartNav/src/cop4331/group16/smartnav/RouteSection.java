package cop4331.group16.smartnav;

import java.util.*;

/**
 * This class stores information about a particular leg of the path
 */
public class RouteSection
{
	private Address startLocation;			// Starting location of this leg of the trip
	private Address endLocation;			// Ending location of this leg of the trip
	private ArrayList<RouteStep> steps;		// Steps of this leg of the trip
	
	// Constructor
	public RouteSection(Address start, Address end, ArrayList<RouteStep> s)
	{
		startLocation = start;
		endLocation = end;
		steps = s;
	}
	
	// Getter methods
	
	public Address getStartLocation()
	{
		return startLocation;
	}
	
	public Address getEndLocation()
	{
		return endLocation;
	}
	
	public ArrayList<RouteStep> getSteps()
	{
		return steps;
	}
}
