package com.example.fall_dectection;

/**********************************************************
 *author: Haojue Wang
 *student number:  S1936286
 *
 *Description:In this activity, it mainly has the following functions
 *              1. Using the Serializable to store the data need to be transmitted
 *              2. Store the data of lat, lng, address
 * Layoutfile: non
 * */
import java.io.Serializable;

public class Intent_data_share implements Serializable {
    public String share_address;

    public double share_lat;

    public double share_long;

    public Intent_data_share( double share_lat, double share_long, String share_address) {
        this.share_lat = share_lat;
        this.share_long = share_long;
        this.share_address = share_address;
    }

    public String get_share_address() {
        return share_address;
    }

    public void set_share_address(String set_share_address) {
        this.share_address = share_address;
    }

    public double get_share_lat() {
        return share_lat;
    }

    public void set_share_lat(double set_share_lat) {
        this.share_lat = share_lat;
    }

    public double get_share_long() {
        return share_long;
    }

    public void set_share_long(double set_share_long) {
        this.share_long = share_long;
    }
}
