# HTTP Server

A simple Java HTTP server

## Features

- [x] JSON configuration management
- [x] HTTP server connection socket
- [x] HTTP server-to-client listener thread
- [x] Support for file types
    - [x] Support for CSS files
    - [x] Support for JS files
    - [x] Support for HTML files
    - [x] Support for image files
- [ ] Support for routes
- [ ] Support for different HTTP methods
- [ ] Handle requests
- [x] Passing tests
- [x] Parsing headers of requests
- [ ] Parsing body of requests
- [ ] Parsing query parameters of requests
- [x] Thread pool support

## Usage

The files in [`root/`](/root/) will be served by the server.

- Simply run the `http-server.jar` file
    ```zsh
    java -jar http-server.jar
    ```
    - The server will start on port 5006 by default
    - The server will serve files from the `root` directory by default
    - The server will use the `http.json` file by default
    