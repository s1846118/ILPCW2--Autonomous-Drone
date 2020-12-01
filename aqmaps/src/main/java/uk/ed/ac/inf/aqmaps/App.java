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
	
	//Longitude LHS, Latitude RHS
	//This is the corners of the confinement zone for the drone labelled accordingly. They have been hard coded because zone stays constant.
	static Point northWest = Point.fromLngLat(-3.192473, 55.946233);
	static Point northEast = Point.fromLngLat(-3.184319, 55.946233 );
	static Point southWest = Point.fromLngLat(-3.192473, 55.942617);
	static Point southEast = Point.fromLngLat(-3.184319, 55.942617);
	
    public static void main( String[] args ) throws Exception{
    	
    	//Step 1 - Make a WebServer object to connect to the local port.
    	WebServer web = new WebServer(Integer.parseInt(args[6]));
    	
    	//Step 2 - Collect the relevant files. No fly zones and maps file for the given day. Reminder argument goes like "YEAR/MONTH/DAY/"
    	var path_maps = args[2] + "/" + args[1] + "/" + args[0] + "/";
    	var maps = web.getMaps(path_maps); 	   	
    	var buildings = web.getBuiding();
    	
    	//Step 3 - Create the array list of type sensor 
    	var sensors = App.getSensors(maps, web);
    	//Step 4 - Create drone object with 150 moves and starting location specified in arguments 
    	int moves = 150;
    	var startingLoc = Point.fromLngLat(Double.parseDouble(args[4]), Double.parseDouble(args[3]));
    	var drone = new Drone(startingLoc, moves);
    	
    	//Step 5 - Call the algorithm class passing our drone in starting state and list of sensors to visit on given day. Step 5 includes all subproblems relating to the algorithm.
    	var algo = new Algorithm(drone, sensors, buildings);
    	//Step 5b - Create the weighted graph to find path. 
    	DefaultUndirectedWeightedGraph<Sensor, DefaultEdge> graph = algo.makeGraph(sensors);
    	//Step 5c - Get the order in which we will visit the sensors. 
    	GraphPath<Sensor, DefaultEdge> gPath = algo.getPath(graph);
    	var path = gPath.getVertexList();
    	//Step 5d - Call the algorithms move method which will move the drone around the maps connecting to nodes and returned at end state.
    	algo.fly(path); 
    	
    	//Step 6 - Use the sensors array plot the air quality readings of each sensor on our geojson mapping. 
    	//TODO
    	
    	//Step 7 - Use the flight log in the drone at end of journey state to write 'flightpath.txt' 
    	//TODO    	       	    	
    	
    	//Make geojson string out of this...
    	
    	    	  	
    	
    	//Make geojson string. Can ignore this for now and put it in some function or something later on. 
    	ArrayList<Feature> pts = new ArrayList<>();
    	//ArrayList<Point> listPts = new ArrayList<>();
    	
    	//Line string representing the drones path. This will be added to the feature collection pts.
    	List<Point> listPtsDrone = algo.droneLine;
    	
    	LineString lineStrD = LineString.fromLngLats(listPtsDrone);
    	Geometry lineStringGeoD = (Geometry)lineStrD;
    	Feature fLnStrD = Feature.fromGeometry(lineStringGeoD);
    	pts.add(fLnStrD);
    	
    	
    
    	for(Sensor sensor : path) {

    		//We will plot this in the geojson.io it is the desired path 
    		//listPts.add(sensor.lnglat);    		
    		
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
    	//Adding the zones to geoJson string. 
    	for(Polygon building : buildings) {
    		Geometry b = (Geometry)building;
    		Feature bf = Feature.fromGeometry(b);
    		bf.addStringProperty("rgb-string", "ff0000");
    		bf.addStringProperty("fill", "#ff0000");
    		pts.add(bf);
    	}
    	
		//Make confinment zone
		//This is our single entry to our List<List<Point>> Required to make polygon
		var polyPoints = new ArrayList<Point>();
		polyPoints.add(northWest);
		polyPoints.add(southWest);
		polyPoints.add(southEast);
		polyPoints.add(northEast);
		
		//This is our List<List<Point>> we will use in order to create the polygon.
		var polyGList = new ArrayList<List<Point>>();
		polyGList.add(polyPoints);
		
		//Creating polygon
		Polygon pol = Polygon.fromLngLats(polyGList);
		Geometry polyG = (Geometry)pol;
		Feature polF = Feature.fromGeometry(polyG);
		polF.addStringProperty("fill-opacity", "0.0");
		pts.add(polF);
    	
		
    	//Line string to be added to feature collection below 
    	//LineString lineStr = LineString.fromLngLats(listPts);
		//Geometry lineStringGeo = (Geometry)lineStr;
    	//Feature fLnStr = Feature.fromGeometry(lineStringGeo);
    	//pts.add(fLnStr);
    	
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
