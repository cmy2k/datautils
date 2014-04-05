package org.nemac.station;

import java.util.HashSet;
import java.util.Set;
import org.json.JSONObject;
import org.nemac.domain.Station;

public class StationFilter {

    private static final String messageFormat = "%s (%s) %s";

    private final Set<Station> filteredStations;
    private final Set<String> removalReason;

    public static class Filterer {

        private final JSONObject summary;
        private Set<Station> stations;
        private Set<String> removalLog;
        private final String prefix;

        public Filterer(Set<Station> stations, JSONObject summary, String prefix) {
            removalLog = new HashSet<String>();
            this.stations = stations;
            this.summary = summary;
            this.prefix = prefix;
        }

        public Filterer intersectStationNames() {
            Set<Station> localStations = new HashSet<Station>();
            localStations.addAll(stations);

            for (Station station : localStations) {
                if (!summary.has(station.getId().substring(prefix.length()))) {
                    stations.remove(station);
                    removalLog.add(String.format(messageFormat, station.getName(), station.getId(), "Not in summary"));
                }
            }

            return this;
        }

        public Filterer maxStartDate(String date) {

            return this;
        }

        public Filterer minEndDate(String date) {

            return this;
        }

        public Filterer hasAllVariables(Set<String> variables) {
            Set<Station> localStations = new HashSet<Station>();
            localStations.addAll(stations);

            for (Station station : localStations) {
                String key = station.getId().substring(prefix.length());
                if (summary.has(key)) {
                    JSONObject stationSummary = summary.getJSONObject(station.getId().substring(prefix.length()));
                    if (!stationSummary.keySet().containsAll(variables)) {
                        stations.remove(station);
                        removalLog.add(String.format(messageFormat, station.getName(), station.getId(), "Doesn't contain all variables"));
                    }
                } else {
                    stations.remove(station);
                    removalLog.add(String.format(messageFormat, station.getName(), station.getId(), "Doesn't contain all variables"));
                }
            }

            return this;
        }

        public StationFilter filter() {
            return new StationFilter(this);
        }
    }

    private StationFilter(Filterer filter) {
        filteredStations = filter.stations;
        removalReason = filter.removalLog;
    }

    public Set<Station> getFilteredStations() {
        return filteredStations;
    }

    public Set<String> getRemovalLog() {
        return removalReason;
    }
}
