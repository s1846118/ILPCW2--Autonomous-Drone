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
import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.traverse.*;

import java.io.*;
import java.net.*;
import java.util.*;



public class App {
	
    public static void main( String[] args ) throws IOException, InterruptedException{
    	
    	//Step 1 - Make a WebServer object to connect to the local port.
    	WebServer web = new WebServer(Integer.parseInt(args[6]));
    	
    	//You were testing the problem with step 2, for some reason it is no fetching maps data??? Try fetching using concrete example. Most likely a problem with webserver set up. 
    	System.out.print(args[0] + "/" + args[1] + "/" + args[2] + "/");
    	//Step 2 - Collect the relevant files. No fly zones and maps file for the given day. 
    	//var maps = web.getMaps(args[0] + "/" + args[1] + "/" + args[2] + "/");
    	
    	//Step 3 - Create the array list of type sensor 
    	//var sensors = App.getSensors(maps, web);
    	
    	//Step 4 - Create drone object with 150 moves and starting location specified in arguments 
    	//int moves = 150;
    	//Point startingLoc = Point.fromLngLat(Double.parseDouble(args[3]), Double.parseDouble(args[4]));
    	//Drone drone = new Drone(startingLoc, moves);
    	
    	
    	/*
    	Drone drone = new Drone(Point.fromLngLat(-3.1847428, 55.945936), 150, "slips.mass.bacon");
    	System.out.print(drone.getPosition());
    	drone.move(90);
    	System.out.print(drone.getPosition());
    	
    	
    	Graph<String, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);
    	
    	g.addVertex("a");
    	g.addVertex("c");
    	g.addVertex("b");
    	g.addVertex("d");
    	
    	g.addEdge("a", "b");
    	g.addEdge("a", "d");
    	*/
    	
    }
    
    //Getter for sensors for the given day.
    public static ArrayList<Sensor> getSensors(ArrayList<Maps> maps, WebServer web) throws IOException, InterruptedException{
    	
    	var sensors = new ArrayList<Sensor>();
    	
    	//For loop that creates sensors with maps attributes and corresponding centre point in words.
    	for(Maps map : maps) {
    		//Split into separate words.
    		String[] words = map.location.split(".");
    		//Returns words object for the corresponding sensor.
    		Words corrWords = web.getWords(words[0] + "/" + words[1] + "/" + words[2]);
    		//Creates corresponding sensor
    		Point centre = Point.fromLngLat(corrWords.coordinates.lng, corrWords.coordinates.lat);
    		sensors.add(new Sensor(map.location, map.battery, map.reading, centre));
    	}
    	
    	return sensors;
    	
    }
       
}
