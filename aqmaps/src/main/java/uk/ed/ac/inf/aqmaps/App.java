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
 
public class App {
	
    public static void main( String[] args ) throws IOException, InterruptedException{
    	
    	//Makes a webserver at specified port.
    	WebServer web = new WebServer(Integer.parseInt(args[6]));
    	
    }
    
    //Getter for sensors for the given day.
    public ArrayList<Sensor> getSensors(ArrayList<Maps> maps, WebServer web) throws IOException, InterruptedException{
    	
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
