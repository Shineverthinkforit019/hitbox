package com.hitboxexpand.mixin;

import com.hitboxexpand.HitboxState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "getBoundingBox", at = @At("RETURN"), cancellable = true)
    private void onGetBoundingBox(CallbackInfoReturnable<Box> cir) {
        if (!HitboxState.enabled) return;

        Entity self = (Entity) (Object) this;
        MinecraftClient mc = MinecraftClient.getInstance();

        // Không mở rộng cho chính mình
        if (self == mc.player) return;

        // Ẩn khi bật F3 + B (debug hitbox)
        if (HitboxState.hideOnDebug
                && mc.getEntityRenderDispatcher().shouldRenderHitboxes()) {
            return;
        }

        double m = HitboxState.expandMultiplier;
        if (m == 1.0) return;

        Box box = cir.getReturnValue();
        if (box == null) return;

        double cx = (box.minX + box.maxX) / 2.0;
        double cz = (box.minZ + box.maxZ) / 2.0;
        double hx = (box.maxX - box.minX) * m / 2.0;
        double hz = (box.maxZ - box.minZ) * m / 2.0;
        double hy = (box.maxY - box.minY) * m;

        Box expanded = new Box(
                cx - hx, box.minY, cz - hz,
                cx + hx, box.minY + hy, cz + hz
        );
        cir.setReturnValue(expanded);
    }
}
