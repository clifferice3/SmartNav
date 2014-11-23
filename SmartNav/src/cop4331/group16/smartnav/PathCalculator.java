package cop4331.group16.smartnav;

import java.util.*;

/**
 * This class is used to calculate the optimal path given a list of user queries.
 * The queries must be in text form and can be a description of the place the user wants to go to.
 * It returns the optimal path as a list of Addresses.
 */
public class PathCalculator
{
	private final int RADIUS = 500;			// Preferred radius for places that match the queries
	private final int NUM_PLACES = 10;		// Number of places gathered for each query
	
	private ArrayList<Address>[] locations;
	
	/**
	 * This is the method that other classes should call.
	 * queryList must be a list of Strings representing the user's queries, and startLocation is the current position of the user.
	 * It returns a list of Addresses representing the places, in order, that the user should visit.
	 */
	public ArrayList<Address> calculate(ArrayList<String> queryList, Address startLocation) throws Exception
	{
		APIWrapper api = new APIWrapper();
		
		// get candidate locations
		findLocations(queryList, startLocation, api);
		
		// calculate optimal path
		return chooseOptimal(api);
	}
	
	/**
	 * Fills locations with lists of Addresses for candidate places that the user could visit for each query.
	 */
	@SuppressWarnings("unchecked")
	private void findLocations(ArrayList<String> queryList, Address startLocation, APIWrapper api) throws Exception
	{
		locations = new ArrayList[queryList.size() + 1];
		
		// The first query is the starting location
		locations[0] = new ArrayList<Address>();
		locations[0].add(startLocation);
		
		// Find locations for all the other queries
		for(int i = 0; i < queryList.size(); i++)
		{
			locations[i + 1] = api.queryPlace(queryList.get(i), startLocation, RADIUS, NUM_PLACES);
		}
	}
	
	/**
	 * Calculates the shortest path that visits exactly one place from each list in locations in order.
	 */
	private ArrayList<Address> chooseOptimal(APIWrapper api) throws Exception
	{
		long[][] dist = new long[locations.length][];
		int[][] prev = new int[locations.length][];
		
		// The distance to the start location is 0
		dist[0] = new long[locations[0].size()];
		prev[0] = new int[locations[0].size()];
		
		for(int i = 1; i < locations.length; i++)
		{
			dist[i] = new long[locations[i].size()];
			prev[i] = new int[locations[i].size()];
			
			Arrays.fill(dist[i], Long.MAX_VALUE);
			
			// Find the time needed to travel between every pair of places
			long[][] matrix = queryTime(locations[i - 1], locations[i], api);
			
			for(int j = 0; j < locations[i].size(); j++)
			{
				for(int k = 0; k < locations[i - 1].size(); k++)
				{
					long currentDist = dist[i - 1][k] + matrix[k][j];

					// Update the distance to j if the best path seen so far comes from k
					if(currentDist < dist[i][j])
					{
						dist[i][j] = currentDist;
						prev[i][j] = k;
					}
				}
			}
		}
		
		// Find the best ending location
		int loc = -1;
		long best = Long.MAX_VALUE;
		for(int i = 0; i < dist[dist.length - 1].length; i++)
		{
			if(dist[dist.length - 1][i] < best)
			{
				best = dist[dist.length - 1][i];
				loc = i;
			}
		}
		
		ArrayList<Address> path = new ArrayList<Address>();
		
		// Build back the path using the previous state from each location in the path
		for(int i = locations.length - 1; i >= 0; i--)
		{
			path.add(locations[i].get(loc));
			
			loc = prev[i][loc];
		}
		
		Collections.reverse(path);
		
		return path;
	}
	
	/**
	 * Uses APIWrapper to find the time needed to travel between every pair of start location and end location.
	 * This function may need to wait to satisfy the query limits.
	 */
	private long[][] queryTime(ArrayList<Address> start, ArrayList<Address> end, APIWrapper api) throws Exception
	{
		long[][] matrix = null;
		
		// Attempt to access Distance Matrix API for a certain number of tries
		int tries = 20;
		while(tries > 0)
		{
			try
			{
				// Get distances
				matrix = api.getTime(start, end);
				
				break;
			}
			catch(Exception e)
			{
				if(e.getMessage().equals("Over query limit."))
				{
					// Wait for Distance Matrix API
					Thread.sleep(2000);
				}
				else
				{
					throw e;
				}
			}
			
			tries--;
		}
		
		if(matrix == null)
		{
			// There must be another reason for being over the query limit
			throw new Exception("Over query limit.");
		}
		
		return matrix;
	}
}
