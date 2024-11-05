# Source Code

---

## [main](./main/java/com/mjanglin/httpserver/Main.java)

##### Functionality

- Loads the configuration file with the [Config Manager]().
    - Creates the `config`
    - Logs server starting with the specified ports from the configuration file
- Initializes the [Server Listener Thread]()
    - Starts the server listener


### [core](/src/main/java/com/mjanglin/httpserver/config/)

#### [HTTP Connection Worker](/src/main/java/com/mjanglin/httpserver/core/HttpConnectionWorker.java)


##### Functionality

- Takes in a `Socket` as a parameter
- Uses a `BufferedReader` to read the input stream
    - Passing in the socket's input stream
- Uses a `PrintWriter` write the output strema
    - Passing in the socket's output stream
- Runs an `@Override` function
    - Writes headers to the output stream to indicate HTML

#### [Server Listener Thread](/src/main/java/com/mjanglin/httpserver/core/ServerListenerThread.java)

##### Functionality
- Takes in a port & webroot parameter
- Initializes the `ServerSocket` with the given port
- Runs an `@Override` function
    - Initalizes the [HttpConnectionWorker](#http-connection-worker) by passing in the client from the server socket's `accept()` function.
    - Closes the server socket.

### [config](./main/java/com/mjanglin/httpserver/config/Config.java)

##### Functionality
- Has functions to get/set the specified config values
    - port
    - webroot

#### [Config Manager](/src/main/java/com/mjanglin/httpserver/config/ConfigManager.java)

