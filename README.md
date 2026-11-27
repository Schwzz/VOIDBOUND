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

## Ability foundation (Stage 2, M2)

Void Collapse, Phantom Arsenal, Rift Walk, and APOCALYPSE have stable identifiers and translated names, but are all unavailable. Their eventual cooldown durations are not defined yet. No spell effects are implemented.

While holding the Codex in either hand, press Z to cycle abilities and R to request activation. Both bindings can be changed in Minecraft's controls. Input is suppressed while a screen is open or the debug modifier is held. Z is commonly used by zoom mods, and other mods may use either key; change the bindings if they conflict. Selection and availability feedback appears in the action bar.

The client sends only a cycle or activation request. The server checks the player's state and held Codex, resolves selection and cooldowns itself, and sends action-bar feedback. Unavailable abilities remain selectable but cannot activate or consume cooldowns. Requests are not sent to servers that do not advertise the payload.

`ServerAbilityManager` keeps selection and cooldowns in memory per server and player UUID. Void Collapse is the default selection, and server-side code can select or cycle through all registered definitions, including unavailable ones. The state survives a reconnect to the same running server but is discarded when that server stops. Call the manager from the server thread.

Cooldowns use overworld game ticks and pause when game time is not advancing. The cooldown status only checks registration, availability, and cooldown; it is not a complete authorization check. A future server-side ability must validate the player and Codex and execute successfully before recording a cooldown. The manager rejects cooldown recording for every currently unavailable ability.

## Timelines and effects (Stage 2, M3)

`Timeline` is a side-independent tick-based building block. Define named phases (which may overlap), easing and ordered one-shot cues before advancing it. Call `advance` with monotonically increasing elapsed game ticks, starting at zero; repeated ticks do not repeat cues, and skipped ticks deliver the crossed cues in order. Completion, interruption, and cue failure invoke the supplied cleanup once. For rendering, `phaseProgress` accepts the current game partial tick; sampling never runs cues. Owners are responsible for advancing timelines on the appropriate game thread and interrupting them when their world or effect is removed.

Client-only `VoidParticles` provides purple dust bursts, directional emission, horizontal expanding rings and an interval-based emitter suitable for trails. Count (capped at 256 per call), lifetime, speed and positional spread are configurable. It observes the normal particle setting and 32-block camera distance. Use a seeded random generator for reproducible inputs; particle settings and camera distance may make the visible result differ between clients. Stop calling an emitter when its effect ends.

`EffectSounds` broadcasts an existing positional sound from the server with adjustable volume and pitch, or schedules it as a timeline cue. Do not also play the same sound from a client visual handler. No custom sounds are included.

The registered clientbound `VisualEffectPayload` carries an effect identifier, origin and seed. A future server ability can send one event to the caster and players currently tracking them via `VisualEffectNetworking`; the client dispatches it to a handler registered under that identifier. Unknown identifiers are ignored. No handlers or spell effects are registered in M3. This is a one-shot notification, not a persistent effect manager: it does not synchronize timelines, moving positions, late joiners or world-space recipients. Each server call sends a new event; callers must avoid sending the same event twice. Ability validation and gameplay remain server-side.

## Current limitations

- First-person floating-book rendering is **not implemented**. In first person, the held item uses its ordinary item icon and the local ambient dust is hidden.
- The book uses Minecraft's native animated book mesh with original textures and an emissive rune overlay. Its geometry is not a bespoke Blockbench model; no GeckoLib dependency is included.
- The 3D book's cover, spine, page, and glow textures still need a dedicated visual-polish pass. The tested third-person position is preserved.
- The render layer follows the player's body. Particle positions are matched to the usual upright pose and can differ during unusual poses such as swimming or sleeping.
- The abilities remain unimplemented. The visuals and recipe still need a full in-game acceptance test.