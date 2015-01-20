package aic.bigdata.rest.model;

import java.util.ArrayList;
import java.util.List;

public class ResultPage {

	private long totalSize;
	private List<? extends ResultEntry> result = new ArrayList<>();

	public int getSize() {
		return getResult().size();
	}

	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	public List<? extends ResultEntry> getResult() {
		return result;
	}

	public void setResult(List<? extends ResultEntry> result) {
		this.result = result;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName());
		sb.append(" totalSize: " + getTotalSize());
		sb.append(" resultSize: " + getSize());
		return sb.toString();
	}
}
