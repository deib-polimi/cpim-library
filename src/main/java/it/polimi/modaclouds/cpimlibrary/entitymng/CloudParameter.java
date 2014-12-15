/**
 * Copyright 2013 deib-polimi
 * Contact: deib-polimi <marco.miglierina@polimi.it>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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
