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
     * If we have failed at all.
     */
    private boolean failedBefore;

    /**
     * Empty
     */
    public CheckResult() {

    }

    /**
     * Create a result from another
     *
     * @param other the other
     */
    public CheckResult(CheckResult other) {
        this.result = other.result;
        this.failedBefore = other.failedBefore;
        this.informationBuilder = other.informationBuilder;
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
        if (informationBuilder != null && informationBuilder.length() != 0) {
            // Arc.getPlugin().getLogger().warning("A check is not resetting the check result, information: \n" + information);
        }

        setFailed();
        info(information);
        return this;
    }

    /**
     * Attach parameter debug information to this result.
     *
     * @param parameter the parameter
     * @param value     the value
     * @return this
     */
    public CheckResult withParameter(String parameter, Object value) {
        if (informationBuilder == null) informationBuilder = new StringBuilder();
        informationBuilder.append("\n").append(TextFormat.GRAY);
        informationBuilder.append(parameter).append("=").append(value.toString());
        return this;
    }

    /**
     * Set failed
     */
    public CheckResult setFailed() {
        this.result = Result.FAILED;
        this.failedBefore = true;
        return this;
    }

    /**
     * Add an information line
     *
     * @param information the information
     */
    public void info(String information) {
        if (informationBuilder == null) informationBuilder = new StringBuilder();
        informationBuilder.append(TextFormat.RED).append(information);
        informationBuilder.append("\n");
    }

    /**
     * @return if the player has failed
     */
    public boolean failed() {
        return result == Result.FAILED;
    }

    /**
     * @return if the player has failed before.
     */
    public boolean hasFailedBefore() {
        return failedBefore;
    }

    /**
     * @return retrieve the information
     */
    public String information() {
        return informationBuilder.toString();
    }

    /**
     * Reset this result
     */
    public void reset() {
        if (result == Result.FAILED) {
            result = Result.PASSED;
            informationBuilder.setLength(0);
        }
    }
}
