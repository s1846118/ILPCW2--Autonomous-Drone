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

public class Drone {

	//Important attributes to define drones position and moves left. 
	private Point position;
	private int moves;
	
	//I chose this to be public so I didn't have to make a getter for it.
	public final Point startingPosition;
	
	//Constructor for our drone class.
	public Drone(Point startingPosition, int moves) {		
		this.position = startingPosition;
		this.moves = moves;
		this.startingPosition = startingPosition;
	}	
	public Point getPosition() {
		return this.position;
	}
	
	public int getMoves() {
		return this.moves;
	}
	
	//Move must be of 0.0003 degrees. Direction is an int since it must be a multiple of ten 
	public void move(int direction) {
		
		//Current lng,lat
		double curr_lng = this.position.longitude();
		double curr_lat = this.position.latitude();
		//Change in lng and lat using basic trig. I have made this absolute value as a mathematical convinience :)
		double lng_change = Math.abs(Math.cos(Math.PI*direction/180))*0.0003;
		double lat_change = Math.abs(Math.sin(Math.PI*direction/180))*0.0003;
		//lng and lat after move initialised 
		double lng = 0;
		double lat = 0;
		
		//Manage illegal arguments 
		if(direction % 10 != 0 || direction < 0 || direction > 350) {
			throw new IllegalArgumentException("Direction argument must be a multiple of 10 in the range 0 <= direction <= 350!");
		}
		
		//We must decide if we are increasing/decreasing the lng and lat depending on which direction we move. 
		//Top right quadrant, lng decreases and lat increases
		if(direction >= 0 && direction <= 90) {
			lng = curr_lng - lng_change;
			lat = curr_lat + lat_change;
		}
		//Top left quadrant, lng increases, lat increases
		if(direction <= 180 && direction >= 90) {
			lng = curr_lng + lng_change;
			lat = curr_lat + lat_change;
		}
		//Bottom left quadrant, lng increases, lat decreases
		if(direction <= 270 && direction >= 180) {
			lng = curr_lng + lng_change;
			lat = curr_lat - lat_change;
		}
		//Bottom right quadrant, lng decreases, lat decreases
		if(direction >= 270 && direction <= 350) {
			lng = curr_lng - lng_change;
			lat = curr_lat - lat_change; 
		}
		
		this.position = Point.fromLngLat(lng, lat);
	}
	
}
