package goeuro;

import static spark.Spark.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.json.JSONObject;

public class BusRoute {

	private static Map<String, String> routes;
	
	public static void main(String[] args) {
		
		try {
			processInput(args[0]);
		} catch (IOException e) {
			System.err.println("Error processing input file");
			e.printStackTrace();
			return;
		}
		
		port(8088);
		get("/api/direct", (req, res) -> { 
			String depId = req.queryParams("dep_sid");
			String arrId = req.queryParams("arr_sid");
			
			res.type("application/json");
			
			JSONObject result = new JSONObject();
			result.put("dep_sid", depId);
			result.put("arr_sid", arrId);
			result.put("direct_bus_route", isDirectRoute(depId, arrId));
			
			return result;
		});
	}
	
	private static void processInput(String path) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(path));
		// No of routes not required with this approach 
		lines.remove(0);
		routes = new HashMap<String, String>();
		
		for (String line : lines) {
			routes.put(line.substring(0, 1), line.substring(1).trim());
		}
	}
	
	private static boolean isDirectRoute(String depId, String arrId) {
		String regex = "(^.*)?(^|\\s)" + depId + "\\s(.*\\s)?" + arrId + "(\\s|$)(.*$)?";
		
		for (String route : routes.values()) {
			if (Pattern.matches(regex, route)) {
				return true;
			}
		}
		
		return false;
	}
}
