#!/bin/sh
#
# Copyright 2013 deib-polimi
# Contact: deib-polimi <marco.miglierina@polimi.it>
#
#    Licensed under the Apache License, Version 2.0 (the "License");
#    you may not use this file except in compliance with the License.
#    You may obtain a copy of the License at
#
#        http://www.apache.org/licenses/LICENSE-2.0
#
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS,
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#    See the License for the specific language governing permissions and
#    limitations under the License.
#

cd ../kundera-gae-datastore && mvn clean install -DskipTests
cd ../modaclouds-cpim-library && mvn install:install-file -DlocalRepositoryPath=repo -DcreateChecksum=true -Dpackaging=jar -DgroupId=com.impetus.kundera.client -DartifactId=kundera-gae-datastore -Dversion=1.0.0 -Dfile=../kundera-gae-datastore/target/kundera-gae-datastore-2.14.jar
cd ../kundera-azure-tables && mvn clean install -DskipTests
cd ../modaclouds-cpim-library && mvn install:install-file -DlocalRepositoryPath=repo -DcreateChecksum=true -Dpackaging=jar -DgroupId=com.impetus.kundera.client -DartifactId=kundera-azure-table -Dversion=1.0.0 -Dfile=../kundera-azure-table/target/kundera-azure-table-2.14.jar
