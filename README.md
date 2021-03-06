# jReality Eclipse Update site integration

This project aims to provide a Maven / Tycho controlled build process for P2 update sites. It provides
no new packages. It only restructures the jReality packages in order to match the expectationa of the P2
installation / provisioning process of Eclipse.

## Experimental update site

Use the following URL within Eclipse P2 update mechanism:

http://bekraft.github.io/bitub.jreality-build/mars/

## Build process
### Clone jReality

First checkout any jReality clone from gitlab to a local folder. 

```
	JREALITY_SOURCE=<absolute path to root of clone>
	git clone https://gitlab.com/eddiejones/jreality.git
```
See http://www3.math.tu-berlin.de/jreality/ for further information.

Note:

jReality provides a Maven build support by its own within a separated branch. This Maven build is
an OSGI bundle build with P2 integration for Eclipse.

The build itself is done by Maven. So no Ant build is needed.

### Clone bitub.jreality and set version number

A version number is given by jReality project's home. It is
set to 1.1.0. We can append a short head commit ID as build number to identify the source revision.

```
	cd $JREALITY_SOURCE
	NEW_VERSION_SNAPSHOT=1.1.0.`git rev-parse --short HEAD`
	cd <back to build root>
	mvn -P build-osgi,build-p2 -Dtycho.mode=maven org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=$NEW_VERSION_SNAPSHOT
```

There are two disjoint profiles. "build-osgi" will repackage and compile an existing jReality clone into OSGI compliant bundles.
"build-p2" will take an existing OSGI compliant build and restructures it into a P2 update site. 

### Build OSGI compliant bundles and bundle cache

First run clean install with the predefined profile "build-osgi". It will build the OSGI bundles
and store them into a temporary cache which will serve as a simple unstructured P2 repository.

```
	mvn -P build-osgi -Djreality.checkoutpath=$JREALITY_SOURCE clean install
```

Actually, it is possible to install the bundles from repository cache. But there are 
no declared dependencies between these bundles at installation time. This task is done by the next
build step. 

### Build Eclipse P2 integration

First define the location of the public update site within the local system. Afterwards, run
clean install. Choose a proper profile (here "platform-mars") for your Eclipse platform.
The profile "build-p2" will add the remote and local repositories as well as the additional features.

```
    USITE=<absolute path to public update site>
	mvn -P platform-mars,build-p2 -Djreality.updatesiteLocal=$USITE clean install
```

By default, the local update site is placed into the the current user's profile at "./.p2/jreality".

### Installing in Eclipse

Add a new update site location (either local or remote) in Eclipse via Help/Install new software. 

## Packaging of jReality 

To allow a working dependency configuration, jReality has been repackaged into four different
installable units for Eclipse (features).

Since some of the dependencies are not available as Maven artifacts, all dependencies are taken
from original jReality clone (lib folder). The 3rd-party libraries are inlined into a new OSGI compliant
bundle. 

Only Jython has been separated as distinct bundle, since it is the biggest bundle.

### Core feature

The Core installable unit (feature for Eclipse) contains following bundles:

 - Core bundle (from original targets see below)
 - Core 3rd-party dependencies
 - Native libraries as fragments
   - Windows with specification win32_64
   - Linux with specification x86_64 (amd64)
   - (MacOS 64 in work)
   
The Core covers the original targets of 
 - core, proxies, tools, audio, soft, jogl, jogl3, gpgpu, renderman, backends-share
 
### Core 3rd-party dependencies

Original libraries from jReality:

 - jacknativeclient.jar (not via Maven)
 - libpd.jar (not via Maven)
 - NetUtil.jar 
 - hidapi-1.1.jar
 - jinput.jar 
 - smrj.jar
 - gluegen-rt.jar 
 - jogl-all.jar 
  
### IO feature

The IO feature includes Core; contains following additional bundles:

 - IO bundle
 - IO 3d-party dependencies
 
The IO unit covers the original targets of 
 - io
 
### IO 3rd-party dependencies

Original libraries from jReality:

 - antlr-3.4-complete.jar 
 - xstream-1.4.7.jar 
 - xpp3_min-1.1.4c.jar 
 - xmlpull-1.1.3.1.jar 
 - itextpdf- 5.3.2.jar
 
 
### UI feature

The UI feature includes Core and IO; contains following additional bundles:
 - UI bundle
 - UI 3rd-party dependencies
 
The UI unit covers the original targets of 
 - swing, plugin

### UI 3rd-party dependencies

Original libraries from jReality:

 - beans.jar
 - bsh.jar
 - colorpicker.jar
 - jrworkspace.jar

### Jython

 - Jython 2.5.3 

## ToDo

- MacOS natives fragment
