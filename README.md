# VOIDBOUND — Stage 1

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

## Current limitations

- First-person floating-book rendering is **not implemented**. In first person, the held item uses its ordinary item icon and the local ambient dust is hidden.
- The book uses Minecraft's native animated book mesh with original textures and an emissive rune overlay. Its geometry is not a bespoke Blockbench model; no GeckoLib dependency is included.
- The render layer follows the player's body. Particle positions are matched to the usual upright pose and can differ during unusual poses such as swimming or sleeping.
- This stage contains no spell abilities. The visuals and recipe still need an in-game acceptance test.