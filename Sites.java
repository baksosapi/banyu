package com.water;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wildan
 */
public class Sites {

    private static final String VILLAGES = "communities_villages";
    private static final String FUNCTIONING = "water_functioning";
    private static final String ID = "_id";
    private static final String YES = "yes";
    private static final String NO = "no";
    private static final String NA_DN = "na_dn";
    private static final String TOTAL_UNIT = "total_unit";
    private ConcurrentHashMap<String, List<String>> mapData = null;
    private HashSet<String> wpSetFunctioning = null;
    private HashMap<String,Integer> functionalWP;
    private JsonArray statusWP = new JsonArray();
    private List<String> wpFunctioning;
    private List<String> wpVillages;
    private Set<String> wpSetVillages;
    private JsonArray countVillagesWaterpoints = new JsonArray();
    private JsonArray percentageVillagesWP = new JsonArray();
    private ArrayList<Object> functionalPercentage;
    private ArrayList<Object> unitPercentage;
    private int numberAllWaterPoints = 0;
    private ArrayList<String> brokenWaterpoints;
    private JsonArray commRankCommunity = new JsonArray();
    private Map<String,Integer> brokenWaterpointsMap;
    private HashMap<String, Integer> mapBrokenWaterpoints;

    public Sites(ConcurrentHashMap<String, List<String>> cl) {
        mapData = cl;
        wpFunctioning = mapData.get(FUNCTIONING);
        wpVillages = mapData.get(VILLAGES);
        wpSetFunctioning = new HashSet<>(wpFunctioning);
        wpSetVillages = new HashSet<>(wpVillages);
        this.functionalWP = countFunctionalWP();
    }


    /**
     * Get Number of Total Waterpoints based on ID
     * @return
     */
    public int getTotalWaterPoints() {
        numberAllWaterPoints = mapData.get(ID).size();
        return numberAllWaterPoints;
    }

    /**
     * Integer count Functional Waterpoints
     * @return  totalFunctionalWP
     */
    public int getFunctionalWaterPoints() {
        int totalFunctionalWP = Collections.frequency(wpFunctioning, YES);
        return totalFunctionalWP;
    }

    /**
     * Get Status of WaterPoints
     * @return
     */
    public JsonArray getStatusWaterPoints() {
        for (String key: functionalWP.keySet()) {
            JsonObject jo = new JsonObject();
            jo.addProperty(key, functionalWP.get(key));
            statusWP.add(jo);
        }
        return this.statusWP;
    }

    /**
     * Get Cunt Waterpoints per Community Village
     * @return
     */
    public JsonArray getCountVillagesWP(){
        HashMap<String, Integer> mapVillagesWaterpoints = new HashMap<>();
        mapBrokenWaterpoints = new HashMap<>();
        functionalPercentage = new ArrayList<>();
        unitPercentage = new ArrayList<>();
        brokenWaterpoints = new ArrayList<>();

        int idx = 0;
        for (String village_name : wpSetVillages){
            // Check water_functioning status: yes, no, na_dn
            for (String status : wpSetFunctioning){
                Integer count = 0;
                for (int i=0; i<wpVillages.size();i++) {
                    if (village_name.equals(wpVillages.get(i)) && status.equals(wpFunctioning.get(i))) {
                        count++;
                    }
                }
                mapVillagesWaterpoints.put(status,count);
            }

            mapVillagesWaterpoints.put(TOTAL_UNIT, Collections.frequency(wpVillages, village_name));

            mapBrokenWaterpoints.put(village_name,mapVillagesWaterpoints.get(NO));

            brokenWaterpoints.add(mapVillagesWaterpoints.get(NO).toString());
            functionalPercentage.add(String.format("%2.2f",(mapVillagesWaterpoints.get(YES) * 100d / mapVillagesWaterpoints.get(TOTAL_UNIT))));
            unitPercentage.add(String.format("%2.2f",(mapVillagesWaterpoints.get(TOTAL_UNIT) * 100d / numberAllWaterPoints)));


            JsonObject jo = new JsonObject();

            jo.addProperty("community_name", village_name);
            jo.addProperty("functional", mapVillagesWaterpoints.get(YES));
            jo.addProperty("not_function", mapVillagesWaterpoints.get(NO));
            jo.addProperty("not_available", mapVillagesWaterpoints.get(NA_DN));
            jo.addProperty("number_total_unit", mapVillagesWaterpoints.get(TOTAL_UNIT));

            countVillagesWaterpoints.add(jo);

        }

        return countVillagesWaterpoints;

    }

    public HashMap<String, Integer> countFunctionalWP(){
        Map<String, Integer> functionalWP = new HashMap<>();
        for(String temp: wpFunctioning){
            Integer count = functionalWP.get(temp);
            functionalWP.put(temp, (count==null)? 1 : count + 1);
        }
        return (HashMap<String, Integer>) functionalWP;
    }

    /**
     * Get Percentage per Community
     * @return
     */
    public JsonArray getPercentageVillagesWP() {
        int idx = 0;
        for (String village_name : wpSetVillages) {
            int j = idx++;

            // Percentage Based on Functional per Community
            JsonObject jo = new JsonObject();
            jo.addProperty("community_name", village_name);
            jo.addProperty("percentage_functional", functionalPercentage.get(j) + " %");
            jo.addProperty("percentage_unit_waterpoints", unitPercentage.get(j) + " %");
            percentageVillagesWP.add(jo);
        }

        return percentageVillagesWP;
    }

    /**
     * Get Rank Communities by Broken Waterpoints , minimum is Best
     * @return
     */
    public JsonArray getRankCommunities(){

        Map<String, Integer> sortBrokenWaterpoints = sortByValue(mapBrokenWaterpoints);

        for (String temp: sortBrokenWaterpoints.keySet()) {
            JsonObject jo = new JsonObject();
            jo.addProperty("community_name", temp);
            jo.addProperty("broken_rank", sortBrokenWaterpoints.get(temp));
            commRankCommunity.add(jo);
        }

        return commRankCommunity;
    }

    /**
     * Sort Rank By Value
     * @param unsortMap
     * @return
     */
    private Map<String, Integer> sortByValue(Map<String, Integer> unsortMap) {

        // Convert data from Map to List
        List<Map.Entry<String, Integer>> list = new LinkedList<>(unsortMap.entrySet());

        // Sort list by used comparator, to compare each the Map values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // Convert sorted map back to a Map Form
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }


}
