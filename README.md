# HELIOS Personal Data Storage API #

## Introduction ##

Personal Data Storage API is used to provide certain unified file storage
abstraction for HELIOS applications without binding these APIs to specific
storage location. Experiment using HTTP and File based transports. Unfied
API interface should be defined that will utilize underlying access APIs.
Because we are expected to also have remote storage the whole API should
be asynchronous.

HELIOS Personal Data Storage API is one of the HELIOS Core APIs as
highlighted in the picture below:

![HELIOS Personal Data Storage API](https://raw.githubusercontent.com/helios-h2020/h.core-Storage/master/doc/images/helios-storage.png "Personal Data Storage API")

Personal Data Storage API is implemented in HELIOS Core component that
is called Personal Data Storage Manager.

## API usage ##

See javadocs in [javadocs.zip](https://raw.githubusercontent.com/helios-h2020/h.core-Storage/master/doc/javadocs.zip).

### Introduction ###

Personal Data Storage API is used to store and load data objects using
HELIOS persistent personal data store. HELIOS personal data store is a
location agnostic storage concept. The storage can be either local or
remote storage. Applications are expected to use asynchronous access
functions also for local storage in order to provide the same API both
for local and remote storage. At the moment there is an implementation
for local storage and WebDAV-based remote storage.

HELIOS Personal Data Storage API supports four basic file access
operations:

* **Download** - download data objects from the repository
* **Upload** - upload data objects to the repository
* **List** - list content of the repository
* **Delete** - remove data objects from the repository

### HeliosStorageManager ###

Applications are expected to use `HeliosStorageManager` singleton
class to access HELIOS Personal Data Storage. Applications should call
`getInstance()` method in order to get a singleton instance:

`HeliosStorageManager storage = HeliosStorageManager.getInstance();`

HeliosStorageManager can then be used to access HELIOS Personal Data
Storage.  It is also possible to include a listener class as a
parameter to these methods. The listener class member function
implementing the required interface can be called when the operation
is completed. The listener parameter can also be null, which means
that there is no callback function and operation completion is not
notified.

An object can be requested from the HELIOS Personal Data Storage using
a download method:

`storage.download("object_name", listener);`

An object can be uploaded to the HELIOS Personal Data Storage using an
upload method:

`storage.upload("object_name", data, listener);`

An object can be removed from the HELIOS Personal Data Storage using a
delete method:

`storage.delete("object_name", listener);`

HELIOS Personal Data Storage objects can be listed using a list
command:

`storage.list("object_name", listener);`

Local Android filesystem is used as the default storage location. There is
also WebDAV-based remote storage alternative. HeliosStorageManager has
a method setAccessMethod that can be used to switch between local
filesystem based storage and WebDAV-based remote storage. There is also
setCredentials method that can be used to set username and password for
WebDAV storage access. WebDAV base URL should be included as a part of
pathname when accessing WebDAV storage.

### HeliosStorageUtils ###

There is also HeliosStorageUtils utility class that is used in HELIOS
test client to access local filesystem based HELIOS Personal Data
Storage. These functions are used in order to provide a layer that
can be ported to support also other Personal Data Storage implementations
instead of directly binding the implementation to Android File operations.

### Listener interfaces ###

As HELIOS Personal Data Storage mechanism can be either local and remote
repository, it is necessary that operations are done asynchronously and
caller is notified when the operation is ready. There are three listener
interfaces:

* **DownloadReadyListener** - Returns downloaded content as buffer that is
  given as a parameter to the call.
* **ListingReadyListener** - Returns an array of strings to the variable
  that is given as parameter to the call.
* **OperationReadyListner** - Returns a status value to the variable
  that is given as parameter to the call. Negative value is an error.
  This listener is used with upload and delete opertions.

### Access method implementations ###

There are separate classes for Delete, Download, List, and Upload
operations both for local filesystem and WebDAV based implementations
with class names {File,Dav}Content{Delete,Download,List,Update}.
Operations are implemented based on the use of Android AsyncTask
abstract class.

### Testing ###

There is a test application (see app subdirectory) that also gives an
example of the API usage. The application can be used to walk-through
basic functionality of the API by clicking multiple times the floating
action button of the application.

There are also instrumented unit tests for local and WebDAV basic
operations and unit tests for HeliosStorageUtils class.

### Future work ###

The current implementation is using either hardcoded local filesystem
based implementation or WebDAV-based remote storage. HELIOS Profile
Manager could be used to query user's settings that can be used to
specify location of the personal storage. Extension modules can be
used to provide alternative storage implementations. Some kind of
registering mechanism is then needed.

The current implementation is based on classic Android AsyncTask design
pattern that was deprecated in Android 11. Deprecated API still works
in Android but future implementation should use java.util.concurrent
based implementation instead of AsyncTask.

## Multiproject dependencies ##

HELIOS software components are organized into different repositories
so that these components can be developed separately avoiding many
conflicts in code integration. However, the modules may also depend
on each other. However, `Storage` module does not depend on other
HELIOS projects.

### How to configure the dependencies ###

To manage project dependencies developed by the consortium, the
approach proposed is to use a private Maven repository with Nexus.

To avoid clone all dependencies projects in local, to compile the
"father" project. Otherwise, a developer should have all the projects
locally to be able to compile. Using Nexus, the dependencies are
located in a remote repository, available to compile, as described in
the next section.  Also to improve the automation for deploy,
versioning and distribution of the project.

### How to use the HELIOS Nexus ###

Similar to other dependencies available in Maven Central, Google or
others repositories. In this case we specify the Nexus repository
provided by Atos:

`https://builder.helios-social.eu/repository/helios-repository/`

This URL makes the project dependencies available.

To access, we simply need credentials, that we will define locally in
the variables `heliosUser` and `heliosPassword`.

The `build.gradle` of the project define the Nexus repository and the
credential variables in this way:

```
repositories {
        ...
        maven {
            url "https://builder.helios-social.eu/repository/helios-repository/"
            credentials {
                username = heliosUser
                password = heliosPassword
            }
        }
    }
```

And the variables of Nexus's credentials are stored locally at
`~/.gradle/gradle.properties`:

```
heliosUser=username
heliosPassword=password
```
To request Nexus username and password, contact Atos.

### How to use the dependencies ###

To use the dependency in `build.gradle` of the "father" project, you
should specify the last version available in Nexus, related to the
last Jenkins's deploy.

## Android Studio project structure ##

This Android Studio Arctic Fox 2020.3.1 Patch 2 project contains the
following components:

* app - Personal Data Storage API test application

* doc - Additional documentation files

* lib - Personal Data Storage API implementation
