import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.geohash.Geohash;

import fi.ni.vo.StepVO;

/**
 * @author Jyrki Oraskari
 * @license This work is licensed under a Creative Commons Attribution 3.0 Unported License.
 * http://creativecommons.org/licenses/by/3.0/
 */



public class RouteVO implements Comparable<RouteVO>{
	final List<String> geohashes;
	final double distance;
	final Set<String> geohash_set=new HashSet<String>();
	final double variation;
	double unique_distance=0;
	
	public RouteVO(List<String> geohashes,Map<String, LinkedPointVO> location_map) {
		super();
		this.geohashes = geohashes;
	
        // Calculate distance		
		double l_distance = 0;
		LinkedPointVO current;
		Set<StepVO> ustep=new HashSet<StepVO>();
		for (int i = 0; i < geohashes.size(); i++) {
			current = location_map.get(geohashes.get(i));

			if (i < (geohashes.size() - 1)) {
				StepVO s = current.getShortcut().get(
						geohashes.get(i+1));
				if (s == null) {
					System.err.println("Should have a step from the point");
				    distance=-1;
				    variation=0;
					return;
				}
				l_distance += s.getDistance();
				geohash_set.add(geohashes.get(i));
				if(ustep.add(s))
				{
					unique_distance+=s.getDistance();
				}
			}
		}
       distance=l_distance;
	   variation=unique_distance/l_distance;
	}
	
	
	public List<String> getGeohashes() {
		return geohashes;
	}

	public double getDistance() {
		return distance;
	}
	public Set<String> getGeohash_set() {
		return geohash_set;
	}
	
	public void showThePath()
	{
		System.out.println();
		System.out.print(": "+distance+" v:"+variation+" g:"+getGrade());
	}
	
	public double getVariation()
	{
		return variation;
	}
	
	public int getGrade()
	{
		int i_dist=((int)(getDistance()/1000f));
		int distance_points=Math.abs(150-i_dist);
		if(i_dist<150)
			distance_points+=1;  // preferably more or equal than 50 km
		return (int)((1-getVariation())*50f+distance_points);
		
	}

	public int compareTo(RouteVO other) {
		
		// Minimize
		return this.getGrade()-other.getGrade();
	}
}
