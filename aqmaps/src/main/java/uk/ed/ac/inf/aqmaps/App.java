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
	
    public static void main( String[] args ) throws Exception{
    	
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
    	
    	//Make geojson string
    	ArrayList<Feature> pts = new ArrayList<>();
    	ArrayList<Point> listPts = new ArrayList<>();
    
    	for(Sensor sensor : path) {

    		//We will plot this in the geojson.io
    		listPts.add(sensor.lnglat);    		
    		
    		Point point = sensor.lnglat;
    		//Cast sensor to geometry 
    		Geometry g = (Geometry)point;
    		//Cast to feature
    		Feature f = Feature.fromGeometry(g);
    		
    		//Adding the correct properties ie symbol, colour, location...
    		f.addStringProperty("location", sensor.location);
    		//To add the following properties correctly we must make some comparisons.    		
    		
    		Double battery = sensor.battery;
    		
			if(battery <= 10) {
				f.addStringProperty("marker-symbol", "cross");
				f.addStringProperty("rgb-string" , "000000");
				f.addStringProperty("marker-color" , "000000");
				f.addStringProperty("fill" , "#000000");
				pts.add(f);
				continue;
			}
    		
    		Double reading = Double.parseDouble(sensor.reading);
    		
			if(reading < 0 || reading >= 256) {
				throw new Exception("reading is out of the range 0 <= x < 256!");				
			}
			
			//Following if statements place polygon in specific air quality range. We have divided into two cases
			//with subcases and continue statements to keep comparisons low.
			if(0 <= reading && reading < 128) {
				//Lighthouse symbol
				f.addStringProperty("marker-symbol", "lighthouse");
				
				if(reading < 32) {
					f.addStringProperty("rgb-string" , "00ff00");
					f.addStringProperty("marker-color" , "00ff00");
					f.addStringProperty("fill" , "#00ff00");				
					pts.add(f);
					continue;
				}
				if(reading < 64) {
					f.addStringProperty("rgb-string" , "40ff00");
					f.addStringProperty("marker-color" , "40ff00");
					f.addStringProperty("fill" , "#40ff00");		
					pts.add(f);				
					continue;
				}
				if(reading < 96) {
					f.addStringProperty("rgb-string" , "80ff00");
					f.addStringProperty("marker-color" , "80ff00");
					f.addStringProperty("fill" , "#80ff00");	
					pts.add(f);					
					continue;
				}
				else {
					f.addStringProperty("rgb-string" , "c0ff00");
					f.addStringProperty("marker-color" , "c0ff00");
					f.addStringProperty("fill" , "#c0ff00");	
					pts.add(f);				
					continue;
				}
			}
			
			if(reading >= 128 && reading < 256) {
				f.addStringProperty("marker-symbol", "danger");
				if(reading < 160) {
					f.addStringProperty("rgb-string" , "ffc000");
					f.addStringProperty("marker-color" , "ffc000");
					f.addStringProperty("fill" , "#ffc000");
					pts.add(f);					
					continue;
				}
				if(reading < 192) {
					f.addStringProperty("rgb-string" , "ff8000");
					f.addStringProperty("marker-color" , "ff8000");
					f.addStringProperty("fill" , "#ff8000");
					pts.add(f);				
					continue;
				}
				if(reading < 224) {
					f.addStringProperty("rgb-string" , "ff4000");
					f.addStringProperty("marker-color" , "ff4000");
					f.addStringProperty("fill" , "#ff4000");
					pts.add(f);					
					continue;
				}
				else {
					f.addStringProperty("rgb-string" , "ff0000");
					f.addStringProperty("marker-color" , "ff0000");
					f.addStringProperty("fill" , "#ff0000");
					pts.add(f);			
					continue;
				}
			}			
    	}
    	
    	//Line string to be added to feature collection below 
    	LineString lineStr = LineString.fromLngLats(listPts);
    	Geometry lineStringGeo = (Geometry)lineStr;
    	Feature fLnStr = Feature.fromGeometry(lineStringGeo);
    	pts.add(fLnStr);
    	
    	//Create geojson file.
    	FeatureCollection fc = FeatureCollection.fromFeatures(pts);
    	String jsonString = fc.toJson();
    	
		//Creating our GeoJson file 
		File geojsonfile = new File("readings.geojson");
		geojsonfile.createNewFile();


		//Writing GeoJson formatted string into above file
		FileWriter jsonWriter = new FileWriter("readings.geojson");
		jsonWriter.write(jsonString);
		jsonWriter.close();
    	
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
