# VOIDBOUND

VOIDBOUND targets Minecraft 26.3, Fabric Loader 0.19.5 or newer, Fabric API 0.161.0+26.3, and Java 25. The Gradle project uses Fabric Loom 1.17-SNAPSHOT and separates client rendering from common gameplay code.

## Crafting the Void Codex

Craft the following at a crafting table:

```text
Amethyst shard | Obsidian    | Amethyst shard
Ender pearl    | Book        | Ender pearl
Amethyst shard | Obsidian    | Amethyst shard
```

The recipe is also unlocked in the recipe book when you acquire a book. The Codex is available in the Tools & Utilities creative tab.

## Using and testing it

Hold the Codex in either hand and switch to third person. The book floats beside the player, bobs and turns, and its pages flutter and periodically flip. A restrained trail of purple dust follows it; normal particle settings and distance limits apply. The regular held-item sprite is hidden in third person so only the floating book appears.

For a multiplayer check, install VOIDBOUND and Fabric API on the server and both clients, then have one player hold the Codex while the other watches. Test both player skin types, crouching, changing hands, and putting the book away. The visual relies on vanilla synchronized hand equipment rather than a custom network message.

## Ability foundation (Stage 2, M1)

Void Collapse, Phantom Arsenal, Rift Walk, and APOCALYPSE have stable identifiers and translated names, but are all unavailable. Their eventual cooldown durations are not defined yet. There are no ability controls, activation packets, or spell effects in this milestone.

`ServerAbilityManager` keeps selection and cooldowns in memory per server and player UUID. Void Collapse is the default selection, and server-side code can select or cycle through all registered definitions, including unavailable ones. The state survives a reconnect to the same running server but is discarded when that server stops. Call the manager from the server thread.

Cooldowns use overworld game ticks and pause when game time is not advancing. The cooldown status only checks registration, availability, and cooldown; it is not a complete authorization check. A future server-side ability must validate the player and Codex and execute successfully before recording a cooldown. The manager rejects cooldown recording for every currently unavailable ability.

## Current limitations

- First-person floating-book rendering is **not implemented**. In first person, the held item uses its ordinary item icon and the local ambient dust is hidden.
- The book uses Minecraft's native animated book mesh with original textures and an emissive rune overlay. Its geometry is not a bespoke Blockbench model; no GeckoLib dependency is included.
- The 3D book's cover, spine, page, and glow textures still need a dedicated visual-polish pass. The tested third-person position is preserved in M1.
- The render layer follows the player's body. Particle positions are matched to the usual upright pose and can differ during unusual poses such as swimming or sleeping.
- The abilities remain unimplemented. The visuals and recipe still need a full in-game acceptance test.