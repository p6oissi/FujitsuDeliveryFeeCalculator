package com.fujitsu.delivery.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StationData {
    public String name;
    public String wmocode;
    public Double airtemperature;
    public Double windspeed;
    public String phenomenon;
}
