[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) 
[![Build Status][travis-badge]][travis-badge-url] 
[![Quality Gate][sonarqube-badge]][sonarqube-badge-url] 
[![Technical debt ratio][technical-debt-ratio-badge]][technical-debt-ratio-badge-url] 
[![Coverage][coverage-badge]][coverage-badge-url] 

Introscope (CA APM) JAX-RS and Spring REST Java Agent Extension
===================================================================
The `rest.pbd` and `Rest.jar` are used to instrument **Spring** and **JAX-RS** REST web services in **Introscope**.
The `rest-client.pbd `is used for instrumenting Spring REST client to generate backend metrics and triage map.

## Build
You need [Maven](https://maven.apache.org/) to build the `Rest.jar`. Once you have Maven installed,
yo can navigate to `rest-introscope` project folder and execute the following command,

```
mvn clean install
```

Once the build is successful, you will find the `Rest.jar` under `target` folder.

## Deployment
Make the following changes to your Introscope agent after stopping the app/web server:

### rest.pbd, rest-client.pbd
1. Navigate to `wily/core/config` directory.
2. Copy `rest.pbd` to `wily/core/config` directory.
3. Edit the `IntroscopeAgent.profile`
4. Modify the `introscope.autoprobe.directivesFile` property by adding a new entry, `rest.pbd`, the comma-delimited list, e.g.,
`introscope.autoprobe.directivesFile=tomcat-typical.pbl,hotdeploy,rest.pbd, rest-client.pbd`

### Rest.jar
1. Copy `Rest.jar` to `wily/core/ext` directory.


## Running
1. Start your app/web server to pick up the new changes.
2. Exercise your Spring or JAX-RS REST application to generate Introscope metrics.

[travis-badge]: https://travis-ci.org/indrabasak/rest-introscope.svg?branch=master
[travis-badge-url]: https://travis-ci.org/indrabasak/rest-introscope

[sonarqube-badge]: https://sonarcloud.io/api/project_badges/measure?project=com.basaki%3Arest-introscope&metric=alert_status
[sonarqube-badge-url]: https://sonarcloud.io/dashboard/index/com.basaki:rest-introscope 

[technical-debt-ratio-badge]: https://sonarcloud.io/api/project_badges/measure?project=com.basaki%3Arest-introscope&metric=sqale_index
[technical-debt-ratio-badge-url]: https://sonarcloud.io/dashboard/index/com.basaki:rest-introscope 

[coverage-badge]: https://sonarcloud.io/api/project_badges/measure?project=com.basaki%3Arest-introscope&metric=coverage
[coverage-badge-url]: https://sonarcloud.io/dashboard/index/com.basaki:rest-introscope
