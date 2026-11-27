package dev.voidbound.client.effect;

import java.util.Objects;
import java.util.random.RandomGenerator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.world.phys.Vec3;

public final class VoidParticles {
    private static final DustParticleOptions DUST = new DustParticleOptions(0x9D55FF, 0.65F);

    private VoidParticles() {
    }

    public static void burst(Minecraft client, Vec3 origin, Settings settings, RandomGenerator random) {
        Objects.requireNonNull(origin, "origin");
        Objects.requireNonNull(settings, "settings");
        Objects.requireNonNull(random, "random");
        if (!canEmit(client)) {
            return;
        }
        for (int i = 0; i < settings.count(); i++) {
            double y = random.nextDouble(-1.0, 1.0);
            double angle = random.nextDouble() * Math.PI * 2.0;
            double horizontal = Math.sqrt(1.0 - y * y);
            double x = horizontal * Math.cos(angle);
            double z = horizontal * Math.sin(angle);
            double distance = settings.spread() * Math.cbrt(random.nextDouble());
            spawn(client, origin.x + x * distance, origin.y + y * distance, origin.z + z * distance,
                    x * settings.speed(), y * settings.speed(), z * settings.speed(), settings.lifetime(), random);
        }
    }

    public static void directional(Minecraft client, Vec3 origin, Vec3 direction,
            Settings settings, RandomGenerator random) {
        Objects.requireNonNull(origin, "origin");
        Objects.requireNonNull(direction, "direction");
        Objects.requireNonNull(settings, "settings");
        Objects.requireNonNull(random, "random");
        double length = Math.sqrt(direction.x * direction.x + direction.y * direction.y + direction.z * direction.z);
        if (!Double.isFinite(length) || length == 0.0) {
            throw new IllegalArgumentException("Direction must be finite and nonzero");
        }
        if (!canEmit(client)) {
            return;
        }
        double vx = direction.x / length * settings.speed();
        double vy = direction.y / length * settings.speed();
        double vz = direction.z / length * settings.speed();
        for (int i = 0; i < settings.count(); i++) {
            double spread = settings.spread();
            spawn(client, origin.x + (random.nextDouble() * 2.0 - 1.0) * spread,
                    origin.y + (random.nextDouble() * 2.0 - 1.0) * spread,
                    origin.z + (random.nextDouble() * 2.0 - 1.0) * spread,
                    vx, vy, vz, settings.lifetime(), random);
        }
    }

    public static void ring(Minecraft client, Vec3 origin, double radius,
            Settings settings, RandomGenerator random) {
        Objects.requireNonNull(origin, "origin");
        Objects.requireNonNull(settings, "settings");
        Objects.requireNonNull(random, "random");
        if (!Double.isFinite(radius) || radius < 0.0) {
            throw new IllegalArgumentException("Radius must be finite and nonnegative");
        }
        if (!canEmit(client)) {
            return;
        }
        double offset = random.nextDouble() * Math.PI * 2.0;
        for (int i = 0; i < settings.count(); i++) {
            double angle = offset + Math.PI * 2.0 * i / settings.count();
            double x = Math.cos(angle);
            double z = Math.sin(angle);
            double distance = Math.max(0.0, radius + (random.nextDouble() * 2.0 - 1.0) * settings.spread());
            spawn(client, origin.x + x * distance, origin.y, origin.z + z * distance,
                    x * settings.speed(), 0.0, z * settings.speed(), settings.lifetime(), random);
        }
    }

    private static boolean canEmit(Minecraft client) {
        Objects.requireNonNull(client, "client");
        return client.level != null && client.options.particles().get() != ParticleStatus.MINIMAL;
    }

    private static void spawn(Minecraft client, double x, double y, double z,
            double vx, double vy, double vz, int lifetime, RandomGenerator random) {
        if (client.gameRenderer.mainCamera().position().distanceToSqr(x, y, z) > 1024.0
                || (client.options.particles().get() == ParticleStatus.DECREASED && random.nextInt(3) == 0)) {
            return;
        }
        Particle particle = client.particleEngine.createParticle(DUST, x, y, z, vx, vy, vz);
        if (particle != null) {
            particle.setParticleSpeed(vx, vy, vz);
            particle.setLifetime(lifetime);
        }
    }

    public record Settings(int count, int lifetime, double speed, double spread) {
        public Settings {
            if (count < 0 || count > 256 || lifetime <= 0
                    || !Double.isFinite(speed) || speed < 0.0
                    || !Double.isFinite(spread) || spread < 0.0) {
                throw new IllegalArgumentException("Invalid particle settings");
            }
        }
    }

    public static final class Emitter {
        private final Settings settings;
        private final int intervalTicks;
        private final RandomGenerator random;
        private long lastObserved = -1L;
        private long lastEmission = -1L;

        public Emitter(Settings settings, int intervalTicks, RandomGenerator random) {
            this.settings = Objects.requireNonNull(settings, "settings");
            this.random = Objects.requireNonNull(random, "random");
            if (intervalTicks <= 0) {
                throw new IllegalArgumentException("Emission interval must be positive");
            }
            this.intervalTicks = intervalTicks;
        }

        public void emit(Minecraft client, Vec3 origin, Vec3 direction, long elapsedTicks) {
            if (elapsedTicks < 0L || elapsedTicks < lastObserved) {
                throw new IllegalArgumentException("Elapsed ticks must be nonnegative and monotonic");
            }
            lastObserved = elapsedTicks;
            if (lastEmission >= 0L && elapsedTicks - lastEmission < intervalTicks) {
                return;
            }
            lastEmission = elapsedTicks;
            directional(client, origin, direction, settings, random);
        }
    }
}