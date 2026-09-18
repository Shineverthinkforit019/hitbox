package com.hitboxexpand.gui;

import com.hitboxexpand.Config;
import com.hitboxexpand.HitboxState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class SettingsScreen extends Screen {

    private int sliderX, sliderY, sliderW = 220, sliderH = 12;
    private boolean dragging = false;

    private boolean waitingForToggleKey = false;
    private boolean waitingForSettingsKey = false;

    public SettingsScreen() {
        super(Text.literal("PerfCore Settings"));
    }

    @Override
    protected void init() {
        sliderX = width / 2 - sliderW / 2;
        sliderY = height / 2 - 40;

        // Nút bật/tắt optimization
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Realtime: " + (HitboxState.enabled ? "§aON" : "§cOFF")),
                b -> {
                    HitboxState.enabled = !HitboxState.enabled;
                    b.setMessage(Text.literal("Realtime: "
                            + (HitboxState.enabled ? "§aON" : "§cOFF")));
                    Config.save();
                }
        ).dimensions(width / 2 - 110, height / 2 + 25, 220, 20).build());

        // Toggle hideOnDebug
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Debug-Safe: " + (HitboxState.hideOnDebug ? "§aON" : "§cOFF")),
                b -> {
                    HitboxState.hideOnDebug = !HitboxState.hideOnDebug;
                    b.setMessage(Text.literal("Debug-Safe: "
                            + (HitboxState.hideOnDebug ? "§aON" : "§cOFF")));
                    Config.save();
                }
        ).dimensions(width / 2 - 110, height / 2 + 50, 220, 20).build());

        // Chỉnh keybind Toggle
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Toggle Key: §e" + keyName(Config.data.toggleKey)),
                b -> {
                    waitingForToggleKey = true;
                    b.setMessage(Text.literal("§7Press a key..."));
                }
        ).dimensions(width / 2 - 110, height / 2 + 80, 220, 20).build());

        // Chỉnh keybind Settings
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Settings Key: §e" + keyName(Config.data.settingsKey)),
                b -> {
                    waitingForSettingsKey = true;
                    b.setMessage(Text.literal("§7Press a key..."));
                }
        ).dimensions(width / 2 - 110, height / 2 + 105, 220, 20).build());

        // Close
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Close"),
                b -> close()
        ).dimensions(width / 2 - 50, height / 2 + 135, 100, 20).build());
    }

    private String keyName(int code) {
        String n = GLFW.glfwGetKeyName(code, 0);
        return n == null ? "KEY_" + code : n.toUpperCase();
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);

        ctx.drawCenteredTextWithShadow(textRenderer, "§b§lPerfCore §r§7- Settings",
                width / 2, height / 2 - 90, 0xFFFFFF);

        ctx.drawCenteredTextWithShadow(textRenderer,
                "§7Entity Culling Optimizer", width / 2, height / 2 - 75, 0xAAAAAA);

        String label = String.format("§fCache Scale: §e%.2f", HitboxState.expandMultiplier);
        ctx.drawText(textRenderer, label, sliderX, sliderY - 14, 0xFFFFFF, true);

        // Slider nền
        ctx.fill(sliderX, sliderY, sliderX + sliderW, sliderY + sliderH, 0xFF333333);

        double percent = (HitboxState.expandMultiplier - 0.1) / (5.0 - 0.1);
        int fillW = (int) (sliderW * percent);
        ctx.fill(sliderX, sliderY, sliderX + fillW, sliderY + sliderH, 0xFF00AAFF);

        // Mốc 1.00
        int oneW = (int) (((1.0 - 0.1) / (5.0 - 0.1)) * sliderW);
        ctx.fill(sliderX + oneW, sliderY - 2, sliderX + oneW + 1, sliderY + sliderH + 2, 0xFFFFFF00);

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0 && mx >= sliderX && mx <= sliderX + sliderW
                && my >= sliderY && my <= sliderY + sliderH) {
            dragging = true;
            updateSlider(mx);
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (dragging) { updateSlider(mx); return true; }
        return super.mouseDragged(mx, my, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        if (dragging) { dragging = false; Config.save(); return true; }
        return super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (waitingForToggleKey) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                waitingForToggleKey = false;
                clearAndInit();
                return true;
            }
            Config.data.toggleKey = keyCode;
            Config.save();
            waitingForToggleKey = false;
            clearAndInit();
            return true;
        }
        if (waitingForSettingsKey) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                waitingForSettingsKey = false;
                clearAndInit();
                return true;
            }
            Config.data.settingsKey = keyCode;
            Config.save();
            waitingForSettingsKey = false;
            clearAndInit();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void updateSlider(double mx) {
        double p = (mx - sliderX) / (double) sliderW;
        p = Math.max(0.0, Math.min(1.0, p));
        double val = 0.1 + p * (5.0 - 0.1);
        val = Math.round(val * 20) / 20.0;
        HitboxState.expandMultiplier = val;
    }
}
