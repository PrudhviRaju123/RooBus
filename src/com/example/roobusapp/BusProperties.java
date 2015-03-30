package com.example.roobusapp;

public class BusProperties {

	int bus_id;
	double lat;
	double lng;
	int route_no;
	int lastStop;
	double time_remaining;

	public int getBus_id() {
		return bus_id;
	}

	public void setBus_id(int bus_id) {
		this.bus_id = bus_id;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public int getRoute_no() {
		return route_no;
	}

	public void setRoute_no(int route_no) {
		this.route_no = route_no;
	}

	public int getLastStop() {
		return lastStop;
	}

	public void setLastStop(int lastStop) {
		this.lastStop = lastStop;
	}
	
	public double getTime_remaining() {
		return time_remaining;
	}

	public void setTime_remaining(double time_taken) {
		this.time_remaining = time_taken;
	}


}
