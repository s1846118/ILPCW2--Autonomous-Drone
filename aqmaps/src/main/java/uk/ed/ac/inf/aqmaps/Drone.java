package uk.ed.ac.inf.aqmaps;

import java.net.URI;
import java.lang.Math;
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

public class Drone {

	//Important attributes to define drones position and moves left. 
	private Point position;
	private int moves;
	//Each line here will represent one line in 'flightpath.txt'.
	private ArrayList<String> flightLog;
	
	//I chose this to be public so I didn't have to make a getter for it.
	public final Point startingPosition;
	
	//Constructor for our drone class.
	public Drone(Point startingPosition, int moves) {		
		this.position = startingPosition;
		this.moves = moves;
		this.startingPosition = startingPosition;
		this.flightLog = new ArrayList<String>();
	}	
	public Point getPosition() {
		return this.position;
	}
	
	public int getMoves() {
		return this.moves;
	}
	
	//Move must be of 0.0003 degrees. Direction is an int since it must be a multiple of ten 
	public void move(double direction) throws Exception {
		
		if(moves <= 0) {
			//throw new Exception("Out of moves :(");
		}
		
		//Current lng,lat
		double curr_lng = this.position.longitude();
		double curr_lat = this.position.latitude();
		//Change in lng and lat using basic trig. I have made this absolute value as a mathematical convinience :)
		double lng_change = (Math.cos(Math.PI*direction/180))*0.0003;
		double lat_change = (Math.sin(Math.PI*direction/180))*0.0003;
		//lng and lat after move initialised 
		double lng = 0;
		double lat = 0;
		
		//Manage illegal arguments 
		if(direction % 10 != 0 || direction < 0 || direction > 360) {
			throw new IllegalArgumentException("Direction argument must be a multiple of 10 in the range 0 <= direction <= 350!");
		}
		lng = curr_lng + lng_change;
		lat = curr_lat + lat_change;
		//Change drone position
		this.position = Point.fromLngLat(lng, lat);
		//This uses up one move. 
		this.moves -=1;
	}
	
	//This simple function tells us if we are connected to a sensor. 
	public boolean isConnected(Sensor sensor, Point position) {
		List<Double> pt1 = sensor.lnglat.coordinates();
		List<Double> pt2 = position.coordinates();
		//Weight
		double range = Point2D.distance(pt1.get(0), pt1.get(1), pt2.get(0), pt2.get(1));
		
		//Connected or not connected that is the question.
		if(range <= 0.0002) {return true;}
		else {return false;}
	}
	
	//This function allows us to add a move to the flight path
	public void addFlightPath(int move_num, Point prev_loc, double angle_nearest_ten, Point curr_loc, String connected) {
		String move = "" + move_num + ",";
		String loc = "" + prev_loc.longitude() + "," + prev_loc.latitude() + ","; 
		String angle = "" + angle_nearest_ten + ",";
		String loc_ = "" + curr_loc.longitude() + "," + curr_loc.latitude() + ",";
		String w3w = connected + "\n";
		
		String line = move + loc + angle + loc_ + w3w;
		
		//System.out.println(line);
		
		flightLog.add(line);
	}
	
}
