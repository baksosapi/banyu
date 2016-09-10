package com.water.service;

import com.google.gson.JsonElement;

/**
 * Created by wildan
 */
public interface SiteService {

    int getTotalWaterPoints();

    String getFunctionalWaterPoints();

    JsonElement getStatusWaterPoints();

    JsonElement getCountVillagesWP();

    JsonElement getPercentageVillagesWP();

    JsonElement getRankCommunities();
}
