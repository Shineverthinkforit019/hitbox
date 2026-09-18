package com.hitboxexpand;

import com.hitboxexpand.gui.SettingsScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class HitboxExpandMod implements ClientModInitializer {

    private static boolean lastTogglePressed = false;

    @Override
    public void onInitializeClient() {
        Config.load();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Đọc phím trực tiếp từ GLFW - không hiện trong Controls menu
            long handle = client.getWindow().getHandle();
            boolean pressed = GLFW.glfwGetKey(handle, Config.data.toggleKey) == GLFW.GLFW_PRESS;

            if (pressed && !lastTogglePressed) {
                HitboxState.enabled = !HitboxState.enabled;
                Config.save();
                if (client.player != null) {
                    client.player.sendMessage(
                        Text.literal("§7[§bPerfCore§7] §fRealtime optimizations: "
                                + (HitboxState.enabled ? "§aENABLED" : "§cDISABLED")),
                        true
                    );
                }
            }
            lastTogglePressed = pressed;
        });

        System.out.println("[PerfCore] Loaded optimization modules for MC 1.21.4");
    }
}
