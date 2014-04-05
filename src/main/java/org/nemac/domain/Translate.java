package org.nemac.domain;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Translate {
    
    public static Set<Station> toStations(String stationsBlob) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<List<Station>>(){}.getType();
        List<Station> stations = gson.fromJson(stationsBlob, collectionType);
        return new HashSet<Station>(stations);
    }
    
    public static String fromStations(Set<Station> stations) {
        Gson gson = new Gson();
        return gson.toJson(stations);
    }
}
