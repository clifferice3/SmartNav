package cop4331.group16.smartnav;

import java.util.*;

public class PathCalculator
{
	private final int RADIUS = 1000000;
	
	private ArrayList<Address>[] locations;
	
	public ArrayList<Address> calculate(ArrayList<String> queryList)
	{
		APIWrapper api = new APIWrapper();
		
		findLocations(queryList, api);
		
		return chooseOptimal(api);
	}
	
	@SuppressWarnings("unchecked")
	private void findLocations(ArrayList<String> queryList, APIWrapper api)
	{
		locations = new ArrayList[queryList.size() + 1];
		
		Address currentLocation = api.getCurrentLoc();
		
		locations[0] = new ArrayList<Address>();
		locations[0].add(currentLocation);
		
		for(int i = 0; i < queryList.size(); i++)
		{
			locations[i + 1] = api.queryPlace(queryList.get(i), RADIUS);
		}
	}
	
	private ArrayList<Address> chooseOptimal(APIWrapper api)
	{
		double[][] dist = new double[locations.length][];
		int[][] prev = new int[locations.length][];
		
		for(int i = 1; i < locations.length; i++)
		{
			dist[i] = new double[locations[i].size()];
			prev[i] = new int[locations[i].size()];
			
			for(int j = 0; j < locations[i].size(); j++)
			{
				for(int k = 0; k < locations[i - 1].size(); k++)
				{
					double currentDist = dist[i - 1][k] + api.getTime(locations[i - 1].get(k), locations[i].get(j));

					if(currentDist < dist[i][j])
					{
						dist[i][j] = currentDist;
						prev[i][j] = k;
					}
				}
			}
		}
		
		int loc = -1;
		double best = Double.POSITIVE_INFINITY;
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
