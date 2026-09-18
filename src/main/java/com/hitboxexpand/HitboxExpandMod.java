package com.hitboxexpand;

import com.hitboxexpand.gui.SettingsScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class HitboxExpandMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Config.load();
        Keybinds.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (Keybinds.toggleKey.wasPressed()) {
                HitboxState.enabled = !HitboxState.enabled;
                Config.save();
                if (client.player != null) {
                    client.player.sendMessage(
                        net.minecraft.text.Text.literal(
                            "§a[HitboxExpand] " + (HitboxState.enabled ? "ON" : "OFF")),
                        true
                    );
                }
            }
            while (Keybinds.settingsKey.wasPressed()) {
                client.setScreen(new SettingsScreen());
            }
        });

        System.out.println("[HitboxExpand] Loaded for MC 1.21.4");
    }
}
