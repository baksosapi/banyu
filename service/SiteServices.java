package com.water.service;

import com.google.gson.JsonElement;
import com.water.Sites;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wildan
 */
public class SiteServices implements SiteService {

    private int totalWaterPoints;
    private HashMap<String,Integer> functionalWP;
    private String functionalWaterPoints;
    private JsonElement statusWaterPoints;
    private JsonElement countVillagesWP;
    private JsonElement percentageVillagesWP;
    private JsonElement rankCommunities;

    public SiteServices(ConcurrentHashMap<String, List<String>> list) {
        Sites sites = new Sites(list);

        this.totalWaterPoints = sites.getTotalWaterPoints();
        this.functionalWP = sites.countFunctionalWP();
        this.statusWaterPoints = sites.getStatusWaterPoints();
        this.countVillagesWP = sites.getCountVillagesWP();
        this.percentageVillagesWP = sites.getPercentageVillagesWP();
        this.rankCommunities = sites.getRankCommunities();

    }

    @Override
    public int getTotalWaterPoints() {
        return totalWaterPoints;
    }

    @Override
    public String getFunctionalWaterPoints() {
        return functionalWaterPoints;
    }

    @Override
    public JsonElement getStatusWaterPoints() {
        return statusWaterPoints;
    }

    @Override
    public JsonElement getCountVillagesWP() {
        return countVillagesWP;
    }

    @Override
    public JsonElement getPercentageVillagesWP() {
        return percentageVillagesWP;
    }

    public HashMap<String, Integer> getFunctionalWP() {
        return functionalWP;
    }

    public void setFunctionalWP(HashMap<String, Integer> functionalWP) {
        this.functionalWP = functionalWP;
    }

    public JsonElement getRankCommunities() {
        return rankCommunities;
    }

    public void setRankCommunities(JsonElement rankCommunities) {
        this.rankCommunities = rankCommunities;
    }
}
