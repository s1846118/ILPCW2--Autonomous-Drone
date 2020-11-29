package uk.ed.ac.inf.aqmaps;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import com.mapbox.geojson.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import org.jgrapht.graph.*;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.tour.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class App {
	
    public static void main( String[] args ) throws IOException, InterruptedException{
    	
    	//Step 1 - Make a WebServer object to connect to the local port.
    	WebServer web = new WebServer(Integer.parseInt(args[6]));
    	
    	//Step 2 - Collect the relevant files. No fly zones and maps file for the given day. Reminder argument goes like "YEAR/MONTH/DAY/"
    	String path_maps = args[2] + "/" + args[1] + "/" + args[0] + "/";
    	var maps = web.getMaps(path_maps); 	   	
    	var buildings = web.getBuiding();
    	
    	//Step 3 - Create the array list of type sensor 
    	var sensors = App.getSensors(maps, web);
    
    	//Step 4 - Create drone object with 150 moves and starting location specified in arguments 
    	int moves = 150;
    	Point startingLoc = Point.fromLngLat(Double.parseDouble(args[3]), Double.parseDouble(args[4]));
    	Drone drone = new Drone(startingLoc, moves);
    	
    	//Step 5 - Call the algorithm class passing our drone in starting state and list of sensors to visit on given day. 
    	//TODO
    	
    	//Step 6 - Use the sensors array plot the air quality readings of each sensor on our geojson mapping. 
    	//TODO
    	
    	//Step 7 - Use the flight log in the drone at end of journey state to write 'flightpath.txt' 
    	//TODO
    	
    	Algorithm algo = new Algorithm(drone, sensors, buildings);
    	
    	DefaultUndirectedWeightedGraph<Sensor, DefaultEdge> graph = algo.makeGraph(sensors);
    	
    	//Make geojson string out of this...
    	GraphPath<Sensor, DefaultEdge> gPath = algo.getPath(graph);
    	List<Sensor> path = gPath.getVertexList();
    	
    	for(Sensor sensor : path) {
    		System.out.println(sensor.location);
    	}
    	
    	System.out.print(gPath.getWeight());
    	
    	
    }
    
    //Getter for sensors for the given day.
    public static ArrayList<Sensor> getSensors(ArrayList<Maps> maps, WebServer web) throws IOException, InterruptedException{
    	
    	var sensors = new ArrayList<Sensor>();
    	
    	//For loop that creates sensors with maps attributes and corresponding centre point in words.
    	for(Maps map : maps) {
    		//Split into separate words.
    		String[] words = map.location.split("\\.");
    		//Returns words object for the corresponding sensor.
    		Words corrWords = web.getWords(words[0] + "/" + words[1] + "/" + words[2]);
    		//Creates corresponding sensor
    		Point centre = Point.fromLngLat(corrWords.coordinates.lng, corrWords.coordinates.lat);
    		sensors.add(new Sensor(map.location, map.battery, map.reading, centre));
    	}
    	
    	return sensors;   	
    }
}
