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

//The reason we must make this class on top of 'maps' is that 'maps' does not give us enough info about longitude and latitude for our drone algo.
public class Sensor {

	//W3W location
	String location;
	//Battery level
	String battery;
	//Air quality reading
	String reading;
	//Centre point of sensor which our drone is going to aim for.
	Point lnglat;
	
	public Sensor(String location, String battery, String reading, Point lnglat) {
		this.location = location;
		this.battery = battery;
		this.lnglat = lnglat;		
	}
	
	
}
