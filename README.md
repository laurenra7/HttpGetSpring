# HttpGetSpring
Do an HTTP GET from a URL using Spring Framework [RestTemplate](https://docs.spring.io/spring/docs/current/javadoc-api/index.html?org/springframework/web/client/RestTemplate.html).

See [REST in Spring 3: RestTemplate](https://spring.io/blog/2009/03/27/rest-in-spring-3-resttemplate) for tutorial and examples.

Spring uses the Apache [HttpComponents](http://hc.apache.org/) [HttpClient](http://hc.apache.org/httpcomponents-client-ga/index.html) for the HTTP requests.

### Build

Build with [Maven](https://maven.apache.org/).

```
mvn clean install
```

Produces an executable .jar file

```
/target/HttpGetSpring.jar
```


### Run

```
java -jar HttpGetSpring.jar
```


### Options

```
usage: java -jar httpGetTest.jar url [-h] [-o <filename>] [-v]

Do an HTTP GET from a URL

 -h,--help                Show this help
 -o,--output <filename>   output file
 -v,--verbose             show HTTP headers and processing messages

Examples:

  java -jar httpGetTest.jar https://someurl.com/get/stuff

  java -jar httpGetTest.jar -o myfile.txt https://someurl.com/get/stuff
```
