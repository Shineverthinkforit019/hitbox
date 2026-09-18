package com.hitboxexpand;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Path;

public class Config {
    private static final Path PATH = FabricLoader.getInstance()
        .getConfigDir().resolve("entityculling.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static class Data {
        public boolean enabled = false;
        public double expandMultiplier = 1.0;
        public boolean hideOnDebug = true;
        public int toggleKey = 75; // GLFW_KEY_K
        public int settingsKey = 74; // GLFW_KEY_J
    }

    public static Data data = new Data();

    public static void load() {
        try {
            File f = PATH.toFile();
            if (!f.exists()) { save(); return; }
            try (Reader r = new FileReader(f)) {
                Data d = GSON.fromJson(r, Data.class);
                if (d != null) data = d;
            }
        } catch (Exception e) { e.printStackTrace(); }
        HitboxState.enabled = data.enabled;
        HitboxState.expandMultiplier = data.expandMultiplier;
        HitboxState.hideOnDebug = data.hideOnDebug;
    }

    public static void save() {
        try {
            data.enabled = HitboxState.enabled;
            data.expandMultiplier = HitboxState.expandMultiplier;
            data.hideOnDebug = HitboxState.hideOnDebug;
            PATH.toFile().getParentFile().mkdirs();
            try (Writer w = new FileWriter(PATH.toFile())) {
                GSON.toJson(data, w);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}
public static class Data {
    public boolean enabled = false;
    public double expandMultiplier = 1.0;
    public boolean hideOnDebug = true;
    public int toggleKey = 75;   // K
    public int settingsKey = 74; // J
}
