# Set up the built-in MongoDB database

*This is only needed if you use the MongoDB setup included in the `compose.yaml` file. If you use your own instance, you don't have to perform these steps.*

Comment out the `springboot` service in the compose.yaml file like so:

    services:
    # springboot:
    #   build: .
    #   ports:
     #   - "8080:8080"
      mongodb:
    The rest of compose.yaml...

Write a root password where prompted.

Set a volume where the database will store its files. To do this, write a path on your computer, followed by `:/data/db`:

    volumes:
      - "./data:/data/db" # <-- In this example, all database files are stored under a 'data' folder in the project root.

Save and close the `compose.yaml` file.

Make sure you have a terminal running in the project folder.

Start the database with:

    docker compose up

> In case the database stops after a few seconds and the logs show 'permission denied' errors, try to give the user running docker (your user on Windows, the `docker` group on Linux) full access to the volume folder.

If the database runs for a few seconds without shutting down, stop it and restart it with:

    docker compose down
    docker compose up -d

Take note of the name of the container. 
Open a MongoDB shell using:

    docker exec -it <container's name> mongosh

This opens a MongoDB shell with the prompt `test>`.

Switch to the admin database and create a new user:

    use admin
    db.createUser({
      user: "<your username>",
      pwd: passwordPrompt(),
      roles: [{ role: "readWriteAnyDatabase", db: "admin" }]
    })
After pressing Enter, type your password.
If you get a response of `ok`, the user creation was successful.
Leave the shell with `exit`, then enter it again, this time as your new user. Use the password you provided earlier:

     docker exec -it <container's name> mongosh --username <your username> --password

In the shell, switch to a new database to create it:

    use <your database>

Type `exit` again and the database is ready for use.

Before continuing setup, make sure to remove the comments from the springboot service in compose.yaml:

    services:
      springboot:
        build: .
        ports:
          - "8080:8080"
      mongodb:
      The rest of compose.yaml...
