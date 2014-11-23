package aic.bigdata.rest.model;

import java.util.ArrayList;
import java.util.List;

public class ResultPage {

	private int totalSize;
	private List<? extends ResultEntry> result = new ArrayList<>();

	public int getSize() {
		return getResult().size();
	}

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public List<? extends ResultEntry> getResult() {
		return result;
	}

	public void setResult(List<? extends ResultEntry> result) {
		this.result = result;
	}
}
