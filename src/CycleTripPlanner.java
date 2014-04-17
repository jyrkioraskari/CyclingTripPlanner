import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.google.geohash.Geohash;

import fi.ni.HashBag;
import fi.ni.vo.PointVO;
import fi.ni.vo.StepVO;
/**
 * @author Jyrki Oraskari
 * @license This work is licensed under a Creative Commons Attribution 3.0 Unported License.
 * http://creativecommons.org/licenses/by/3.0/
 */


public class CycleTripPlanner {

	static public Random randomGenerator = new Random(
			System.currentTimeMillis());
	List<String> seed_locations = new ArrayList<String>();

	List<StepVO> steps_collection;

	Map<String, LinkedPointVO> location_map = new HashMap<String, LinkedPointVO>();
	Geohash geohash = new Geohash();
	PointVO home = new PointVO();

	// Map of geohash, description
	Map<String, String> location_description = new HashMap<String, String>();

	static public long global_routing_counter = 0;

	public CycleTripPlanner() {
		List<RouteVO> route_candidates = new ArrayList<RouteVO>();

		read_model();
		read_location_descriptions();
		// Vallikallio, Espoo
		home.setLat("60.2270000");
		home.setLng("24.8168700");
		home.setGeokey();

		// initialize();
		for (int n = 0; n < steps_collection.size(); n++) {
			handlePoint(steps_collection.get(n), steps_collection.get(n)
					.getP1());
			handlePoint(steps_collection.get(n), steps_collection.get(n)
					.getP2());
		}

		get_the_solution(route_candidates);

		save_location_descriptions();
	}

	private int get_the_best_solution_statistics(List<RouteVO> route_candidates) {
		HashBag hb=new HashBag();
		int best_now = Integer.MAX_VALUE;
		for (int j = 0; j < 100; j++) {
			route_candidates.clear();
			for (int n = 0; n < 1000; n++) {
				RouteVO route = generate_route();
				if (route.getDistance() > 1000) // filter very short candidates
												// (shorter than 10 km
					if (route.getVariation() > .35) // minimum variation filter
						route_candidates.add(route);
			}
			if(route_candidates.size()<3)
				continue;
			Collections.sort(route_candidates);
			for (int i = 0; i < 100; i++) {
				if(route_candidates.size()<3)
					continue;

				List<RouteVO> new_route_candidates = new ArrayList<RouteVO>();

				// Elite
				RouteVO elite = route_candidates.get(0);
				new_route_candidates.add(elite);

				for (int n = 0; n < 20; n++) {
					int first = CycleTripPlanner.randomGenerator
							.nextInt(route_candidates.size() / 2) + 1;
					int second = CycleTripPlanner.randomGenerator
							.nextInt(route_candidates.size() / 2) + 1;

					int probability = CycleTripPlanner.randomGenerator
							.nextInt(100);
					if (probability > 80) {
						List<RouteVO> crossed = makeCrossower(
								route_candidates.get(first),
								route_candidates.get(second));
						new_route_candidates.add(crossed.get(0));
						new_route_candidates.add(crossed.get(1));
					} else {

						int mutation_probability = CycleTripPlanner.randomGenerator
								.nextInt(100);
						if (mutation_probability > 80) {
							new_route_candidates.add(mutate(route_candidates
									.get(first)));
							new_route_candidates.add(mutate(route_candidates
									.get(second)));
						} else {
							new_route_candidates.add(route_candidates
									.get(first));
							new_route_candidates.add(route_candidates
									.get(second));

						}
					}
				}
				route_candidates = filter_valid(new_route_candidates);
				Collections.sort(route_candidates);
			}
			if(route_candidates.size()<1)
				continue;
			hb.add(route_candidates.get(0).getGrade());
			if(route_candidates.get(0).getGrade()<best_now)
				best_now=route_candidates.get(0).getGrade();
		}
		hb.list_result();
		return best_now;
	}

	private void get_the_solution(List<RouteVO> route_candidates) {
		for (int n = 0; n < 1000; n++) {
			RouteVO route = generate_route();
			if (route.getDistance() > 1000) // filter very short candidates
											// (shorter than 10 km
				if (route.getVariation() > .35) // minimum variation filter
					route_candidates.add(route);
		}
		Collections.sort(route_candidates);

		for (int n = 0; n < route_candidates.size(); n++) {
			route_candidates.get(n).showThePath();
		}
		if(route_candidates.size()<3)
			return;

		for (int i = 0; i < 100; i++) {
			if(route_candidates.size()<3)
				continue;

			List<RouteVO> new_route_candidates = new ArrayList<RouteVO>();

			// Elite
			RouteVO elite = route_candidates.get(0);
			new_route_candidates.add(elite);

			for (int n = 0; n < 20; n++) {
				
				int first = CycleTripPlanner.randomGenerator
						.nextInt(route_candidates.size() / 2) + 1;
				int second = CycleTripPlanner.randomGenerator
						.nextInt(route_candidates.size() / 2) + 1;

				int probability = CycleTripPlanner.randomGenerator.nextInt(100);
				if (probability > 80) {
					List<RouteVO> crossed = makeCrossower(
							route_candidates.get(first),
							route_candidates.get(second));
					new_route_candidates.add(crossed.get(0));
					new_route_candidates.add(crossed.get(1));
				} else {

					int mutation_probability = CycleTripPlanner.randomGenerator
							.nextInt(100);
					if (mutation_probability > 80) {
						new_route_candidates.add(mutate(route_candidates
								.get(first)));
						new_route_candidates.add(mutate(route_candidates
								.get(second)));
					} else {
						new_route_candidates.add(route_candidates.get(first));
						new_route_candidates.add(route_candidates.get(second));

					}
				}
			}
			route_candidates = filter_valid(new_route_candidates);
			System.out.println("\n ------------");
			Collections.sort(route_candidates);
			for (int n = 0; n < route_candidates.size(); n++) {
				route_candidates.get(n).showThePath();
			}
		}
		if(route_candidates.size()<1)
			return;

		describe_the_winner(route_candidates.get(0));
	}

	private void describe_the_winner(RouteVO route) {

		System.out.println("\n\nThe route description:");
		String previous_description = "";
		for (int n = 0; n < route.geohashes.size(); n++) {
			String desc = fetch_location_description(route.geohashes.get(n));
			if (!previous_description.equals(desc))
				System.out.println(desc);
			previous_description = desc;
		}
	}

	private List<RouteVO> filter_valid(List<RouteVO> route_candidates) {
		List<RouteVO> return_list = new ArrayList<RouteVO>();
		for (int n = 0; n < route_candidates.size(); n++) {
			RouteVO route = route_candidates.get(n);
			if (route.getDistance() > 150000) // more than 150 km
			{
				List<String> steps = route.getGeohashes();
				if (home.getGeokey().equals(steps.get(0)))
					if (home.getGeokey().equals(steps.get(steps.size() - 1)))
						return_list.add(route);

			}
		}
		return return_list;
	}

	private RouteVO mutate(RouteVO original) {
		String key = home.getGeokey();
		LinkedPointVO home_point = location_map.get(key);
		if (home_point == null) {
			System.err.println("No home point set.");
			return null; // Should not happen
		}

		ArrayList<String> empty = new ArrayList<String>();
		ArrayList<String> start = new ArrayList<String>();
		List<String> original_route = original.getGeohashes();
		int position = CycleTripPlanner.randomGenerator.nextInt(original_route
				.size() - 1) + 1;
		String current = key;
		for (int n = 0; n < position; n++) {
			start.add(original_route.get(n));
			current = original_route.get(n);
		}
		LinkedPointVO current_point = location_map.get(current);
		ArrayList<String> end = recurse_route(current_point, home_point, 0,
				empty);

		start.addAll(end);
		return new RouteVO(start, location_map);
	}

	private List<RouteVO> makeCrossower(RouteVO first, RouteVO second) {
		List<RouteVO> return_list = new ArrayList<RouteVO>();
		Set<String> set = new HashSet<String>();
		set.addAll(first.getGeohash_set());
		set.retainAll(second.getGeohash_set());
		set.remove(home.getGeokey());
		if (set.size() > 0) {

			int key_position = CycleTripPlanner.randomGenerator.nextInt(set
					.size());
			String pos_key = (String) set.toArray()[key_position];
			int position1 = first.getGeohashes().indexOf(pos_key);
			int position2 = second.getGeohashes().indexOf(pos_key);

			ArrayList<String> list1 = new ArrayList<String>();
			ArrayList<String> list2 = new ArrayList<String>();
			for (int n = 0; n < position1; n++) {
				list1.add(first.getGeohashes().get(n));
			}
			for (int n = 0; n < position2; n++) {
				list2.add(second.getGeohashes().get(n));
			}

			for (int n = position2; n < second.getGeohashes().size(); n++) {
				list1.add(second.getGeohashes().get(n));
			}

			for (int n = position1; n < first.getGeohashes().size(); n++) {
				list2.add(first.getGeohashes().get(n));
			}

			return_list.add(new RouteVO(list1, location_map));
			return_list.add(new RouteVO(list2, location_map));

		} else {
			// No modification, since no common points
			return_list.add(first);
			return_list.add(second);
		}
		return return_list;
	}

	private RouteVO generate_route() {
		String key = home.getGeokey();
		LinkedPointVO home_point = location_map.get(key);
		if (home_point == null) {
			System.err.println("No home point set.");
			return null; // Should not happen
		}
		ArrayList<String> empty = new ArrayList<String>();
		empty.add(key);
		ArrayList<String> route = recurse_route(home_point, home_point, 0,
				empty);
		return new RouteVO(route, location_map);
	}

	private ArrayList<String> recurse_route(LinkedPointVO current,
			LinkedPointVO home_point, int counter, ArrayList<String> route) {
		if ((current == home_point) && counter > 1)
			return route;
		if (counter > 5000)
			return route;
		StepVO step = current.getRandomLink();
		PointVO next_point = step.getOtherEnd(current.getMe());

		String key = next_point.getGeokey();
		current = location_map.get(key);
		if (current == null)
			return null;
		route.add(key);
		return recurse_route(current, home_point, counter + 1,
				(ArrayList<String>) route.clone());
	}

	private void handlePoint(StepVO step, PointVO p) {
		String key = p.getGeokey();
		LinkedPointVO mapped_p = location_map.get(key);
		if (mapped_p == null) {
			mapped_p = new LinkedPointVO(p);
			location_map.put(key, mapped_p);
		}

		mapped_p.addStep(step);
	}

	private void initialize() {
		seed_locations.add("Vallikallio,Espoo");
		seed_locations.add("Lohja");
		seed_locations.add("Fiskars");
		seed_locations.add("Tuusula");
		seed_locations.add("Vihti");
		seed_locations.add("Otalampi");
		seed_locations.add("Karjalohja");
		seed_locations.add("Pohja");
		seed_locations.add("Porvoo");
		seed_locations.add("Fagervik");
		seed_locations.add("Inkoo");
		seed_locations.add("Karjaa");
		seed_locations.add("Viherlaakso,Espoo");
		seed_locations.add("Kauniainen");
		seed_locations.add("Sipoo");
		seed_locations.add("Kerava");
		seed_locations.add("Veikkola");
		seed_locations.add("Klaukkala");
		seed_locations.add("Kirkkonummi");
		seed_locations.add("Sammatti");
		seed_locations.add("Siuntio");
		seed_locations.add("Espoo");
		seed_locations.add("Otaniemi,Espoo");

		for (int i = 0; i < (seed_locations.size() - 1); i++)
			for (int j = i; j < seed_locations.size(); j++) {
				fetch_directions(seed_locations.get(i), seed_locations.get(j));
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
	}

	@SuppressWarnings("unchecked")
	private void read_model() {

		FileInputStream fis;
		try {
			fis = new FileInputStream("c:\\data\\bike_trip_data.xml");
			BufferedInputStream bis = new BufferedInputStream(fis);
			XMLDecoder xmlDecoder = new XMLDecoder(bis);
			steps_collection = (ArrayList<StepVO>) xmlDecoder.readObject();
		} catch (FileNotFoundException e) {
			steps_collection = new ArrayList<StepVO>();
		}

	}

	private void save_model() {
		XMLEncoder e;
		try {
			e = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(
					"c:\\data\\bike_trip_data.xml")));
			e.writeObject(steps_collection);
			e.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	private void read_location_descriptions() {

		FileInputStream fis;
		try {
			fis = new FileInputStream("c:\\data\\location_data.xml");
			BufferedInputStream bis = new BufferedInputStream(fis);
			XMLDecoder xmlDecoder = new XMLDecoder(bis);
			location_description = (Map<String, String>) xmlDecoder
					.readObject();
		} catch (FileNotFoundException e) {
			location_description = new HashMap<String, String>();
		}

	}

	private void save_location_descriptions() {
		XMLEncoder e;
		try {
			e = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(
					"c:\\data\\location_data.xml")));
			e.writeObject(location_description);
			e.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	private void parse_directions(String google_result) {
		XMLReader xr;
		try {
			xr = XMLReaderFactory.createXMLReader();
			DirectionsSAXParser handler = new DirectionsSAXParser();
			xr.setContentHandler(handler);
			xr.setErrorHandler(handler);

			InputStream xmlStream = new ByteArrayInputStream(
					google_result.getBytes("UTF-8"));
			xr.parse(new InputSource(xmlStream));
			List<StepVO> step_list = handler.getStep_list();
			this.steps_collection.addAll(step_list);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return;
	}

	private void fetch_directions(String location1, String location2) {
		try {
			String encoded_loc1 = java.net.URLEncoder.encode(location1,
					"ISO-8859-1");
			String encoded_loc2 = java.net.URLEncoder.encode(location2,
					"ISO-8859-1");

			String search_string = "http://maps.googleapis.com/maps/api/directions/xml?origin="
					+ encoded_loc1
					+ "&destination="
					+ encoded_loc2
					+ "&mode=bicycling&language=FI&sensor=false";
			System.out.println(search_string);

			parse_directions(httpGET(search_string));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	private String parse_location_description(String http_result) {
		XMLReader xr;
		try {
			xr = XMLReaderFactory.createXMLReader();
			DescriptionsSAXParser handler = new DescriptionsSAXParser();
			xr.setContentHandler(handler);
			xr.setErrorHandler(handler);

			InputStream xmlStream = new ByteArrayInputStream(
					http_result.getBytes("UTF-8"));
			xr.parse(new InputSource(xmlStream));
			return handler.getDescription();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private String fetch_location_description(String geohash) {
		String description = location_description.get(geohash);
		if (description != null)
			return description;
		try {
			Geohash h = new Geohash();
			double d[] = h.decode(geohash);
			if (geohash == null)
				return "";
			String search_string = "http://nominatim.openstreetmap.org/reverse?format=xml&lat="
					+ d[0] + "&lon=" + d[1] + "&zoom=18&addressdetails=1";
			description = parse_location_description(httpGET(search_string));
			location_description.put(geohash, description);
			return description;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return description;
	}

	private String httpGET(String urlString) {
		StringBuffer result = new StringBuffer();
		BufferedReader reader = null;
		URL url;
		try {
			url = new URL(urlString);
			URLConnection connection = url.openConnection();
			reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result.toString();
	}

	public static void main(String[] args) {
		new CycleTripPlanner();
	}

}
