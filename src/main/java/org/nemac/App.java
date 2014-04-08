package org.nemac;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.nemac.domain.Station;
import org.nemac.domain.Translate;
import org.nemac.normal.Normalizer;
import org.nemac.station.StationFilter;
import org.nemac.util.Compression;
import org.nemac.util.TypeDelete;

public class App {

    private static final boolean filterStations;
    private static final Set<String> minimumVariables = new HashSet<String>();
    private static final Set<String> normalsVariables = new HashSet<String>();
    private static final String summaryLocation;
    private static final String stationsLocation;
    private static final String prefix;
    private static final String outputPath;
    
    private static final boolean normalize;
    private static final String normalsDir;
    
    private static final boolean recursiveDelete;

    static {
        // STATION FILTER -------------------
        // should stations be filtered
        filterStations = false;

        // stations should have at least these variables
        minimumVariables.add("TMIN");
        minimumVariables.add("TMAX");
        //minimumVariables.add("PRCP");
        //minimumVariables.add("SNOW");
        
        normalsVariables.add("TMIN");
        normalsVariables.add("TMAX");

        // path to stations file
        stationsLocation = "stations.json";

        // path to summary file
        summaryLocation = "summary.json";

        // station id prefix
        prefix = "GHCND:";

        // path for output
        outputPath = "output/";
        
        // NORMALIZER -----------------------
        // should normals be normalized
        normalize = false;
        
        // path to normals, searches directories recursively for dat files
        normalsDir = "data/normals/";
        
        // RECURSIVE DELETE -----------------
        // will delete all dat files from the directory
        recursiveDelete = true;
        
    }

    public static void main(String[] args) {
        try {
            if (filterStations) {
                String stationsBlob = FileUtils.readFileToString(new File(stationsLocation));
                Set<Station> stations = Translate.toStations(stationsBlob);

                String summaryBlob = FileUtils.readFileToString(new File(summaryLocation));
                JSONObject summary = new JSONObject(summaryBlob);

                StationFilter filter = new StationFilter.Filterer(stations, summary, prefix)
                        .intersectStationNames()
                        .hasAllVariables(minimumVariables)
                        .hasNormals(normalsVariables, normalsDir)
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
            }
            
            if (normalize) {
                String[] extensions = {"dat"};
                Iterator<File> files = FileUtils.iterateFiles(new File(normalsDir), extensions, true);
                int count = 0;
                
                while (files.hasNext()) {
                    File f = files.next();
                    List<String> lines = Normalizer.normalize(f);
                    File outFile = new File(f.getCanonicalPath().substring(0, f.getCanonicalPath().lastIndexOf(".")) + ".csv");
                    FileUtils.writeLines(outFile, lines);
                    Compression.compress(outFile.getCanonicalPath());
                    count++;
                }
                
                System.out.print(count);
                System.out.println(" normals processed.");
                
            }
            
            if (recursiveDelete) {
                TypeDelete.recursiveDelete(normalsDir, "dat");
            }

        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}
