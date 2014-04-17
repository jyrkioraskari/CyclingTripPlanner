import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import fi.ni.vo.PointVO;
import fi.ni.vo.StepVO;

/**
 * @author Jyrki Oraskari
 * @license This work is licensed under a Creative Commons Attribution 3.0 Unported License.
 * http://creativecommons.org/licenses/by/3.0/
 */

public class DirectionsSAXParser extends DefaultHandler {

	boolean is_in_step=false;
	boolean is_in_distance=false;
	boolean is_in_start_location=false;
	boolean is_in_end_location=false;
    StringBuffer characters=new StringBuffer();
	
    PointVO start_point;
    PointVO end_point;
    StepVO  step;
    List<StepVO> step_list=new ArrayList<StepVO>();
    
    public DirectionsSAXParser() {
        super();
    }



    public void startElement(String uri, String name, String qName, Attributes atts) {
        if ("".equals(uri))
        {
        	if(is_in_step)
        	{
                if(qName.equals("lat"))
                {
                	characters.setLength(0);
                }
                if(qName.equals("lng"))
                {
                	characters.setLength(0);
                }
                
                if(qName.equals("start_location"))
                {
                	is_in_start_location=true;
                }
                
                if(qName.equals("end_location"))
                {
                	is_in_end_location=true;
                }
                
                if(qName.equals("distance"))
                {
                	is_in_distance=true;
                }
                
                if(qName.equals("value"))
                {
                	characters.setLength(0);
                }
        	}
            if(qName.equals("step"))
            {
            	is_in_step=true;
                step=new StepVO();
            	start_point=new PointVO();
            	end_point=new PointVO();
            	step.setP1(start_point);
            	step.setP2(end_point);

            }
        }
    }

    public void endElement(String uri, String name, String qName) {
        if ("".equals(uri))
        {
        	if(is_in_step)
        	{
                if(qName.equals("lat"))
                {
                	if(is_in_start_location)
                	  start_point.setLat(characters.toString());
                	if(is_in_end_location)
                  	  end_point.setLat(characters.toString());
                }
                
                if(qName.equals("lng"))
                {
                	if(is_in_start_location)
                	  start_point.setLng(characters.toString());
                	if(is_in_end_location)
                      end_point.setLng(characters.toString());
                }

                if(qName.equals("value"))
                {
                	if(is_in_distance)
                	{
                		double distance =Double.parseDouble(characters.toString());
                		step.setDistance(distance);
                	}
                }

                if(qName.equals("start_location"))
                	is_in_start_location=false;
                
                if(qName.equals("end_location"))
                	is_in_end_location=false;
                
                if(qName.equals("distance"))
                {
                	is_in_distance=false;
                }

        	}
            if(qName.equals("step"))
            {
            	is_in_step=false;
            	step_list.add(step);
            }
        }
    }

    public void characters(char ch[], int start, int length) {
        for (int i = start; i < start + length; i++) {
                characters.append(ch[i]);
        }
    }



	public List<StepVO> getStep_list() {
		return step_list;
	}


    
    
}
