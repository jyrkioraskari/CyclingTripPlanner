package fi.ni.vo;
/**
 * @author Jyrki Oraskari
 * @license This work is licensed under a Creative Commons Attribution 3.0 Unported License.
 * http://creativecommons.org/licenses/by/3.0/
 */



public class StepVO implements Comparable<StepVO>{
  PointVO p1, p2;
  double distance;
  long touch_time=0;
  
public PointVO getP1() {
	return p1;
}
public void setP1(PointVO p1) {
	this.p1 = p1;
}
public PointVO getP2() {
	return p2;
}
public void setP2(PointVO p2) {
	this.p2 = p2;
}
public double getDistance() {
	return distance;
}
public void setDistance(double distance) {
	this.distance = distance;
}
  
public PointVO getOtherEnd(PointVO me)
{
  if(p1.getGeokey().equals(me.getGeokey()))
	  return p2;
  else
	  return p1;
}

public int compareTo(StepVO o) {
	if((o.touch_time-this.touch_time)>0)
		return -1;
	if((o.touch_time-this.touch_time)<0)
		return 1;
	return 0;
	
}
public long getTouch_time() {
	return touch_time;
}
public void setTouch_time(long touch_time) {
	this.touch_time = touch_time;
}


}
