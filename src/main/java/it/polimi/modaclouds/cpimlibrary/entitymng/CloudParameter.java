package it.polimi.modaclouds.cpimlibrary.entitymng;

import javax.persistence.Parameter;

/**
 * Local implementation of {@link javax.persistence.Parameter} interface.
 * <p/>
 * <i>Positional</i> parameters have name set to null, vice versa <i>Named</i> parameters
 * have position set to null.
 *
 * @author Fabio Arcidiacono.
 * @see javax.persistence.Parameter
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.TypedCloudQuery
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.CloudQuery
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
