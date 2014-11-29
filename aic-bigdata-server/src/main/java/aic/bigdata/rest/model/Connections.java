package aic.bigdata.rest.model;

import java.util.ArrayList;
import java.util.List;

public class Connections {

	private int totalSize;
	private List<SigmaNode> nodes = new ArrayList<SigmaNode>();
	private List<SigmaEdge> edges = new ArrayList<SigmaEdge>();

	public int getSize() {
		return nodes.size();
	}

	public List<SigmaNode> getNodes() {
		return nodes;
	}
	
	public List<SigmaEdge> getEdges(){
		return edges;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

}
