package me.vrekt.arc.check.result;

import cn.nukkit.utils.TextFormat;

/**
 * Represents a check result.
 */
public final class CheckResult {

    public enum Result {

        /**
         * The player has passed
         */
        PASSED,
        /**
         * The player has failed
         */
        FAILED,
    }

    /**
     * The result
     */
    private Result result = Result.PASSED;

    /**
     * Information builder.
     */
    private StringBuilder informationBuilder;

    /**
     * Empty
     */
    public CheckResult() {

    }

    /**
     * @return the result
     */
    public Result result() {
        return result;
    }

    /**
     * Set the result
     *
     * @param result the result
     */
    public void result(Result result) {
        this.result = result;
    }

    /**
     * Set failed
     *
     * @param information the initial information
     */
    public CheckResult setFailed(String information) {
        setFailed();
        info(information);
        return this;
    }

    /**
     * Set failed
     */
    public CheckResult setFailed() {
        this.result = Result.FAILED;
        this.informationBuilder = new StringBuilder();
        return this;
    }

    /**
     * Add an information line
     *
     * @param information the information
     */
    public void info(String information) {
        informationBuilder.append(TextFormat.RED).append(information);
        informationBuilder.append("\n");
    }

    /**
     * Add a parameter
     *
     * @param parameter the parameter
     * @param value     the value
     */
    public void parameter(String parameter, Object value) {
        if (informationBuilder == null) informationBuilder = new StringBuilder();
        informationBuilder.append("\n").append(TextFormat.GRAY);
        informationBuilder.append(parameter).append("=").append(value.toString());
    }

    /**
     * @return if the player has failed
     */
    public boolean failed() {
        return result == Result.FAILED;
    }

    /**
     * @return retrieve the information
     */
    public String information() {
        return informationBuilder.toString();
    }

}
