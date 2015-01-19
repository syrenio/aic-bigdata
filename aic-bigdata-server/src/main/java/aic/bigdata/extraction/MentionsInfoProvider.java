package aic.bigdata.extraction;

public interface MentionsInfoProvider extends Runnable{
	public void addHandler(MentionsInfoHandler t);
	public void stopProvider();
}
