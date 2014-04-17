

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.ni.vo.PointVO;
import fi.ni.vo.StepVO;

/**
 * @author Jyrki Oraskari
 * @license This work is licensed under a Creative Commons Attribution 3.0 Unported License.
 * http://creativecommons.org/licenses/by/3.0/
 */


public class LinkedPointVO {
	PointVO me;
    List<StepVO> links_out=new ArrayList<StepVO>();
    Map<String,StepVO> shortcut=new HashMap<String,StepVO>();
    
	public LinkedPointVO(PointVO me) {
		super();
		this.me = me;
	}
	
	public PointVO getMe() {
		return me;
	}

	public List<StepVO> getLinks_out() {
		return links_out;
	}

	public StepVO getRandomLink() {
		Collections.sort(links_out);
	    double variance= links_out.size()/2;
	    if(variance==0)
	    	variance=1;
		int random_index=Math.abs(((int)(CycleTripPlanner.randomGenerator.nextGaussian()*variance)));
		if(random_index>(links_out.size()-1))
			random_index=links_out.size()-1;
		links_out.get(random_index).setTouch_time(CycleTripPlanner.global_routing_counter++);
		return links_out.get(random_index);
	}

	public double getLat() {
		return me.getLat();
	}
	public double getLng() {
		return me.getLng();
	}

	public void addStep(StepVO step) {
	   // Filter out dublicates
	   for(int n=0;n<links_out.size();n++)
	   {
		   StepVO link=links_out.get(n);
		   if(link.getP1().getGeokey().equals(step.getP1().getGeokey()))
				   if(link.getP2().getGeokey().equals(step.getP2().getGeokey()))
				   {
					   return;
				   }
				   
	   }
	   if(!links_out.contains(step))
           links_out.add(step);
       if(me.getGeokey().equals(step.getP1().getGeokey()))
       {
    	   shortcut.put(step.getP2().getGeokey(), step);
       }
       else
       {
    	   shortcut.put(step.getP1().getGeokey(), step);
    	   
       }
		
	}

	public Map<String, StepVO> getShortcut() {
		return shortcut;
	}

	
}
