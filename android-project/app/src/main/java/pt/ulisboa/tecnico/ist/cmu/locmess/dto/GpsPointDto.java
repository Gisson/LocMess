package pt.ulisboa.tecnico.ist.cmu.locmess.dto;

/**
 * Created by jorge on 17/05/17.
 */

public class GpsPointDto implements LocMessDto {

    private double _latitude,_longitude;

    public GpsPointDto(double latitude,double longitude){
        _latitude=latitude;
        _longitude=longitude;
    }
    @Deprecated
    public GpsPointDto(){
        _latitude=0;
        _longitude=0;
    }


    public double getLatitude() {
        return _latitude;
    }

    public double getLongitude() {
        return _longitude;
    }
}
