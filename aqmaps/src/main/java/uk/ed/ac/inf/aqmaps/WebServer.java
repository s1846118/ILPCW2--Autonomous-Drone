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
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

public class WebServer {
	
	//Make this a global variable because it will be used by all methods to access webserver.
	static HttpClient client;
	static int port;
	
	public WebServer(int port) {		
		this.client = HttpClient.newHttpClient();
		this.port = port;
	}
    
    //This function returns us an object containing the details of some 'Words' file.
    public Words getWords(String path) throws IOException, InterruptedException {
     	
    	//Standard steps to get the required json 'Words' file from the web.
    	var request = HttpRequest.newBuilder().uri(URI.create("http://localhost:" + this.port + "/words/"+ path + "/details.json")).build();  	
    	var response = client.send(request, BodyHandlers.ofString());   	
    	response.statusCode();
    	
    	//Json string.
    	String jsonString = response.body();
    	
    	//Deserialise the json file to 'Words' class.
    	var words = 
    			new Gson().fromJson(jsonString, Words.class); 
    	
    	return words;
    }
	
    public ArrayList<Polygon> getBuiding() throws IOException, InterruptedException {
    	
    	//Fethces the geo-json file for the no-fly zones from web.
    	var request = HttpRequest.newBuilder().uri(URI.create("http://localhost:" + this.port + "/buildings/no-fly-zones.geojson")).build();  	
    	var response = client.send(request, BodyHandlers.ofString());   	
    	response.statusCode();
    	
    	//GeoJson string.
    	String geoJsonString = response.body();
    	
    	//Below two steps create geojson string into list of features.
    	FeatureCollection fc = FeatureCollection.fromJson(geoJsonString);  	
    	var fcList = fc.features();
  
    	//Lines 62-72 downcast the Geometry elements in 'fcList' to corresponding Polygon objects in 'noFlyZones'
    	var noFlyZones = new ArrayList();
    	
    	for (int i=0 ; i<= (fcList.size()-1) ; i++) {
    		
    		Geometry g = fcList.get(i).geometry();
    		Polygon p = (Polygon)g;
    		
    		noFlyZones.add(p);
    	}
    	
    	return noFlyZones;
    	
    }
    
    //This is the daddy function of this object which returns the json file corresponding to the specific day of the year.
    public ArrayList<Maps> getMaps(String path) throws IOException, InterruptedException {
    	
    	//Fethces the geo-json file for the no-fly zones from web.
    	var request = HttpRequest.newBuilder().uri(URI.create("http://localhost:" + this.port + "/maps/" + path + "air-quality-data.json")).build();  	
    	var response = client.send(request, BodyHandlers.ofString());   	
    	response.statusCode();
    	
    	//GeoJson string.
    	String geoJsonString = response.body();
    	
    	Type listType =
    			new TypeToken<ArrayList<Maps>>() {}.getType();
    			
    	ArrayList<Maps> mapsList =
    			new Gson().fromJson(geoJsonString, listType);

    	return mapsList;
    	
    }
    
}
