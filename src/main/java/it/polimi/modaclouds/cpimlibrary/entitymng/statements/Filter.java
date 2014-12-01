package it.polimi.modaclouds.cpimlibrary.entitymng.statements;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Fabio Arcidiacono.
 */
@Data
@AllArgsConstructor
public class Filter {
    private String name;
    private String operator;
    private Object value;

    @Override
    public String toString() {
        return this.name + " " + this.operator + " " + this.value;
    }
}