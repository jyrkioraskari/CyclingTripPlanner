package fi.ni.vo;

/**
 * @author Jyrki Oraskari
 * @license This work is licensed under a Creative Commons Attribution 3.0 Unported License.
 * http://creativecommons.org/licenses/by/3.0/
 */

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

import com.google.geohash.Geohash;


public class PointVO {
	  String geokey;
	  double lat, lng;

	public String getGeokey() {
		if(geokey==null)
			setGeokey();
		return geokey;
	}

	public void setGeokey() {
		Geohash geohash= new Geohash();
		geokey= geohash.encode(this.getLat(), this.getLng()); 
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public void setLat(String txt) {
		DecimalFormat df = new DecimalFormat();
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		symbols.setGroupingSeparator(' ');
		df.setDecimalFormatSymbols(symbols);
		
		try {
			this.lat = df.parse(txt).doubleValue();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public void setLng(String txt) {
		DecimalFormat df = new DecimalFormat();
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		symbols.setGroupingSeparator(' ');
		df.setDecimalFormatSymbols(symbols);
		
		try {
			this.lng = df.parse(txt).doubleValue();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	  
}
