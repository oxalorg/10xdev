# HTTP server in C

## How should an HTTP server work?

At it's core an http server does the following:
- creates a `socket` which can be used for listening to incoming connections
- `bind` this socket on to a local machine port eg: 8080
- `listen`s for new connections on this socket
- when new connection is found, it `accept`s that connection and binds it to a local machine port
- read and write from the socket


