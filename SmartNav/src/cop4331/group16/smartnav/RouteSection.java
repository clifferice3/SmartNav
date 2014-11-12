package cop4331.group16.smartnav;

import java.util.*;

public class RouteSection
{
	private Address startLocation;
	private Address endLocation;
	private ArrayList<RouteStep> steps;
	
	public RouteSection(Address start, Address end, ArrayList<RouteStep> s)
	{
		startLocation = start;
		endLocation = end;
		steps = s;
	}
	
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
