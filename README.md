# chatty-pie-connector

A minimal example of a connector using the service-integration SDK

## Requirements
* Java 8
* Docker
* Mysql running on `localhost:3306` with `root/password`

## Running the application
### In the IDE
* run `ChattyPieConnectorApplication`

### On the command-line
* `./mvnw spring-boot:run`

This starts the connector application's http server and exposes the main
endpoint `localhost:8080/api/v1/integration/processEvent` which a marketplace
can send event notifications to.

### Changing the default port
* By default, it starts on port `8080`.
* To override, add the `-Dserver.port=XXXX` jvm arg

### Starting dependencies locally
By default the application is expecting a MySQL instance to be running
at `localhost:3306` and a smtp server running on `localhost:3025`.
If you do not have both, we offer a docker compose file at the root. Run it with

    docker-compose up

## Accessing the app
The chatty-pie-connector is deployed in 3 environments: DEV, TEST and PROD
* The DEV instance is available at https://dev-cpc.devappdirect.me
* The TEST instance is available at https://test-cpc.devappdirect.me
* The PROD instance is available at https://cpc.appdirect.com

To verify that the server is running correctly, check if a GET request at the `/health`
endpoint of a running CPC instance. For example, the endpoint for the dev 
[https://dev-cpc.devappdirect.me/health](https://dev-cpc.devappdirect.me/health)
returns 200(OK)

## Adding a new field to the database
Let's say you want to add a new column to the `company_account` table and expose it as a field in the `CompanyAccount` bean.
Let's walk through the process required for that.

### Create Flyway migration
* Add `.sql` migration file in `src/main/resources/db.migration/`: this new file should contain the addition of your new column.
* Run the migrations against your local db. To do this, start the app - `./mvnw spring-boot:run`.

### Regenerate the beans
* Classes such as `CompanyAccount` and `ChatroomCreationRecord` are beans generated by [`QueryDSL`](http://www.querydsl.com/) based on your database tables.
* When making table changes, you need to regenerate the beans so they reflect the new table state.
* Regenerate the beans with: `mvn clean generate-sources -DskipDbCodeGeneration=false`
    * This assumes a default db at localhost:3306 with `root/password`.
    * Optional params available to override those defaults are:
        * `-Dmodel.source.db.username=[your-local-db-user]`
        * `-Dmodel.source.db.password=[your-local-db-password]`
        * `-Dmodel.source.db.url=[connection-string-for-your-db-here]`
* The updated sources will be regenerated in `src/main/java/com/chattypie/persistence/model`
* Commit the updated files like any other sources.
* Note: keep in mind this entire package is autogenerated, so do not edit any code in it: your changes will be overridden the next time someone regenerates the beans.

## Dependencies version update
Using the [`versions` maven plugin](http://www.mojohaus.org/versions-maven-plugin/), you can keep
your dependencies & maven plugins up-to-date. Most of those commands directly modify your `pom.xml`.
* update the `spring-boot` parent version: `./mvnw versions:update-parent`
* update all dependencies: `./mvnw versions:use-latest-releases`
    * Warning: this takes _15 minutes_ due to each dependency polling `artifactory.appdirect.com` (and it seems as if that is very slow)
* display available maven plugin updates `./mvnw versions:display-plugin-updates`
    * If newer versions are available, you will need to manually update `pom.xml`.

## Deployment procedure:
The instructions for deploying the application vary according to the target environment. Check them out at the 
[project wiki](https://github.com/AppDirect/chatty-pie-connector/wiki/Deployment-Procedure) 

## TestRails
* [We have some testrails here](https://appdirect.testrail.com/index.php?/suites/view/98&group_by=cases:section_id&group_id=2139181&group_order=asc)
