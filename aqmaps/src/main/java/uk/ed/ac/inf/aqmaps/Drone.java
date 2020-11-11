package uk.ed.ac.inf.aqmaps;

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

public class Drone {

	//Important attributes to define drones position and moves left. 
	private Point position;
	private int moves;
	private String wordsLoc;
	//I chose this to be public so I didn't have to make a getter for it.
	public final Point startingPosition;
	
	//Constructor for our drone class.
	public Drone(Point startingPosition, int moves, String wordsLoc) {
		
		this.position = startingPosition;
		this.moves = moves;
		this.wordsLoc = wordsLoc;
		this.startingPosition = startingPosition;
	}
	
	public Point getPosition() {
		return this.position;
	}
	
	public int getMoves() {
		return this.moves;
	}
	
	public String getWords() {
		return this.wordsLoc;
	}
	
}
