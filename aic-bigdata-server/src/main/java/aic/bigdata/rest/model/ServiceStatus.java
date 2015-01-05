package aic.bigdata.rest.model;

/**
 * Created by syrenio on 05.01.15.
 */
public class ServiceStatus {

    public boolean stream;
    public boolean extraction;
    public boolean analyse;

    public boolean getActive(){
        return stream || extraction || analyse;
    }
}
