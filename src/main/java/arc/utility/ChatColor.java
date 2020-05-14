package arc.utility;

/**
 * Trying to replicate Bukkits ChatColor stuff.
 */
public enum ChatColor {

    RED("§c"),

    BLACK("§0"),

    DARK_BLUE("§1"),

    DARK_GREEN("§2"),

    DARK_AQUA("§3"),

    DARK_RED("§4"),

    DARK_PURPLE("§5"),

    GOLD("§6"),
    GRAY("§7"),
    DARK_GRAY("§8"),
    BLUE("§9"),
    GREEN("§a"),
    AQUA("§b"),
    LIGHT_PURPLE("§d"),
    YELLOW("§e"),
    WHITE("§f");

    private final String color;

    ChatColor(String color) {
        this.color = color;
    }

    /**
     * @return the color
     */
    public String color() {
        return color;
    }
}
