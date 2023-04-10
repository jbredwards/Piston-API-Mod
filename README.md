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
* Grovvyscript examples:
```groovy
import static git.jbredwards.piston_api.mod.asm.PushReactionHandler.overridePushReaction
import net.minecraft.block.material.EnumPushReaction

//makes dirt be destroyed by pistons when pushed (like grass or leaves)
overridePushReaction(block('minecraft:dirt'), EnumPushReaction.DESTROY)

//makes orange wool not pushable by pistons, while leaving the rest pushable
def orangeWool = blockstate('minecraft:wool', 1)
overridePushReaction(block('minecraft:wool'), {source, pistonInfo -> orangeWool == source.getBlockState() ? EnumPushReaction.BLOCK : EnumPushReaction.NORMAL})
```

[`IStickyBehavior`](https://github.com/jbredwards/Piston-API-Mod/blob/1.12.2/src/main/java/git/jbredwards/piston_api/api/block/IStickyBehavior.java):
* This interface adds two methods: `getStickReaction` (which takes in the block source, the neighbor block to test, and piston info, and returns the [`EnumStickReaction`](https://github.com/jbredwards/Piston-API-Mod/blob/1.12.2/src/main/java/git/jbredwards/piston_api/api/piston/EnumStickReaction.java)), and `hasStickySide` (which takes in the block source & piston info, and returns whether the block has any potentially sticky sides). Blocks should implement this if they have advanced stickiness logic. This interface overrides forge's `isStickyBlock(IBlockState)` method, blocks that don't implement the interface fallback on forge's method. **For modpack devs:** Each block's stick result can be overriden by calling [`StickReactionHanlder.overrideStickReaction`](https://github.com/jbredwards/Piston-API-Mod/blob/1.12.2/src/main/java/git/jbredwards/piston_api/mod/asm/StickReactionHandler.java) through [Groovyscript](https://www.curseforge.com/minecraft/mc-mods/groovyscript) during postInit (mod devs should not do this!)
* Groovyscript examples:
```groovy

import static git.jbredwards.piston_api.mod.asm.StickReactionHandler.overrideStickReaction
import git.jbredwards.piston_api.api.piston.EnumStickReaction

//makes slime blocks not sticky
overrideStickReaction(block('minecraft:slime'), EnumStickReaction.PASS)

//makes crafting tables sticky
overrideStickReaction(block('minecraft:crafting_table'), EnumStickReaction.STICK)

//makes bookshelves never stick to anything
overrideStickReaction(block('minecraft:bookshelf'), EnumStickReaction.NEVER)

//makes stained glass sticky, but only to the same color
overrideStickReaction(block('minecraft:stained_glass'), {source, other, pistonInfo -> source.getBlockState() == other.getBlockState() ? EnumStickReaction.STICK : EnumStickReaction.PASS})

//a more complicated example, makes the open part of logs sticky
import static git.jbredwards.piston_api.api.block.IStickyBehavior.getConnectingSide
import net.minecraft.block.BlockLog
overrideStickReaction([block('minecraft:log'), block('minecraft:log2')], {source, other, pistonInfo ->
    def connectingAxis = BlockLog.EnumAxis.fromFacingAxis(getConnectingSide(source, other).getAxis())
    def logAxis = source.getBlockState().getValue(BlockLog.LOG_AXIS)

    connectingAxis == logAxis ? EnumStickReaction.STICK : EnumStickReaction.PASS
})
```
