[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) 
[![Build Status][travis-badge]][travis-badge-url] 
[![Quality Gate][sonarqube-badge]][sonarqube-badge-url] 
[![Technical debt ratio][technical-debt-ratio-badge]][technical-debt-ratio-badge-url] 
[![Coverage][coverage-badge]][coverage-badge-url] 

Introscope (CA APM) JAXRS and Spring REST Java Agent Extension
===================================================================
The `rest.pbd` and `Rest.jar` are used to instrument **Spring** and **JAXRS** REST web services in **Introscope**.
The `rest-client.pbd `is used for instrumenting Spring REST client to generate backend metrics and triage map.

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
2. Exercise your Spring and JAXRS REST application to generate Introscope metrics.

[travis-badge]: https://travis-ci.org/indrabasak/rest-introscope.svg?branch=master
[travis-badge-url]: https://travis-ci.org/indrabasak/rest-introscope

[sonarqube-badge]: https://sonarcloud.io/api/badges/gate?key=com.basaki.example:rest-introscope
[sonarqube-badge-url]: https://sonarcloud.io/dashboard/index/com.basaki.example:rest-introscope 

[technical-debt-ratio-badge]: https://sonarcloud.io/api/badges/measure?key=com.basaki.example:rest-introscope&metric=sqale_debt_ratio
[technical-debt-ratio-badge-url]: https://sonarcloud.io/dashboard/index/com.basaki.example:rest-introscope 

[coverage-badge]: https://sonarcloud.io/api/badges/measure?key=com.basaki.example:rest-introscope&metric=coverage
[coverage-badge-url]: https://sonarcloud.io/dashboard/index/com.basaki.example:rest-introscope
