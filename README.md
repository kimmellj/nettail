
![Fluid, Inc](http://www.fluid.com/img/logo.jpg) NetTail
=======

A Java Desktop Application that can monitor web accessible log files using a similar process as tail.

This was a fun project created in my spare time at work in order to monitor web accessible log files for the Demandware Platform.  The log files for a specific Demandware server are password protected using HTTP Basic AUTH and are only accessible via HTTP or the WebDav protocol.

"Taiing" the log files is accomplished by specifying the size of content to retrieve using CURL. The application keeps track of how much content has been downloaded and how much more is needed on a specified interval.

This project is my first full Java Desktop application and feed back is always welcome. If you have an suggestion on how to make the application better or how to better structure some of the internal components, please let me know!

The following projects are used within this application:
* [http://hc.apache.org/httpcomponents-client-ga/](http://hc.apache.org/httpcomponents-client-ga/)
* [http://commons.apache.org/proper/commons-codec/](http://commons.apache.org/proper/commons-codec/
* [http://commons.apache.org/proper/commons-logging/](http://commons.apache.org/proper/commons-logging/)
* [https://java.net/downloads/appbundler/appbundler.html](https://java.net/downloads/appbundler/appbundler.html)

## Features
* Locally store any number of saved favorites
	* A favorite is a saved site configuration
* View a list of any files ending in .log or .txt in a web accessible folder
* View the contents of any selected log file
* Monitor any changes on configurable repeat interval

## Building the application

**Requirements**

* Java JDK 1.7
* Apache Ant
* The Java Home Environment Variable needs to be set to a valid JDK Installation

Build are executed by using the Ant build tool [http://ant.apache.org/](http://ant.apache.org/).

**Normal Build**

```
ant

# Built application will be in the dist/ folder
```

**Bundle application into a single distributable jar file**

```
ant
ant build-single-jar

# Bundled application will be in the store/ folder
```

**Bundle application into an executable OSX application**

[http://docs.oracle.com/javase/7/docs/technotes/guides/jweb/packagingAppsForMac.html](http://docs.oracle.com/javase/7/docs/technotes/guides/jweb/packagingAppsForMac.html)

```
ant
ant build-single-jar
ant build-osx-application

# Bundled application will be in the store/ folder
```
