package dev.voidbound.client;

final class VoidCodexMotion {
    static final float SIDE = 0.78F;
    static final float HEIGHT = 0.22F;
    static final float DEPTH = 0.05F;
    static final float CROUCH_DROP = 0.14F;

    private VoidCodexMotion() {
    }

    static float bob(float time) {
        return (float) (Math.sin(time * 0.075F) * 0.045F + Math.sin(time * 0.031F) * 0.012F);
    }

    static float pageSweep(float time) {
        float phase = time % 220.0F;
        return smoothstep((phase - 36.0F) / 14.0F) * (1.0F - smoothstep((phase - 62.0F) / 16.0F));
    }

    private static float smoothstep(float value) {
        float t = Math.max(0.0F, Math.min(1.0F, value));
        return t * t * (3.0F - 2.0F * t);
    }
}