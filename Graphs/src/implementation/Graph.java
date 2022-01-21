package implementation;

import java.util.*;

/**
 * Implements a graph. We use two maps: one map for adjacency properties 
 * (adjancencyMap) and one map (dataMap) to keep track of the data associated 
 * with a vertex. 
 * 
 * @author cmsc132
 * 
 * @param <E>
 */
public class Graph<E> {
	/* You must use the following maps in your implementation */
	private HashMap<String, HashMap<String, Integer>> adjacencyMap;
	private HashMap<String, E> dataMap;

	public Graph() {
		this.adjacencyMap = new HashMap(); 
		this.dataMap = new HashMap(); 
	}
	
	public void addVertex(String vertexName, E data) {
		dataMap.put(vertexName, data); 
	}
	
	public void addDirectedEdge(String startVertexName, String endVertexName, int cost) {
		if(adjacencyMap.containsKey(startVertexName)) {
			HashMap<String, Integer> map = adjacencyMap.get(startVertexName); 
			map.put(endVertexName, cost); 
		}
		else {
			adjacencyMap.put(startVertexName, new HashMap<String, Integer>()); 
			HashMap<String, Integer> map = adjacencyMap.get(startVertexName); 
			map.put(endVertexName, cost); 
		}
	}
	
	public String toString() {
		//Probably need to convert HashMaps to TreeMaps so that they are automatically sorted. 
		TreeMap<String, HashMap<String, Integer>> dataTree = new TreeMap(dataMap); 
		ArrayList<String> vertices = new ArrayList<String>(); 
		String edges = ""; 
		for(String vertexName : dataTree.keySet()) {
			vertices.add(vertexName); 
			if(adjacencyMap.get(vertexName) == null) {
				edges += "Vertex(" + vertexName + ")--->" + "{}\n"; 
			}
			else {
				edges += "Vertex(" + vertexName + ")--->" + adjacencyMap.get(vertexName) + "\n"; 
			}
		}
		String result = "Vertices: " + vertices + "\nEdges:\n" + edges; 
		return result; 
	}
	
	public Map<String, Integer> getAdjacentVertices(String vertexName){
		Map<String, Integer> result = adjacencyMap.get(vertexName); 
		return result; 
	}
	
	public int getCost(String startVertexname, String endVertexName) {
		Map<String, Integer> adjVertices = getAdjacentVertices(startVertexname); 
		int result = adjVertices.get(endVertexName); 
		return result; 
	}
	
	public Set<String> getVertices() {
		Set result = dataMap.keySet(); 
		return result; 
	}
	
	public E getData(String vertex) {
		E result = dataMap.get(vertex); 
		return result; 
	}
	
	public void doDepthFirstSearch(String startVertexName, CallBack<E> callback) {
		//DFS: visit all nodes on path, backtrack when path ends.
		ArrayList<String> visited = new ArrayList(); 
		Stack<String> discovered = new Stack<>(); 
		discovered.add(startVertexName); 
		while(!discovered.isEmpty()) {
			String X = discovered.pop(); 
			if(!visited.contains(X)) {
				visited.add(X); 
				callback.processVertex(X, getData(X));
				Map<String, Integer> successors = getAdjacentVertices(X); 
				for(String vertexName : successors.keySet()) {
					discovered.add(vertexName); 
				}
			}
		}
	}
	
	public void doBreadthFirstSearch(String startVertexName, CallBack<E> callback) {
		//BFS: visit all nodes at certain distance until all nodes are visited. 
		ArrayList<String> visited = new ArrayList(); 
		Queue<String> discovered = new LinkedList<>(); 
		discovered.add(startVertexName); 
		while(!discovered.isEmpty()) {
			String X = discovered.remove(); 
			if(!visited.contains(X)) {
				visited.add(X); 
				callback.processVertex(X, getData(X));
				Map<String, Integer> successors = getAdjacentVertices(X); 
				for(String vertexName : successors.keySet()) {
					discovered.add(vertexName); 
				}
			}
		}
	}
	
	public int doDijkstras(String startVertexName, String endVertexName, ArrayList<String> shortestPath) {
		//Functionality: Runs Dijkstra's algorithm w specified start/end, records the cost of the shortest path and returns it, puts the nodes visited on this shorted path in the ArrayList parameter. 
		//S = nodes w known shortest path, C[k] = shortest path from start to k, p[k] = predecessor to k in shortest path. 
		
		
		//Declaring all variables needed
		TreeSet<String> vertices = new TreeSet(getVertices()); 
		TreeSet<String> S = new TreeSet(); 
		TreeMap<String, String> P = new TreeMap<>(); 
		TreeMap<String, Integer> C = new TreeMap<>(); 
		//ArrayList<String> shortestPathList = shortestPath; 
		int shortestPathCost = 0; 
		
		//Forming C, P
		C.put(startVertexName, 0); 
		for(String vertice : vertices) {
			P.put(vertice, ""); 
			if(!vertice.equals(startVertexName)) {
				C.put(vertice, 10000); 
			}
		}
		//System.out.println(C); 
		
		//Main while loop for functionality
		//WHILE LOOP NEEDS MAJOR FIX IN ORDER TO STOP TIMEOUT ISSUES, ALL RELEASE TESTS THAT ARE FAILING ARE BECAUSE OF THIS ISSUE
		//Infinite loops caused when there is no path from start to a node, but the node is still connected to other nodes in the graph
		System.out.println("first while reached "+ endVertexName); 
		System.out.println(adjacencyMap); 
		System.out.println(dataMap);
		//while(S.size() < adjacencyMap.size()) 
		for(int i = 0; i < adjacencyMap.size(); i++){
			//Finding node K not in S with smallest C[K], adding to S
			int lowestCost = 10000; 
			String lowestCostVertice = ""; 
			for(String key : C.keySet()) {
				if(C.get(key) < lowestCost) {
					if(!S.contains(key)) {
						lowestCostVertice = key; 
						lowestCost = C.get(key); 
					}
				}
			}
			S.add(lowestCostVertice); 
			//Operating on nodes J adjacent to K. 
			Map<String, Integer> successors = getAdjacentVertices(lowestCostVertice);
			if(successors != null) {
				for(String successor : successors.keySet()) {
					if(!S.contains(successor)) {
						int successorCost = successors.get(successor); 
						if(lowestCost + successorCost < C.get(successor)) {
							C.replace(successor, lowestCost + successorCost);  
							P.replace(successor, lowestCostVertice); 
						}
					}
				}
			}
		}
		System.out.println("first while finished " + endVertexName); 
		
		//Forming shortestPath ArrayList 
		String currentNode = endVertexName; 
		shortestPath.add(currentNode); 
		while(P.get(currentNode) != "") {
			String predecessor = P.get(currentNode); 
			if(P.get(currentNode) != null) {
				shortestPath.add(predecessor); 
			}
			currentNode = predecessor; 
		}
		Collections.reverse(shortestPath);
		
		if(C.get(endVertexName) == 10000) {
			shortestPathCost = -1; 
			shortestPath.clear(); 
			shortestPath.add("None"); 
		}
		else {
			shortestPathCost = C.get(endVertexName); 
		}
		System.out.println("second while finished "+ endVertexName); 
		return shortestPathCost; 
	}
	
}