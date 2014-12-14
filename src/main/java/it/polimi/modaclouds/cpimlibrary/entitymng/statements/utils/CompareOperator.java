package it.polimi.modaclouds.cpimlibrary.entitymng.statements.utils;

/**
 * @author Fabio Arcidiacono.
 */
public enum CompareOperator {
    EQUAL("="),
    LOWER_THAN("<"),
    GREATER_THAN(">"),
    LOWER_THAN_OR_EQUAL("<="),
    GREATER_THAN_OR_EQUAL(">="),
    NOT_EQUAL("<>");

    private String string;

    private CompareOperator(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }

    public static CompareOperator fromString(String string) {
        for (CompareOperator o : values()) {
            if (o.toString().equalsIgnoreCase(string)) {
                return o;
            }
        }
        throw new IllegalArgumentException();
    }
}
