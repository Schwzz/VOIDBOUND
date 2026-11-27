package dev.voidbound;

import dev.voidbound.ability.server.ServerAbilityManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public final class Voidbound implements ModInitializer {
    public static final String MOD_ID = "voidbound";
    public static final Item VOID_CODEX = registerCodex();

    private static Item registerCodex() {
        ResourceKey<Item> key = ResourceKey.create(
                Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, "void_codex"));
        return Registry.register(
                BuiltInRegistries.ITEM, key, new Item(new Item.Properties().setId(key).stacksTo(1)));
    }

    @Override
    public void onInitialize() {
        ServerAbilityManager.initialize();
        CreativeModeTabEvents.modifyOutputEvent(ResourceKey.create(
                Registries.CREATIVE_MODE_TAB,
                Identifier.fromNamespaceAndPath("minecraft", "tools_and_utilities")))
                .register(output -> output.accept(VOID_CODEX));
    }
}