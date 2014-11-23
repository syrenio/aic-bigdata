package aic.bigdata.rest.model;

import java.util.ArrayList;
import java.util.List;

public class Connections {

	private int totalSize;
	private List<Connection> connections = new ArrayList<Connection>();

	public int getSize() {
		return connections.size();
	}

	public List<Connection> getConnections() {
		return connections;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

}
