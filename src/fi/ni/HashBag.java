package fi.ni;

/**
 * @author Jyrki Oraskari
 * @license This work is licensed under a Creative Commons Attribution 3.0 Unported License.
 * http://creativecommons.org/licenses/by/3.0/
 */


import java.util.Map;
import java.util.TreeMap;

public class HashBag {
  Map<Integer,Integer> bag=new TreeMap<Integer,Integer>();
  
  public void add(Integer num)
  {
	  Integer current_value=bag.get(num);
	  if(current_value==null)
	  {
		  bag.put(num,new Integer(1));
	  }
	  else
	  {
		  bag.put(num,(current_value.intValue()+1));
	  }
  }
  
  public void list_result()
  {
	    for (Map.Entry entry : bag.entrySet()) {
		    System.out.println(entry.getKey() + ", " + entry.getValue());
		}

  }
  // for tests
  public static void main(String[] args) {
	  HashBag hb=new HashBag();
	  hb.add(2);
	  hb.add(2);
	  hb.add(1);
	  hb.add(3);
	  hb.add(3);
	  hb.add(3);
	  hb.list_result();
  }
}
