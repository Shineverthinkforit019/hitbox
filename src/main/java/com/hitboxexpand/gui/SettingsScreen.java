package com.hitboxexpand.gui;

import com.hitboxexpand.Config;
import com.hitboxexpand.HitboxState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class SettingsScreen extends Screen {

    private int sliderX, sliderY, sliderW = 200, sliderH = 12;
    private boolean dragging = false;

    public SettingsScreen() {
        super(Text.literal("HitboxExpand Settings"));
    }

    @Override
    protected void init() {
        sliderX = width / 2 - sliderW / 2;
        sliderY = height / 2 - 20;

        // Nút bật/tắt hide on debug
        addDrawableChild(ButtonWidget.builder(
                Text.literal("HideOnDebug: " + (HitboxState.hideOnDebug ? "ON" : "OFF")),
                b -> {
                    HitboxState.hideOnDebug = !HitboxState.hideOnDebug;
                    b.setMessage(Text.literal("HideOnDebug: "
                            + (HitboxState.hideOnDebug ? "ON" : "OFF")));
                    Config.save();
                }
        ).dimensions(width / 2 - 100, height / 2 + 40, 200, 20).build());

        // Nút bật/tắt module
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Enabled: " + (HitboxState.enabled ? "ON" : "OFF")),
                b -> {
                    HitboxState.enabled = !HitboxState.enabled;
                    b.setMessage(Text.literal("Enabled: "
                            + (HitboxState.enabled ? "ON" : "OFF")));
                    Config.save();
                }
        ).dimensions(width / 2 - 100, height / 2 + 65, 200, 20).build());

        // Nút đóng
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Close"),
                b -> close()
        ).dimensions(width / 2 - 50, height / 2 + 95, 100, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);

        ctx.drawCenteredTextWithShadow(textRenderer, title, width / 2, height / 2 - 70, 0xFFFFFF);

        // Vẽ label
        String label = String.format("Expand: %.2f", HitboxState.expandMultiplier);
        ctx.drawText(textRenderer, label, sliderX, sliderY - 14, 0xFFFFFF, true);

        // Nền slider
        ctx.fill(sliderX, sliderY, sliderX + sliderW, sliderY + sliderH, 0xFF333333);

        // Phần đã chọn: map 0.1 → 5.0
        double percent = (HitboxState.expandMultiplier - 0.1) / (5.0 - 0.1);
        int fillW = (int) (sliderW * percent);
        ctx.fill(sliderX, sliderY, sliderX + fillW, sliderY + sliderH, 0xFF00AAFF);

        // Hiển thị vạch 1.0 (giữa bình thường)
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

    private void updateSlider(double mx) {
        double p = (mx - sliderX) / (double) sliderW;
        p = Math.max(0.0, Math.min(1.0, p));
        double val = 0.1 + p * (5.0 - 0.1);
        // Round tới 0.05
        val = Math.round(val * 20) / 20.0;
        HitboxState.expandMultiplier = val;
    }
}
