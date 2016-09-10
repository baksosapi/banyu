package com.water;


import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.water.service.SiteService;
import com.water.service.SiteServices;
import org.apache.commons.io.IOUtils;

import java.io.StringReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class Main {

    /**
     * Main program
     * @param args  an optional
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        // 1. Get data

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter JSON url :");

        String url = sc.next();

        String json = IOUtils.toString(new URL(url), "UTF-8");

        JsonArray ja = grabJson(json);

        // 2. Analyze Data

        Set<Object> keys = new Main().getSetKeys(ja);

        JsonObject jo = new Main().dataProcessor(ja, keys);

        // 3. Show Result
        showDisplay(jo);

    }

    /**
     * Processed data From Json
     * @param jsonArray As an input
     * @param keys      Uniq keys
     * @return          Return to List
     */
    private JsonObject dataProcessor(JsonArray jsonArray, Set<Object> keys) {

        ConcurrentHashMap<String, List<String>> cl = new ConcurrentHashMap<>();

        // Foreach data
        for (JsonElement jsonElement : jsonArray){

            JsonObject jo = jsonElement.getAsJsonObject();

            // Get Value given Key per Item
            for (Object idx : keys) {

                JsonElement job = jo.get((String) idx);
                String value = "";

                // Check wether Key and Value is Exist

                if (job != null) {

                    if (job.isJsonNull() || job.isJsonArray()) {
                        value = job.toString();
                    } else if (job.isJsonPrimitive() || job.isJsonObject()) {
                        value = job.getAsString();
                    }

                    recordList(cl, (String)idx,value);

                } // end if job
            } // end keys
        } // end for element

        return toJson(cl);
    }

    private JsonObject toJson(ConcurrentHashMap<String, List<String>> list) {
        JsonObject jobj = new JsonObject();

        SiteService processor = new SiteServices(list);

        jobj.addProperty("number_waterpoints:", processor.getTotalWaterPoints());

        jobj.addProperty("number_functional:", processor.getFunctionalWaterPoints());

        jobj.add("is_waterpoints_functioning:", processor.getStatusWaterPoints());

        jobj.add("waterpoints_per_community", processor.getCountVillagesWP());

        jobj.add("percentage_waterpoints", processor.getPercentageVillagesWP());

        jobj.add("rank_communities", processor.getRankCommunities());

        return jobj;
    }

    /**
     * Put uniq Value on each Key from Json
     * @param entries   Set of Key and Map List
     * @param key       Key on Map
     * @param value     Value on Map
     */
    private void recordList(ConcurrentHashMap<String, List<String>> entries, String key, String value) {
        List<String> values = entries.get(key);
        if (values == null) {
            entries.putIfAbsent(key, Collections.synchronizedList(new ArrayList<String>()));
            // At this point, there will definitely be a list for the key.
            // We don't know or care which thread's new object is in there, so:
            values = entries.get(key);
        }
        values.add(value);
    }

    /**
     * Read Json Data
     * @param s         Url Address
     * @return          Parsed Element form Json Data
     */
    private static JsonArray grabJson(String s){

        JsonReader reader = new JsonReader(new StringReader(s));
        JsonParser jp = new JsonParser();
        JsonElement elparsed = jp.parse(reader);
        return elparsed.getAsJsonArray();
    }

    /**
     * Get an uniq Key from Json
     * @param jsonArray Array Json as Input
     * @return          Return as Uniq Key
     * @throws          Exception
     */
    private Set<Object> getSetKeys(JsonArray jsonArray) throws Exception {
        List keys = getKeysFromJson(jsonArray);
        HashSet<Object> keySet = new HashSet<>();
        for (Object val: keys) {
            keySet.add(val);
        }
        return keySet;
    }

    /**
     * Fetch            List of Keys from JsonArray
     * @param jsonArray Array of Json
     * @return          Return as A list
     * @throws          Exception
     */
    private List getKeysFromJson(JsonArray jsonArray) throws Exception {
        Object items = new Gson().fromJson(jsonArray, Object.class);
        List keys = new ArrayList();

        fetchKeys(keys, items);

        return keys;
    }

    /**
     * Fetch key from Object
     * @param keys      Keys from List
     * @param obj       Object to proccess
     */
    private void fetchKeys(List keys, Object obj) {
        Collection values;
        if (obj instanceof Map) {
            Map map = (Map) obj;
            keys.addAll(map.keySet());// fetch keys at same level
            values = map.values();
        } else if (obj instanceof Collection) {
            values = (Collection) obj;
        } else{
            return;
        }

        for (Object value : values) {
            fetchKeys(keys, value);
        }
    }

    /**
     * Show Result with beauty format
     * @param jsonObject Object as input to proccess
     */
    private static void showDisplay(JsonObject jsonObject){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create();
        String result = gson.toJson(jsonObject);
        System.out.println("New Json : " + result);
    }

}
