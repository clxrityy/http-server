# Java HTTP Server

This is the source code for the `mjanglin.com.java.httpserver` project.

> This project begun starting from the work of [**@CoderFromScratch**](https://github.com/CoderFromScratch), view his repository [here](https://github.com/CoderFromScratch/simple-java-http-server).

- [Project Structure](#project-structure)
    - [Root](#root)
    - [Source Code](#source-code)
        - [HTTP Server](#http-server)


## Project Structure

```cpp
|--*/
    |-- root/*
        |-- ... /** 
            Static files to send to the server 
            - index.html
            - style.css
            - script.js
            - image.png
            - ...
        */
    |-- src*/
        /**
            * The source code for the project.
        */
        |-- main*/
            |-- java/com/mjanglin/
                |-- http/ 
                    |-- ... /* Classes for HTTP protocols */
                |-- httpserver/
                    |-- config/
                        |--> ... /* Configuration classes */
                    |-- core/
                        |--> ... /* Core server classes */
                    |-- util/
                        |--> ... /* Utility classes */
                    |-- Main.java
            |-- resources/
                |-- ... /* Resource files */
        /**
            * The test code for the project.
        */ 
        |-- test*/java/com/mjanglin/
            |-- http/
                |-- ... /* Test classes for HTTP protocols */
            |-- httpserver/core/
                |-- io/
                    |--> ... /* Test classes for IO */
                |--> ... /* Test classes for server */
```

### Root

The [`root/`](/root/) directory is meant to be the web root of the server. This is where the server will look for static files to serve.

This is currently populated with a few example files to test the server.
- [**index.html**](root/index.html)
- [**style.css**](root/style.css)
- [**java.svg**](root/java.svg)
- [**test.json**](root/test.json)

You can reach any static file in this directory by navigating to `http://localhost:5006/<file>`.

---

### Source Code

The [`src/`](/src/) directory is where the source code for the project is located.

The main package is served under [`com.mjanglin.httpserver`](/src/main/java/com/mjanglin/httpserver/Main.java).

The [`com.mjanglin.http`](/src/main/java/com/mjanglin/http/) package contains classes for the HTTP protocol.

##### Destructured

#### HTTP Server

![Main Package](/src/README/img/httpserver.png)

- `config/`
    - [`Config.java`](/src/main/java/com/mjanglin/httpserver/config/Config.java)
        - The main configuration class for the server.
    - [`ConfigManger.java`](/src/main/java/com/mjanglin/httpserver/config/ConfigManager.java)
        - The configuration manager for the server.
        - Loads the configuration from the `config.json` file.
    - [`HttpConfigException.java`](/src/main/java/com/mjanglin/httpserver/config/HttpConfigException.java)
        - The class for raising exceptions related to the configuration.
- `util/`
    - [`Json.java`](/src/main/java/com/mjanglin/httpserver/util/Json.java)
        - The class for parsing JSON.
    - [`MimeTypes.java`](/src/main/java/com/mjanglin/httpserver/util/MimeTypes.java)
        - The class for getting the MIME type of a file.
- `core/`
    


### [<hbd>⬆</kbd>](#java-http-server)