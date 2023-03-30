package git.jbredwards.piston_api.api.piston;

/**
 * Used by {@link git.jbredwards.piston_api.api.block.IStickyBehavior IStickyBehavior} to determine
 * if two blocks should stick to each other when moved by a piston.
 * @author jbred
 *
 */
public enum EnumStickResult
{
    /**
     * Always sticks, this behavior cannot be overriden.
     * This should be always reserved for overriding another block's {@link EnumStickResult#NEVER} behavior,
     * please use {@link EnumStickResult#STICK} instead whenever possible.
     */
    ALWAYS,

    /**
     * Always sticks, this can be overriden if the other block returns {@link EnumStickResult#NEVER}.
     * This is what vanilla's slime block does.
     */
    STICK,

    /**
     * Only sticks if the other block allows it. This is what vanilla's non-slime blocks do.
     */
    PASS,

    /**
     * Never sticks, this can be overriden if the other block returns {@link EnumStickResult#ALWAYS}.
     */
    NEVER
}
