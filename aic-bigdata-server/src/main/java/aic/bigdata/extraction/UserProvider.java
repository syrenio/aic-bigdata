package aic.bigdata.extraction;

public interface UserProvider extends Runnable{
	
	public void addHandler(UserHandler t);
	
	public void stopProvider();

}
