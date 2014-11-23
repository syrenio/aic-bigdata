package aic.bigdata.rest.model;

public class Connection {
	/*
	 * { "id": "n0", "label": "A node", "x": 0, "y": 0, "size": 3 },
	 */
	private String id;
	private String label;
	private int x;
	private int y;
	private int size;

	public Connection(String id, String label, int x, int y, int size) {
		this.id = id;
		this.label = label;
		this.x = x;
		this.y = y;
		this.size = size;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getSize() {
		return size;
	}

}
