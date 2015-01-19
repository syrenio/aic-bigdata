package aic.bigdata.extraction;

public interface TopicProvider extends Runnable{
	
	public void addHandler(TopicHandler t);
	
	public void stopProvider();

}
