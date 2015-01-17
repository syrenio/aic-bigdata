package aic.bigdata.extraction;

public interface RetweetingInfoProvider extends Runnable{
	public void addHandler(RetweetingInfoHandler t);
	public void stopProvider();
}
