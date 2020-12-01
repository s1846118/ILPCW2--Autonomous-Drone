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
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
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


public class Algorithm {
	
	//Drone for measuring aq sensors and list of sensors we want to visit
	private ArrayList<Sensor> sensors;
	private ArrayList<Polygon> noFlyZones;
	public Drone drone;
	public List<Point> droneLine = new ArrayList<Point>();;
	
	public Algorithm(Drone drone, ArrayList<Sensor> sensors, ArrayList<Polygon> noFlyZone) {
		this.sensors = sensors;
		this.noFlyZones = noFlyZone;
		this.drone = drone;
	}
	
	//Below we define getters for all of our needed global variables
	public ArrayList<Sensor> getSensors(){
		return this.sensors;
	}
	
	public ArrayList<Polygon> getZones(){
		return this.noFlyZones;
	}
	
	public Drone getDrone() {
		return this.drone;
	}
	
	
	/*
	 * Step 1 - Creating the graph and assigning weighted edges either Euclidean distance or infinity. 
	 * This step uses the 'doesIntersect' function as seen under the graph making function. We may use this in other parts of out project.
	 */
	public DefaultUndirectedWeightedGraph<Sensor, DefaultEdge> makeGraph(ArrayList<Sensor> sensors){
		
		//The graph we will use
		DefaultUndirectedWeightedGraph<Sensor, DefaultEdge> graph = new DefaultUndirectedWeightedGraph(DefaultEdge.class);

		//Adding each sensor as a node
		for(Sensor node : sensors){
			graph.addVertex(node);
		}
		
		//Adding weighted edges
		for(Sensor node : sensors) {
			for(Sensor node2 : sensors) {
				//We do not want any loops.
				if(node.equals(node2)) continue;
				//If straight line between points intersects a no-fly zone we assign edge weight infinity. 
				else if(doesIntersect(noFlyZones, node, node2)) {
					graph.addEdge(node, node2);
					graph.setEdgeWeight(node, node2, Double.POSITIVE_INFINITY);
				}
				//Otherwise we just set the weight to the Euclidean distance between the nodes. 
				else {
					graph.addEdge(node, node2);
					List<Double> pt1 = node.lnglat.coordinates();
					List<Double> pt2 = node2.lnglat.coordinates();
					//Weight
					double weight = Point2D.distance(pt1.get(0), pt1.get(1), pt2.get(0), pt2.get(1));
					//Set weight
					graph.setEdgeWeight(node, node2, weight);
					
				}
			}
		}
		
		return graph;
	}
	
    public static boolean doesIntersect(ArrayList<Polygon> zones, Sensor sensor1, Sensor sensor2) {
    	//Initialise the boolean.
    	boolean intersects = false;
    	//For each polygon in the no fly zones
    	for(Polygon zone : zones) {
	    	//This is the list of points within the given no fly zone. We get the 0 index because this gives us the outer co-ordinates of the zone. 
	    	List<Point> corners = zone.coordinates().get(0);
	    	//Get the longitude and latitude of the sensors
	    	Point pt1 = sensor1.lnglat;
	    	Point pt2 = sensor2.lnglat;
	    	//We want to make 2D points out of these to make the line segment. 
	    	Point2D vertex1 = new Point2D.Double(pt1.longitude(), pt1.latitude());
	    	Point2D vertex2 = new Point2D.Double(pt2.longitude(), pt2.latitude());
	    	//We now want to make a line segment from the two sensor locations to check if it intersects with the polygon.
	    	Line2D vLine = new Line2D.Double(vertex1, vertex2);
	    	//Now we check for each edge of the polygon if their is an intersection.
	    	for(int i = 0; i < corners.size() - 1; i++) {
	    		//Iterating through each corner and its corresponding corner at other end of edge.
	        	Point2D corner1 = new Point2D.Double(corners.get(i).longitude(), corners.get(i).latitude());
	        	Point2D corner2 = new Point2D.Double(corners.get(i+1).longitude(), corners.get(i+1).latitude());
	        	//Make the edge in 2D space
	        	Line2D edge = new Line2D.Double(corner1, corner2);       	
	        	//Now do the big check that we have all been waiting for! Does the line intersect? Does it??!!??       	
	        	if(vLine.intersectsLine(edge) || edge.intersectsLine(vLine)) {
	        		intersects = true; 
	        	}
	    	}
    	}
    	return intersects;
    }
    
    /*
     * Step 2 - This step finds a good order to visit the sensors using the 2-opt heuristic algorithm.
     * Note that the sensor at index 0 does not necessarily mean we begin visiting this sensor. What we are interested about
     * with this list is the order it is in. Ie if we begin visiting path[i] then next we should visit path[i+1] then path[i+2]
     */
	public GraphPath<Sensor, DefaultEdge> getPath(DefaultUndirectedWeightedGraph<Sensor, DefaultEdge> graph){
		
		//Run the 2-opt algorithm to get our path
		var twoOpt = new TwoOptHeuristicTSP();
		var tour = twoOpt.getTour(graph);
		//Convert to list for convenience 
		var path = tour.getVertexList();
		//Remove the last element as it is a duplicate of the first. 
		int size = path.size();
		//path.remove(size-1);

				
		return tour;
	}
	
	/*
	 * Step 3 - This is the biggest step which involves us actually moving the drone around the map. 
	 */
	//This function finds the nearest sensor to the starting location to start our journey.
	public int nearestSensor(Point startingLoc, List<Sensor> path) {
		//Euclidean distances from each sensor to the starting position.
		ArrayList<Double> distances = new ArrayList<>();
		List<Double> pt1 = startingLoc.coordinates();
		
		//Find distance to each sensor. 
		for(Sensor sensor : path) {
			List<Double> pt2 = sensor.lnglat.coordinates();
			double dist = Point2D.distance(pt1.get(0), pt1.get(1), pt2.get(0), pt2.get(1));
			distances.add(dist);			
		}
		//This gives us the index of the sensor to move to first.
		double min = Collections.min(distances);
		int index = distances.indexOf(min);
		
		return index;
	}
	
	//This function finds the exact angle from the drone to the sensor
	public double findAngle(Point dronePos, Point sensorPos) {
		double y1 = dronePos.latitude();
		double x1 = dronePos.longitude();
		
		double y2 = sensorPos.latitude();
		double x2 = sensorPos.longitude();
		
		//initialise x and y
		double x = Math.abs(x1 - x2);
		double y = Math.abs(y1 - y2); 
		
		//Are we on the same latitude?
		if(y1 == y2) {
			if(x1 < x2) {
				//Sensor must be exactly east.
				return 0;
			}
			if(x1 > x2) {
				//Sensor must be exactly west.
				return 180;
			}
		}
		//Are we on the same longitude
		if(x1 == x2) {
			//Sensor must be exactly north
			if(y1 < y2) {
				return 90;
			}
			//Sensor must be exactly south
			if(y2 < y1) {
				return 270;
			}
		}
		//Quadrant 1
		if((x1 < x2) & (y1 < y2)) {
			//System.out.println("Quadrant 1");
			return Math.atan2(y, x) * 180 / Math.PI;
		}
		//Quadrant 2
		if((x2 < x1) & (y1 < y2)) {
			//System.out.println(Math.atan2(y, x) * 180 / Math.PI);
			return 180 - (Math.atan2(y, x) * 180 / Math.PI);
		}
		//Quadrant 3
		if((x2 < x1) & (y2 < y1)) {
			return 180 + (Math.atan2(y, x) * 180 / Math.PI);
		}
		//Quadrant 4
		else{
			//System.out.println("Quadrant 4");
			return 360 - (Math.atan2(y, x) * 180 / Math.PI);
		}
	}
	//This function is responsible for moving the drone around the desired path by making small repetative moves. 
	public void fly(List<Sensor> path) throws Exception {
		//This gives us the index of the sensor we should go to first. Because it is closest. 
		int first_index = nearestSensor(drone.startingPosition, path);
		int move_num = 0;
		//Initialisation 
		String w3w = "";
		//We want to loop through (and connect to) each sensor in the path. We do not necessarily start at path[0] so we use mod function.
		//This loop is responsible for moving through every sensor. We do not make it out the loop without error otherwise. 
		for(int i = 0; i < path.size(); i++) {
			Sensor next_sensor = path.get((first_index + i) % (path.size()));
			
			//These are the repetitive moving steps we make to get to the desired location. 
			//We make these steps until we reach the location. 
			while(!(drone.isConnected(next_sensor, drone.getPosition()))){
				
				//Get exact angle to the sensor
				double angle = findAngle(drone.getPosition(), next_sensor.lnglat);
				//Round this to the nearest ten
				double angle_nearest_ten = Math.round(angle/10.0)*10;
				//Move at this angle
				Point prev_loc = drone.getPosition();
				
				//Adding to the testing line string to see our drones horrific path
				droneLine.add(prev_loc);
				
				drone.move(angle_nearest_ten);
				Point curr_loc = drone.getPosition();
				
				//Adding to line string for testing
				droneLine.add(curr_loc);
				
				//Made a move
				move_num+=1;
				//After our move the drone may well be connected to a sensor. We must check this
				boolean connected_now = drone.isConnected(next_sensor, drone.getPosition());
				if(connected_now) {w3w = next_sensor.location;}
				else {w3w = "null";}
				//Lets now add this to the flight path
				drone.addFlightPath(move_num, prev_loc, angle_nearest_ten, curr_loc, w3w);
				
			}
		}
	}	
}






















