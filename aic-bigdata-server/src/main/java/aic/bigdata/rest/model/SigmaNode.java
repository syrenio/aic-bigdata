package aic.bigdata.rest.model;

public class SigmaNode {
	/*
	 * { "id": "n0", "label": "A node", "x": 0, "y": 0, "size": 3 },
	 */
	private String id;
	private String label;
	private double x;
	private double y;
	private double size;

	public SigmaNode(String id, String label, double x, double y, double size) {
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

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getSize() {
		return size;
	}

}
