package dev.voidbound.client;

import dev.voidbound.Voidbound;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.DustParticleOptions;

final class VoidCodexParticles {
    private static final DustParticleOptions DUST = new DustParticleOptions(0x9D55FF, 0.65F);

    private VoidCodexParticles() {
    }

    static void tick(Minecraft minecraft) {
        var level = minecraft.level;
        if (level == null || minecraft.player == null || minecraft.isPaused()) {
            return;
        }

        boolean firstPerson = minecraft.options.getCameraType().isFirstPerson();
        for (var player : level.players()) {
            if (player.isSpectator() || player.isInvisible() || !player.isHolding(Voidbound.VOID_CODEX)
                    || (firstPerson && player == minecraft.player)
                    || (player.tickCount + player.getId() * 3) % 8 != 0) {
                continue;
            }

            double yaw = Math.toRadians(player.yBodyRot);
            double phase = (player.tickCount + player.getId() * 13.0) * 0.91;
            boolean crouching = player.isCrouching();
            double playerScale = player.getScale();
            double scale = playerScale * 0.9375;
            double x = player.getX() + scale * (VoidCodexMotion.SIDE * Math.cos(yaw)
                    + VoidCodexMotion.DEPTH * Math.sin(yaw) + 0.12 * Math.cos(phase));
            double y = player.getY() + scale * (1.501 - VoidCodexMotion.HEIGHT
                    + VoidCodexMotion.bob(player.tickCount)
                    - (crouching ? VoidCodexMotion.CROUCH_DROP : 0.0)
                    + 0.09 * Math.sin(phase * 1.7))
                    - (crouching ? playerScale * 0.125 : 0.0);
            double z = player.getZ() + scale * (VoidCodexMotion.SIDE * Math.sin(yaw)
                    - VoidCodexMotion.DEPTH * Math.cos(yaw) + 0.12 * Math.sin(phase));
            level.addParticle(DUST, x, y, z, Math.cos(phase) * 0.001, 0.004, Math.sin(phase) * 0.001);
        }
    }
}