# jReality Eclipse Update site integration

This project aims to provide a maven / tychp controlled build process for P2 update sites. 

## Build process
### Clone jReality

First checkout any jReality clone from gitirous to a local folder. 

```
	JREALITY_SOURCE=<absolute path to root of clone>
	git clone http://	
```

The build itself is done by Maven. So no Ant build is needed.

### Clone bitub.jreality and set version number

Some these version numbers are only provided by jReality project's home. Since now, it is
set to 1.1.0. We can append a short head commit ID as build number to identify the source revision.

```
	cd $JREALITY_SOURCE
	NEW_VERSION_SNAPSHOT=1.1.0.`git rev-parse --short HEAD`
	mvn -Dtycho.mode=maven org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=$NEW_VERSION_SNAPSHOT
```

### Build OSGI compliant bundles and bundle cache

```
	mvn -Djreality.checkoutpath=$JREALITY_SOURCE clean install
```

Don't clean build before since it will clean the OSGI bundle cache, too.

### Build Eclipse P2 integration

```
	mvn -P platform-luna,build-p2 clean install
```

