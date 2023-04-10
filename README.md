# Basic Overview

A small mod that lets mods and modpacks handle piston interactions!

**API Features:**
* Give custom blocks advanced piston interactions

**Basic Mod Features:**
* Pistons move tile entities
* Configurable piston push limit
* Fluidlogged API mod support
* Quark mod support

**Advanced Mod Features:**
* Override advanced piston interactions
* Chests stick each other when pushed

---

# Advanced Overview

Piston API gives blocks a way to have position & world sensitive interactions with pistons! Mainly the block's pushability, and its stickiness.

[`IPushableBehavior`](https://github.com/jbredwards/Piston-API-Mod/blob/1.12.2/src/main/java/git/jbredwards/piston_api/api/block/IPushableBehavior.java):
* This interface adds one method `getPushReaction` (which takes in the block source & piston info, and returns the `EnumPushReaction`). Blocks should implement this if they have advanced push reaction logic. This interface overrides vanilla's `getPushReaction(IBlockState)` method, blocks that don't implement the interface fallback on vanilla's method. **For modpack devs:** Each block's push reaction can be overriden by calling [`PushReactionHanlder.overridePushReaction`](https://github.com/jbredwards/Piston-API-Mod/blob/1.12.2/src/main/java/git/jbredwards/piston_api/mod/asm/PushReactionHandler.java) through [Groovyscript](https://www.curseforge.com/minecraft/mc-mods/groovyscript) during postInit (mod devs should not do this!)

[`IStickyBehavior`](https://github.com/jbredwards/Piston-API-Mod/blob/1.12.2/src/main/java/git/jbredwards/piston_api/api/block/IStickyBehavior.java):
* This interface adds two methods: `getStickResult` (which takes in the block source, the neighbor block to test, and piston info, and returns the [`EnumStickResult`](https://github.com/jbredwards/Piston-API-Mod/blob/1.12.2/src/main/java/git/jbredwards/piston_api/api/piston/EnumStickResult.java)), and `hasStickySide` (which takes in the block source & piston info, and returns whether the block has any potentially sticky sides). Blocks should implement this if they have advanced stickiness logic. This interface overrides forge's `isStickyBlock(IBlockState)` method, blocks that don't implement the interface fallback on forge's method. **For modpack devs:** Each block's stick result can be overriden by calling [`StickResultHanlder.overrideStickiness`](https://github.com/jbredwards/Piston-API-Mod/blob/1.12.2/src/main/java/git/jbredwards/piston_api/mod/asm/StickResultHandler.java) through [Groovyscript](https://www.curseforge.com/minecraft/mc-mods/groovyscript) during postInit (mod devs should not do this!)
