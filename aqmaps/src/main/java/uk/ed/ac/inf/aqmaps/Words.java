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

public class Words {

	//Country belonging to words location
	String country;
	
	//Square defined by two points
	Square square;
	public static class Square{
		Pt southwest;
		public static class Pt{
			double lng;
			double lat;
		}
		
		Pt northeast;
	}
	
	//Below is the rest of the attributes of the specific 'Words' file.
	String nearestPlace;
	Pt coordinates;
	public static class Pt{
		double lng;
		double lat;
	}
	String words;	
	String language;	
	String map;
	
}
