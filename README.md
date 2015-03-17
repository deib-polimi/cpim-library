CPIM Installation Manual
============


The CPIM library is available on github at the following url:

https://github.com/deib-polimi/cpim-library

The library is managed by maven with which you need to install it .
At the moment it is not yet present any maven repository to enable the automatic import.

In order to use within a project the CPIM library it is necessary to add the following maven dependency :

    <dependency>

  	<groupId> it.polimi.modaclouds.cpimlibrary </groupId>

	  <artifactId>CPIM-library</artifactId>

	  <version> 1.0 -SNAPSHOT </version>

    </dependency>

The two library, SimpleJPA and JPA4azure used by the CPIM to implement the service NoSQL respectively in the case of Amazon and Azure, are automatically imported by maven and a local maven repository present in this project (repo folder). Also all the other dependency needed to deploying on the different cloud provider and not available on any maven repository, are stored in the local maven repo. Thus in order to dispose of these library we have just to install via maven the cpim library. However we remind that this could be just a temporary solution.


CPIM User Manual
============


The library introduces a software abstraction between applications and cloud services by exposing Vendor-Independent  API.
The platforms currently supported are Google App Engine , Windows Azure and Amazon Web Services. In addition, the library has been extended in order to allow the deployment of local applications that make use of Glassifish 4.0 Application Server.
The library CPIM exposes APIs for access to the following Cloud services , among the most common in PaaS platforms :

- NoSQL Service

- SQL Service

- Blob Service

- Message Queue

- Task Queue

- Mail Service

- Memcache

The library, using only the metadata contained in the configuration files, can remap, at runtime, requests to these generic cloud services, into vendor-specific calls in respect of the service provider you choose.
In particular, the library makes use of 3 XML configuration file:

- configuration.xml

- persistence.xml

- queue.xml

- migration.xml

These files should be placed in the META-INF folder of the project.
The persistence.xml file is the well known configuration file of the  data persistence service and is in fact used by the CPIM to perform the NoSQL service. Being already in itself a standard we will analyze only the configuration for the specific provider in the following sections .
The configuration.xml file and queue.xml are proper to the library. Then we go below to analyze their structure in order to better understand the correct manual configuration of an application that makes use of the CPIM .

configurations.xml
============


The file structure is as follows:

	<configurations>

		<vendor name="value" />

		<services>

			<slq>

				<connection string="value" />

				<blobconnection string="value" />

			</sql>

			<memcache>

				<host address="value" port="11211" />

			</memcache>

			<mail>

				<server smtp host="smtp.provider.com" port="587" />

				<account info username="mail address" password="psw" />

			</mail>

			<backend name="value" />

		</services>

	</configurations>

Quickly analyze the meaning of the various tags :

vendor:  allows you to set the cloud platform choice for the deployment of the application. Possible values ??currently will be:

Glassfish

Google

Azure

Amazon


- <sql>:  tags come within this specified all the information you need to use the SQL service. In particular, it is necessary to specify the connection string to access the database via JDBC to use. The tag <blobconnection> , similar to the first , was introduced following the extension to Glassfish for the only purpose of managing the Blob data in a database different from the one used for normal data, as will be explained in subsequent sections. Note that for the other vendors the Blob service is provided by the Cloud Provider itself and is then automatically configured using the same information required for the SQL service.


- <memcache>: memcache service is configured simply by entering the address of the memcached server. The port associated with this service is usually the " 11211 ".


- <mail>: for configuring the mail service you will first need to specify the SMTP server that you will use going to set the host and port attributes of the tag <server smtp> . In addition, the tag <mail> also admits the tag <account info> whose attributes are used to specify the authentication information ( username and password) to the service.


- <backend>: CPIM library also allows you to perform tasks in a queue, which is configured with the tag <mode> PUSH </mode> on a dedicated VM . This is enabled by entering the tag backend. the values to be specified in the name attribute change according on the platform on which the application runs,


queue.xml
============

The file structure is as follows:


	<queue-entries>

		<queue>

			<name> message_queue_name </name>

			<mode>PULL</mode>

		</queue>

		<queue>

			<name>task_queue_name</name>

			<mode>PUSH</mode>

			<rate>VALUE</rate>

		</queue>

	</queue-entries>


With this file you can configure both queue services ( tasks and messages ) provided by the CPIM .
A generic queue is marked by the tag <queue> ,specifying a unique name for each queue so that it can be recognized and recalled.
What makes explicit the difference between message and task queue is the tag <mode> that admits values PULL or PUSH to indicate that you want to use , in the first case a message queue,in the second a task queue.
In the case of task queue, it must also be specified a rate, which is the time interval that the system will wait between the execution of a task and that of the next one. The rate should be specified as [numberoftasks/seconds] .

To also allow integration with Glassfish the file structure was extended. In the case of a deployment on Glassfish applications that makes use of the message queue service must also specify the two tags <messagequeueconnection> <messagequeueresource.> whose meaning will be explained in the paragraphs relating to the setup of the architecture and the deploy configuration for Glassfish .

migration.xml
============
The file structure is as follow

```
  <migration>

	   <zooKeeper>

		   <type>thread|http</type>

           <connection>connection-string-or-api-base-url</connection>

    	   <range>seq-number-range</range>

     </zooKeeper>

  	 <backup>

        <execute>yes|no</execute>

    		<type>blob|file</type>

    		<directory>directory-for-file-backup</directory>

        <prefix>backup-prefix</prefix>

     </backup>

</migration>
```

With this file can be configured the interaction with the migration system.
If the file is missing the NoSQL service interacts directly with the persistence provider, otherwise all the required classes for synchronization and configuration are initialized.

The content of the `connection` tag should be the API base path in case the `type` is http and thus the PaaS provider does not support thread spawning.
Otherwise the _thread_ type can be used and then the `connection` tag should be _hostname:port_ of the zookeeper service.

`range` is optional, the default is 10, and is the default range of sequence number requested to the synchronization system.

the `execute` tag in `backup` should be used to turn off backups since are executed by default to blob.
If the `type` is set to file, the `directory` tag is mandatory and must contains the path to the directory in which backup files will be stored; the tag is ignored if type is set to blob.
The `prefix` tag contains the prefix added to the filename of each backup, is optional since its default is _SeqNumer__

Deploy on GlassFish AS
============


The library CPIM has been extended to allow applications that use it can be performed on Glassfish AS 4.0. All services exposed by the library have been appropriately extended in order to be available in case of deployment on Glassfish 4.0.

Details about the Implementation of the CMPI Services in the Glassfish Extension
============


NoSQL Service
============

For the implementation of the service NoSQL it has been used the JPA specification . The chosen provider is EclipseLink available by default on Glassfish 4.0.

SQL Service
============


The SQL service has been implemented in a manner similar to what was done for the other vendors. The DBMS is MySQL 5.6.15.

MAIL Service
============


The implementation was carried out using the JavaMail specification . In the case of Glassfish should be noted that unlike GAE and Azure is necessary to specify in the configuration file the SMTP server you want to use for the mail service , there being not the one used by the AS default , specifying the address and port as specified in paragraph application configuration .

MessageQueue Service
============


The service MessageQueue has been implemented using the JMS specification and then leaning into JNDI as we shall see in the setup of the architecture.

Blob Service
============


Regarding this service , as other vendors have a specific database in which to store the files Blob and the specific API to handle , it is thought to create also in this case a second database , managed by MySQL and always using the interface Blob available in the package java.sql . To make the second database is recognized by the library, has also extended the structure and parsing the file configuration.xml including a second connection string to a second database and extending the class CloudMetadata for this string is equalized in the case of Glassfish as vendors. The database for the file type Blob goes just created and attached to the application as its initialization is carried out internally by the library at the time of instantiation of the class GlassfishBlobManagerFactory .

Memcache Service
============


Even in this case not being present in a Glassfish implementation of such a service has been necessary to resort to external tools. First of all you must have a memecached server that implements the service locally. In the configuration section we will explain how to get one. As regards the actual implementation of the APIs that are meeting this problem, it is chosen to use the library spymemcached already used for the implementation of the service provider to the relatively Azure. In order to use work with the memcached server we also need to provide Glassifish extra modules . Even in this case , see the chapter on the setup of the architecture for more details.



Setup of the Architecture
============

NoSQL, SQL and Blob Services

The 3 services in the case of Glassfish make use of MySQL 5.6.15 DBMS. First thing to do then is to have a MySQL 5.6.15 server. Through this it will be necessary to create two databases, one for data and one for normal data type Blob providing the user with which you will access to MySQL all rights to the two databases created by using the command:


mysql grant all on database_name . * to ' account_name ' @ ' host_name '


In the case of deployment in local course host_name = localhost.
Once you have successfully created the backend , you will need to instruct Glassfish in order to interact with the newly created database through MySQL. To do this you will need :


- create on Glassfish two JDBC connection to the two created databases
- create a resources on Glassfish JDBC to use the  databases created for the table service

Mail Service

The use of the mail service does not impact in any way on the setup of the architecture and just goes properly configured.

MessageQueue Service

The service uses a JMS connection MessageQueue and specifies a JMS resource associated with each message queue you want to use as specified in queue.xml , recall that the CPIM via JNDI . The steps to configure Glassfish in order to use the service are therefore the following :

- create a JMS Connection
- create JMS resources as there are so many message queues used by the application.

This solution is obviously designed for deployment in local as the number and type of queue that the application uses , and thus in particular the number of JNDI resources to create, it should be known only at runtime by the application itself . Soon we will attempt to provide a more general solution.

Memcache Service

To use the service as you have already mentioned you need to use a dedicated server that implements the memcached protocol . This server can run locally or remotely. You can use any server that implements the protocol.

In particular, if you are running under windows systems you can download memcached-1.4.4 server directly from the following link:

http://s3.amazonaws.com/downloads.northscale.com/memcached-win64-1.4.4-14.zip

all you need to do at this point it's just to run the executable using administrator privilegies.

If you are running under MacOSX systems you can find instraction on how to get a memcached server at the following link:

http://www.journaldev.com/1/how-to-install-memcached-server-on-mac-oslinux

Configure Glassfish in order to work with the memcached server  following the instruction at the following link (skip the phase of adding a glass fish-web.xml file and of adding spymemcached library to Glassfish modules since it is already imported by the CPIM):

https://github.com/rickyepoderi/couchbase-manager/wiki/Installation


TaskQueue Service

Your use of the service TaskQueue not impact in any way on the setup of the architecture and just goes properly configured



Application Configuration
============


NoSQL Service

In order to use the service NoSQL is necessary to include in the META-INF folder persistence.xml file , properly configured.
The structure of the document in the case of Glassfish is the following :

    <persistence version = "1.0" xmlns = "     http://java.sun.com/xml/ns/persistence "
    xmlns: xsi = " http://www.w3.org/2001/XMLSchema-instance "
    xsi: schemaLocation = " http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_1_0.xsd ">

	<persistence-unit name = " MyPersistenceUnit " transaction- type = " JTA ??">

	<provider> org.eclipse.persistence.jpa.PersistenceProvider </provider>

	<jta-data-source> -IN- YOUR- RESOURCE GlassFish </jta -data- source>

	<class> LIST- OF- CLASS- TO- PERSIST </class>

	<exclude-unlisted-classes> false </exclude- unlisted -classes>

	<properties>

	<property name="javax.persistence.jdbc.password" value="DATABASE-USER" />

	<property name="javax.persistence.jdbc.user" value="DATABASE-PASSWROD" />

		<property name="javax.persistence.driver" value="com.mysql.jdbc.Driver" />

		<property name="javax.persistence.url" value="DATABASE-CONNECTION" />

		<property name="eclipselink.ddl-generation" value="create-tables" />

		<property name="eclipselink.logging.level" value="INFO" />

		<property name="account.name" value="DATABASE-USER" />

		<property name="account.key" value="DATABASE-PASSWORD" />

	</properties>

	</persistence-unit>

	</persistence>

The tags to be defined are shown in uppercase and are self-explanatory .
The repetition of the fields "DATABASE-USER " and " DATABASE-PASSWORD" is present in order to enable the CPIM to configure and use the service.

SQL and Blob Services

Place in the configuration.xml as seen in the section on , the connection strings to the database available on Glassfish after the creation of resources.

Mail Service

Normally indicate the SMTP server information and choose the account with which to log on as explained above in the section on the configuration.xml file

Message and Task Queue Services

Configure normally queue.xml as seen in the section on , remembering that in the case of Glassfish and delservizio MessageQueue must be specified for each message queue also <messagequeueconnection> tags and <messagequeueresource> with the values of the JMS connection and JMS resources created on Glassfish during the setup phase of the architecture .
For the service TaskQueue you will also indicate the tag in the backend configuration.xml , possibly to specify a backend on which you want to move the load computation.
In the case of Glassfish that tag will be filled with the URL of the servlet from a contact to be able to move the load computation application. If you do not use a backend but the computation is all on a single instance must in every case indicate the URL of the servlet local to our application , for the computation of the task .

Memcache Service

Just specify the address and port of the server intended to implement the service , in the specific tag configuration.xml file as seen in the section on.

In the templates folder there are also templates of the  3 configuration files needed to use the CPIM that summarize what has been said in the paragraphs relating to the structure of the file and in the latter part relating to configuration details specific to Glassfish  4.0 AS.
