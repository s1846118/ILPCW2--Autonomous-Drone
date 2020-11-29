package uk.ed.ac.inf.aqmaps;

import org.jgrapht.graph.*;

import org.jgrapht.alg.tour.*;
import java.util.Random;
import com.mapbox.geojson.*;



public class GraphPractice {
	
	public static void main(String[] args) {
		/*
		DefaultUndirectedWeightedGraph<Sensor, DefaultEdge> graph = new DefaultUndirectedWeightedGraph(DefaultEdge.class);
		
		var sensor1 = new Sensor("a", "20", "30", Point.fromLngLat(52, -3.323));
		var sensor2 = new Sensor("b", "21", "31", Point.fromLngLat(52.12, -3.323));
		var sensor3 = new Sensor("c", "22", "32", Point.fromLngLat(52.13, -3.323));
		var sensor4 = new Sensor("d", "23", "33", Point.fromLngLat(52.14, -3.323));
		var sensor5 = new Sensor("e", "24", "34", Point.fromLngLat(52.15, -3.323));
		
		
		Sensor[] sensors = {sensor1, sensor2, sensor3, sensor4, sensor5};
		
		for(Sensor node : sensors){
			graph.addVertex(node);
		}
		
		Random rand = new Random();
		
		//Assigning a weghted edge from and to each vertex in the tingggg
		for(Sensor node : sensors) {
			for(Sensor node2 : sensors) {
				
				//We don't have loops remember. 
				if(node.equals(node2)) continue;
				else {
					graph.addEdge(node, node2);
					graph.setEdgeWeight(node, node2, rand.nextInt(100));
				}
			}		
		}
		
		graph.removeEdge(sensor4,sensor5);
		graph.addEdge(sensor4, sensor5);
		graph.setEdgeWeight(sensor4, sensor5, Double.POSITIVE_INFINITY);
		
		var twoOpt = new TwoOptHeuristicTSP();
		var tour = twoOpt.getTour(graph);
		
		var vertex = tour.getVertexList();
		
		int x = vertex.size();
	
		for(int i = 0; i < x; i++) {
			
			Sensor s = (Sensor) vertex.get(i);
			System.out.println(s.location);
		}
		
		System.out.print("\n" + tour.getWeight());
		
		
		*/

	}
}
