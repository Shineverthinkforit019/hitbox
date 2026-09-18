package com.hitboxexpand;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Keybinds {
    public static KeyBinding toggleKey;
    public static KeyBinding settingsKey;

    public static void register() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hitboxexpand.toggle",
                InputUtil.Type.KEYSYM,
                Config.data.toggleKey,
                "category.hitboxexpand"
        ));
        settingsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hitboxexpand.settings",
                InputUtil.Type.KEYSYM,
                Config.data.settingsKey,
                "category.hitboxexpand"
        ));
    }
}
