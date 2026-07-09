package pl.fuzjajadrowa.froggy.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class FroggyConfig {
    public static boolean spawnStalker = true;
    public static boolean spawnJumpscare = true;
    public static boolean spawnSleeping = true;
    public static boolean spawnBored = true;

    public static int weightStalker = 30;
    public static int weightJumpscare = 70;
    public static int weightBored = 10;

    public static int minSpawnRate = 18000;
    public static int maxRandomAdded = 12000;
    
    public static int sleepingCheckInterval = 1200;
    public static double sleepingSpawnChance = 0.10;

    public static int screamDamageLvl1 = 8;
    public static int screamDamageLvl2 = 13;
    public static int screamDamageLvl3 = 18;
 
    private static final File FILE = new File("config/froggy.json");
 
    public static void load() {
        if (!FILE.getParentFile().exists()) {
            FILE.getParentFile().mkdirs();
        }
        if (FILE.exists()) {
            try (FileReader reader = new FileReader(FILE)) {
                Gson gson = new Gson();
                JsonObject json = gson.fromJson(reader, JsonObject.class);
                if (json != null) {
                    if (json.has("spawnStalker")) spawnStalker = json.get("spawnStalker").getAsBoolean();
                    if (json.has("spawnJumpscare")) spawnJumpscare = json.get("spawnJumpscare").getAsBoolean();
                    if (json.has("spawnSleeping")) spawnSleeping = json.get("spawnSleeping").getAsBoolean();
                    if (json.has("spawnBored")) spawnBored = json.get("spawnBored").getAsBoolean();
                    if (json.has("weightStalker")) weightStalker = json.get("weightStalker").getAsInt();
                    if (json.has("weightJumpscare")) weightJumpscare = json.get("weightJumpscare").getAsInt();
                    if (json.has("weightBored")) weightBored = json.get("weightBored").getAsInt();
                    if (json.has("minSpawnRate")) minSpawnRate = json.get("minSpawnRate").getAsInt();
                    if (json.has("maxRandomAdded")) maxRandomAdded = json.get("maxRandomAdded").getAsInt();
                    if (json.has("sleepingCheckInterval")) sleepingCheckInterval = json.get("sleepingCheckInterval").getAsInt();
                    if (json.has("sleepingSpawnChance")) sleepingSpawnChance = json.get("sleepingSpawnChance").getAsDouble();
                    if (json.has("screamDamageLvl1")) screamDamageLvl1 = json.get("screamDamageLvl1").getAsInt();
                    if (json.has("screamDamageLvl2")) screamDamageLvl2 = json.get("screamDamageLvl2").getAsInt();
                    if (json.has("screamDamageLvl3")) screamDamageLvl3 = json.get("screamDamageLvl3").getAsInt();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            save();
        }
    }
 
    public static void save() {
        try (FileWriter writer = new FileWriter(FILE)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject json = new JsonObject();
            json.addProperty("spawnStalker", spawnStalker);
            json.addProperty("spawnJumpscare", spawnJumpscare);
            json.addProperty("spawnSleeping", spawnSleeping);
            json.addProperty("spawnBored", spawnBored);
            json.addProperty("weightStalker", weightStalker);
            json.addProperty("weightJumpscare", weightJumpscare);
            json.addProperty("weightBored", weightBored);
            json.addProperty("minSpawnRate", minSpawnRate);
            json.addProperty("maxRandomAdded", maxRandomAdded);
            json.addProperty("sleepingCheckInterval", sleepingCheckInterval);
            json.addProperty("sleepingSpawnChance", sleepingSpawnChance);
            json.addProperty("screamDamageLvl1", screamDamageLvl1);
            json.addProperty("screamDamageLvl2", screamDamageLvl2);
            json.addProperty("screamDamageLvl3", screamDamageLvl3);
            gson.toJson(json, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}