## Development with docker
If you choose to develop this plug-in using Docker,
you of course need to install Docker.
Other than that, nothing is absolutely required, but I would
still recommend installing OpenJDK 21 and IntelliJ or Eclipse.

### Running a test server
Run the following commands to test the plug-in on 
minecraft 1.12: 
- `docker build --file docker/run-1.12/Dockerfile --tag run-server --target generated .`
- `docker run -p 25565:25565 -it run-server java -jar server.jar`

When you want to run a different minecraft version,
you should replace the `run-1.12` in the first command
with the desired minecraft version.

Note: only MC 1.12 and 1.21 are supported right now.
More versions may follow later.

Note: do **not** run the second command with git bash,
since that will probably give the following error:
```
the input device is not a TTY.  If you are using mintty, try prefixing the command with 'winpty'
```

TODO Editor