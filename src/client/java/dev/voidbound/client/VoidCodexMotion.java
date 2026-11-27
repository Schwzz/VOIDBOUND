package dev.voidbound.client;

final class VoidCodexMotion {
    static final float SIDE = 0.87F;
    static final float HEIGHT = -0.06F;
    static final float DEPTH = -0.12F;
    static final float CROUCH_DROP = 0.18F;

    private VoidCodexMotion() {
    }

    static float bob(float time) {
        return (float) (Math.sin(time * 0.075F) * 0.045F + Math.sin(time * 0.031F) * 0.012F);
    }

    static float pageTurn(float time) {
        float phase = time % 320.0F;
        if (phase < 72.0F || phase >= 112.0F) {
            return -1.0F;
        }
        return 0.02F + 0.96F * smoothstep((phase - 72.0F) / 40.0F);
    }

    private static float smoothstep(float value) {
        float t = Math.max(0.0F, Math.min(1.0F, value));
        return t * t * t * (t * (t * 6.0F - 15.0F) + 10.0F);
    }
}