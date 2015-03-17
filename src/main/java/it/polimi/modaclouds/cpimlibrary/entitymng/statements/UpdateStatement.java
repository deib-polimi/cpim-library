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
package it.polimi.modaclouds.cpimlibrary.entitymng.statements;

import it.polimi.modaclouds.cpimlibrary.entitymng.statements.utils.Filter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;

/**
 * Represents an UPDATE statement.
 *
 * @author Fabio Arcidiacono.
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.statements.Statement
 */
@Data
@Slf4j
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UpdateStatement extends Statement {

    @Override
    public String toString() {
        StringBuilder setList = new StringBuilder();
        Iterator entries = getFieldsIterator();
        while (entries.hasNext()) {
            Filter filter = (Filter) entries.next();
            setList.append(filter.toString());
            if (entries.hasNext()) {
                setList.append(", ");
            }
        }
        if (!haveConditions()) {
            return String.format("UPDATE %s SET %s", this.getTable(), setList);
        } else {
            String conditions = "";
            entries = getConditionsIterator();
            while (entries.hasNext()) {
                Object next = entries.next();
                conditions += next.toString();
                if (entries.hasNext()) {
                    conditions += " ";
                }
            }
            return String.format("UPDATE %s SET %s WHERE %s", this.getTable(), setList, conditions);
        }
    }
}