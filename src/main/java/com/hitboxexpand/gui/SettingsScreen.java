package com.hitboxexpand.gui;

import com.hitboxexpand.Config;
import com.hitboxexpand.HitboxState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class SettingsScreen extends Screen {

    private int sliderX, sliderY;
    private final int sliderW = 220, sliderH = 12;
    private boolean dragging = false;

    private boolean waitingForToggleKey = false;
    private boolean waitingForSettingsKey = false;

    // Màu sắc
    private static final int BG_COLOR = 0xFF1A1A1A;
    private static final int PANEL_COLOR = 0xFF2A2A2A;
    private static final int SLIDER_BG = 0xFF333333;
    private static final int SLIDER_FILL = 0xFF00AAFF;
    private static final int SLIDER_MARK = 0xFFFFFF00;
    private static final int TEXT_WHITE = 0xFFFFFF;
    private static final int TEXT_GRAY = 0xAAAAAA;
    private static final int TEXT_GREEN = 0x55FF55;
    private static final int TEXT_RED = 0xFF5555;
    private static final int TEXT_YELLOW = 0xFFFF55;

    public SettingsScreen() {
        super(Text.literal("EntityCulling Settings"));
    }

    @Override
    protected void init() {
        super.init();

        sliderX = width / 2 - sliderW / 2;
        sliderY = height / 2 - 40;

        // Nút bật/tắt optimization
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Optimization: " + (HitboxState.enabled ? "§aON" : "§cOFF")),
                b -> {
                    HitboxState.enabled = !HitboxState.enabled;
                    b.setMessage(Text.literal("Optimization: "
                            + (HitboxState.enabled ? "§aON" : "§cOFF")));
                    Config.save();
                }
        ).dimensions(width / 2 - 110, height / 2 + 25, 220, 20).build());

        // Toggle Debug-Safe (ẩn khi F3 + B)
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
                    waitingForSettingsKey = false;
                    b.setMessage(Text.literal("§7Press a key... (ESC to cancel)"));
                }
        ).dimensions(width / 2 - 110, height / 2 + 80, 220, 20).build());

        // Chỉnh keybind Settings
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Settings Key: §e" + keyName(Config.data.settingsKey)),
                b -> {
                    waitingForSettingsKey = true;
                    waitingForToggleKey = false;
                    b.setMessage(Text.literal("§7Press a key... (ESC to cancel)"));
                }
        ).dimensions(width / 2 - 110, height / 2 + 105, 220, 20).build());

        // Nút Reset về mặc định
        addDrawableChild(ButtonWidget.builder(
                Text.literal("§eReset Defaults"),
                b -> {
                    HitboxState.expandMultiplier = 1.0;
                    HitboxState.hideOnDebug = true;
                    Config.data.toggleKey = 75;   // K
                    Config.data.settingsKey = 74; // J
                    Config.save();
                    clearAndInit();
                }
        ).dimensions(width / 2 - 110, height / 2 + 135, 105, 20).build());

        // Nút Close
        addDrawableChild(ButtonWidget.builder(
                Text.literal("§7Close"),
                b -> close()
        ).dimensions(width / 2 + 5, height / 2 + 135, 105, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Nền tối
        renderBackground(ctx, mouseX, mouseY, delta);
        ctx.fill(0, 0, width, height, BG_COLOR);

        // Tiêu đề
        ctx.drawCenteredTextWithShadow(textRenderer,
                "§b§lEntityCulling §r§7- Settings",
                width / 2, height / 2 - 100, TEXT_WHITE);

        ctx.drawCenteredTextWithShadow(textRenderer,
                "§7Entity Render Optimizer",
                width / 2, height / 2 - 85, TEXT_GRAY);

        // Label slider
        String label = String.format("§fCulling Distance: §e%.2f", HitboxState.expandMultiplier);
        ctx.drawText(textRenderer, label, sliderX, sliderY - 16, TEXT_WHITE, true);

        // Nền slider
        ctx.fill(sliderX - 1, sliderY - 1, sliderX + sliderW + 1, sliderY + sliderH + 1, 0xFF000000);
        ctx.fill(sliderX, sliderY, sliderX + sliderW, sliderY + sliderH, SLIDER_BG);

        // Phần đã chọn
        double percent = (HitboxState.expandMultiplier - 0.1) / (5.0 - 0.1);
        percent = Math.max(0.0, Math.min(1.0, percent));
        int fillW = (int) (sliderW * percent);
        ctx.fill(sliderX, sliderY, sliderX + fillW, sliderY + sliderH, SLIDER_FILL);

        // Mốc 1.00 (giữa - mặc định)
        int oneW = (int) (((1.0 - 0.1) / (5.0 - 0.1)) * sliderW);
        ctx.fill(sliderX + oneW, sliderY - 3, sliderX + oneW + 1, sliderY + sliderH + 3, SLIDER_MARK);

        // Handle (nút kéo)
        int handleX = sliderX + fillW;
        ctx.fill(handleX - 2, sliderY - 2, handleX + 2, sliderY + sliderH + 2, 0xFFFFFFFF);

        // Hiển thị mốc dưới slider
        ctx.drawText(textRenderer, "§70.1", sliderX, sliderY + sliderH + 4, TEXT_GRAY, true);
        ctx.drawText(textRenderer, "§71.0", sliderX + oneW - 6, sliderY + sliderH + 4, TEXT_YELLOW, true);
        String maxText = "5.0";
        ctx.drawText(textRenderer, "§7" + maxText,
                sliderX + sliderW - textRenderer.getWidth(maxText),
                sliderY + sliderH + 4, TEXT_GRAY, true);

        // Ghi chú
        ctx.drawCenteredTextWithShadow(textRenderer,
                "§7Tip: Keep value ≤ §e2.0 §7for §aanticheat safety",
                width / 2, height / 2 + 165, TEXT_GRAY);

        // Trạng thái đang chờ nhập key
        if (waitingForToggleKey) {
            ctx.drawCenteredTextWithShadow(textRenderer,
                    "§ePress a key to set Toggle Key...",
                    width / 2, height / 2 + 185, TEXT_YELLOW);
        } else if (waitingForSettingsKey) {
            ctx.drawCenteredTextWithShadow(textRenderer,
                    "§ePress a key to set Settings Key...",
                    width / 2, height / 2 + 185, TEXT_YELLOW);
        }

        // Vẽ các widget con (button)
        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0
                && mx >= sliderX && mx <= sliderX + sliderW
                && my >= sliderY && my <= sliderY + sliderH) {
            dragging = true;
            updateSlider(mx);
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (dragging) {
            updateSlider(mx);
            return true;
        }
        return super.mouseDragged(mx, my, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        if (dragging) {
            dragging = false;
            Config.save();
            return true;
        }
        return super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Đang chờ nhập Toggle Key
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

        // Đang chờ nhập Settings Key
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

    @Override
    public boolean shouldPause() {
        return false;
    }

    /**
     * Cập nhật giá trị slider theo vị trí chuột.
     */
    private void updateSlider(double mx) {
        double p = (mx - sliderX) / (double) sliderW;
        p = Math.max(0.0, Math.min(1.0, p));
        double val = 0.1 + p * (5.0 - 0.1);
        // Round tới 0.05
        val = Math.round(val * 20) / 20.0;
        HitboxState.expandMultiplier = val;
    }

    /**
     * Lấy tên phím từ mã GLFW.
     */
    private String keyName(int code) {
        if (code == 0) return "None";
        String n = GLFW.glfwGetKeyName(code, 0);
        return n == null ? "KEY_" + code : n.toUpperCase();
    }
}
