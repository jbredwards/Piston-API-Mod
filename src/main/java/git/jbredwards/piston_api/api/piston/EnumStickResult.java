package git.jbredwards.piston_api.api.piston;

/**
 *
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
     */
    STICK,

    /**
     * Only sticks if the other block allows it.
     */
    PASS,

    /**
     * Never sticks, this can be overriden if the other block returns {@link EnumStickResult#ALWAYS}.
     */
    NEVER
}
