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
package it.polimi.modaclouds.cpimlibrary.entitymng.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "EmployeeOTOne", schema = "gae-test@pu")
@NamedQueries({
        @NamedQuery(name = "allEmployees", query = "SELECT e FROM EmployeeOTOne e"),
        @NamedQuery(name = "updateSalary", query = "UPDATE EmployeeOTOne e SET e.salary = :s WHERE e.name = :n")
})
public class EmployeeOTO {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "EMPLOYEE_ID")
    private String id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "SALARY")
    private Long salary;

    /* an employee have one and only one phone */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PHONE_ID")
    private Phone phone;
}
