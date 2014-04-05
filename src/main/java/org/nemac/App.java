package org.nemac;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.nemac.domain.Station;
import org.nemac.domain.Translate;
import org.nemac.station.StationFilter;

public class App {
    
    private static final Set<String> minimumVariables = new HashSet<String>();
    private static final String summaryLocation;
    private static final String stationsLocation;
    private static final String prefix;
    private static final String outputPath;
    static {
        // stations should have at least these variables
        minimumVariables.add("TMIN");
        minimumVariables.add("TMAX");
        minimumVariables.add("PRCP");
        minimumVariables.add("SNOW");
        
        // path to stations file
        stationsLocation = "target/data/stations.json";
        
        // path to summary file
        summaryLocation = "target/data/summary.json";
        
        // station id prefix
        prefix = "GHCND:";
        
        // path for output
        outputPath = "target/data/";
    }
    
    public static void main(String[] args) {
        try {
            String stationsBlob = FileUtils.readFileToString(new File(stationsLocation));
            Set<Station> stations = Translate.toStations(stationsBlob);
            
            String summaryBlob = FileUtils.readFileToString(new File(summaryLocation));
            JSONObject summary = new JSONObject(summaryBlob);
            
            StationFilter filter = new StationFilter.Filterer(stations, summary, prefix)
                    .intersectStationNames()
                    .hasAllVariables(minimumVariables)
                    .filter();
            
            Set<Station> filteredStations = filter.getFilteredStations();
            Set<String> removalLog = filter.getRemovalLog();
                      
            System.out.print(removalLog.size());
            System.out.println(" stations were filtered out");
            System.out.println("Writing out filter log");
            FileUtils.writeLines(new File(outputPath + "removal.log"), filter.getRemovalLog());
            
            System.out.print(filteredStations.size());
            System.out.println(" stations remaining");
            System.out.println("Writing out filtered stations");
            Gson gson = new Gson();
            FileUtils.writeStringToFile(new File(outputPath + "filteredStations.json"), gson.toJson(filteredStations));
            
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}
