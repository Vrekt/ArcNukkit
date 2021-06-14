package me.vrekt.arc.configuration.types;

import org.apache.commons.lang3.StringUtils;

/**
 * The ban length type.
 */
public enum BanLengthType {

    /**
     * How long a player should be banned for.
     */
    DAYS("days"), YEARS("years"), PERM("permanently");

    /**
     * The pretty name
     */
    private final String prettyName;

    BanLengthType(String prettyName) {
        this.prettyName = prettyName;
    }

    /**
     * @return pretty name
     */
    public String prettyName() {
        return prettyName;
    }

    /**
     * Parse the {@link BanLengthType} from configuration
     *
     * @param input the input
     * @return the ban length type, {@code PERM} for default.
     */
    public static BanLengthType parse(String input) {
        final String actualInput = StringUtils.deleteWhitespace(input.toUpperCase());
        return actualInput.equals("DAYS") ? DAYS : actualInput.equals("YEARS") ? YEARS : PERM;
    }

}
