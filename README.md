# Bodrogi-backend

#### Backend software for a construction company

### ***Important: This software is currently in development. Some features are missing and bugs can be expected. To report any bugs, please ![Open an issue](https://github.com/otottkovi2/bodrogi-backend/issues/new/choose).***

## Building and deployment

### Requirements

- Login and admin pages as HTML files (with their corresponding CSS and JS files)
- A working ![Docker](https://docs.docker.com/get-started/get-docker/) installation with Docker Compose support
- *(optional)* A MongoDB database (included, but feel free to use your own)

### 1. Copy your frontend files

Copy your pages to their respective locations under `src/main/resources/static`. Each folder has a guide to tell you what should you copy where. Delete the guides once you're done.

### 2. *(optional)* Set up the built-in MongoDB database

*This is only needed if you use the MongoDB setup included in the `compose.yaml` file. If you use your own instance, skip to Step 3.*

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

This open
