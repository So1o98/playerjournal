package com.player.journal.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestrictionDataLoader extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new GsonBuilder().create();


    public static final Map<String, Map<String, Integer>> DATAPACK_RESTRICTIONS = new HashMap<>();


    public static final Map<String, Map<String, Integer>> CRAFTING_RESTRICTIONS = new HashMap<>();
    public static final Map<String, Integer> CRAFTING_XP = new HashMap<>();

    public RestrictionDataLoader() {
        super(GSON, "journal_restrictions");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objectIn, ResourceManager resourceManagerIn, ProfilerFiller profilerIn) {
        DATAPACK_RESTRICTIONS.clear();
        CRAFTING_RESTRICTIONS.clear();
        CRAFTING_XP.clear();

        int count = 0;

        for (Map.Entry<ResourceLocation, JsonElement> entry : objectIn.entrySet()) {
            try {
                JsonElement element = entry.getValue();
                if (element.isJsonArray()) {
                    JsonArray array = element.getAsJsonArray();
                    for (JsonElement itemElement : array) {
                        parseItem(itemElement.getAsJsonObject());
                        count++;
                    }
                } else if (element.isJsonObject()) {
                    parseItem(element.getAsJsonObject());
                    count++;
                }
            } catch (Exception e) {
                System.err.println("[PlayerJournal] Failed to parse journal restriction from datapack: " + entry.getKey());
                e.printStackTrace();
            }
        }
        System.out.println("[PlayerJournal] Loaded " + count + " custom item restrictions from datapacks!");
    }

    private void parseItem(JsonObject jsonObject) {
        if (!jsonObject.has("item")) return;
        String item = jsonObject.get("item").getAsString();


        if (jsonObject.has("requirements")) {
            JsonObject reqs = jsonObject.getAsJsonObject("requirements");
            Map<String, Integer> reqMap = new HashMap<>();
            for (String skill : reqs.keySet()) {
                reqMap.put(skill, reqs.get(skill).getAsInt());
            }
            if (!reqMap.isEmpty()) {
                DATAPACK_RESTRICTIONS.put(item, reqMap);
            }
        }


        if (jsonObject.has("crafting_requirements")) {
            JsonObject craftReqs = jsonObject.getAsJsonObject("crafting_requirements");
            Map<String, Integer> craftMap = new HashMap<>();
            for (String skill : craftReqs.keySet()) {
                craftMap.put(skill, craftReqs.get(skill).getAsInt());
            }
            if (!craftMap.isEmpty()) {
                CRAFTING_RESTRICTIONS.put(item, craftMap);
            }
        }


        if (jsonObject.has("xp_reward")) {
            CRAFTING_XP.put(item, jsonObject.get("xp_reward").getAsInt());
        }
    }

    public static Map<String, Integer> getItemRestrictions(String itemId) {
        return DATAPACK_RESTRICTIONS.getOrDefault(itemId, new HashMap<>());
    }

    public static Map<String, Integer> getCraftingRestrictions(String itemId) {
        return CRAFTING_RESTRICTIONS.getOrDefault(itemId, new HashMap<>());
    }

    public static int getCraftingXp(String itemId) {
        return CRAFTING_XP.getOrDefault(itemId, 0);
    }


    public static List<String> exportDatapackAndConfig() {
        List<String> combined = new ArrayList<>(com.player.journal.config.JournalConfig.getAllItemRestrictions());
        for (Map.Entry<String, Map<String, Integer>> entry : DATAPACK_RESTRICTIONS.entrySet()) {
            StringBuilder sb = new StringBuilder(entry.getKey());
            for (Map.Entry<String, Integer> req : entry.getValue().entrySet()) {
                sb.append(";").append(req.getKey()).append(":").append(req.getValue());
            }
            combined.add(sb.toString());
        }
        return combined;
    }


    public static List<String> exportCraftingDatapackAndConfig() {
        List<String> combined = new ArrayList<>(com.player.journal.config.JournalConfig.getAllCraftingRestrictions());
        for (Map.Entry<String, Map<String, Integer>> entry : CRAFTING_RESTRICTIONS.entrySet()) {
            StringBuilder sb = new StringBuilder(entry.getKey());
            for (Map.Entry<String, Integer> req : entry.getValue().entrySet()) {

                sb.append(";").append(req.getKey()).append(":").append(req.getValue());
            }
            combined.add(sb.toString());
        }
        return combined;
    }
}