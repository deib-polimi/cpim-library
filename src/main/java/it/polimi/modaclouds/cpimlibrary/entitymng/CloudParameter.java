package it.polimi.modaclouds.cpimlibrary.entitymng;

import javax.persistence.Parameter;

/**
 * @author Fabio Arcidiacono.
 */
public class CloudParameter<T> implements Parameter<T> {

    private String name;
    private Integer position;
    private Class<T> type;

    public CloudParameter(String name, Integer position, Class<T> type) {
        this.name = name;
        this.position = position;
        this.type = type;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Integer getPosition() {
        return this.position;
    }

    @Override
    public Class<T> getParameterType() {
        return this.type;
    }
}
