package com.hitboxexpand;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class HitboxExpandMod implements ClientModInitializer {

    private static boolean lastTogglePressed = false;

    @Override
    public void onInitializeClient() {
        Config.load();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            long handle = client.getWindow().getHandle();
            boolean pressed = GLFW.glfwGetKey(handle, Config.data.toggleKey) == GLFW.GLFW_PRESS;

            if (pressed && !lastTogglePressed) {
                HitboxState.enabled = !HitboxState.enabled;
                Config.save();
                if (client.player != null) {
                    client.player.sendMessage(
                        Text.literal("§7[§bEntityCulling§7] §fOptimization: "
                                + (HitboxState.enabled ? "§aENABLED" : "§cDISABLED")),
                        true
                    );
                }
            }
            lastTogglePressed = pressed;
        });

        System.out.println("[EntityCulling] Loaded culling engine for MC 1.21.4");
    }
}
