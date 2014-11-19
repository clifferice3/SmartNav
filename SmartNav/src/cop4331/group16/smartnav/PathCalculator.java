package cop4331.group16.smartnav;

import java.util.*;

public class PathCalculator
{
	private final int RADIUS = 500;
	private final int NUM_PLACES = 10;
	
	private ArrayList<Address>[] locations;
	
	public ArrayList<Address> calculate(ArrayList<String> queryList) throws Exception
	{
		APIWrapper api = new APIWrapper();
		
		findLocations(queryList, api);
		
		return chooseOptimal(api);
	}
	
	@SuppressWarnings("unchecked")
	private void findLocations(ArrayList<String> queryList, APIWrapper api) throws Exception
	{
		locations = new ArrayList[queryList.size() + 1];
		
		Address currentLocation;
		try
		{
			currentLocation = api.getCurrentLoc();
		}
		catch(Exception e)
		{
			throw new Exception("Exception in getCurrentLoc");
		}
		
		locations[0] = new ArrayList<Address>();
		locations[0].add(currentLocation);
		
		for(int i = 0; i < queryList.size(); i++)
		{
			locations[i + 1] = api.queryPlace(queryList.get(i), currentLocation, RADIUS, NUM_PLACES);
		}
	}
	
	private ArrayList<Address> chooseOptimal(APIWrapper api) throws Exception
	{
		long[][] dist = new long[locations.length][];
		int[][] prev = new int[locations.length][];
		
		dist[0] = new long[locations[0].size()];
		prev[0] = new int[locations[0].size()];
		
		for(int i = 1; i < locations.length; i++)
		{
			dist[i] = new long[locations[i].size()];
			prev[i] = new int[locations[i].size()];
			
			Arrays.fill(dist[i], Long.MAX_VALUE);
			
			long[][] matrix = api.getTime(locations[i - 1], locations[i]);
			
			for(int j = 0; j < locations[i].size(); j++)
			{
				for(int k = 0; k < locations[i - 1].size(); k++)
				{
					long currentDist = dist[i - 1][k] + matrix[k][j];

					if(currentDist < dist[i][j])
					{
						dist[i][j] = currentDist;
						prev[i][j] = k;
					}
				}
			}
		}
		
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
		
		for(int i = locations.length - 1; i >= 0; i--)
		{
			path.add(locations[i].get(loc));
			
			loc = prev[i][loc];
		}
		
		Collections.reverse(path);
		
		return path;
	}
}
