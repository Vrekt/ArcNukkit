package me.vrekt.arc.check.result;

/**
 * Represents a parameter name and value pair.
 */
public interface Parameter {

    /**
     * Create a new parameter instance
     *
     * @param name  the name
     * @param value the value
     * @return the new parameter.
     */
    static Parameter of(String name, Object value) {
        return new Parameter() {
            private final String pair = name + "=" + value.toString();

            @Override
            public String getPair() {
                return pair;
            }
        };
    }

    /**
     * Retrieve the paired name and value
     *
     * @return the pair
     */
    String getPair();

}
