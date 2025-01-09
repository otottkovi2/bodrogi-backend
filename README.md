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

*This is only needed if you use the built-in MongoDB database. Otherwise skip to step 3.
Follow the instructions listed ![here](https://github.com/otottkovi2/bodrogi-backend/blob/readme-docs/builtin_db_setup.md).

### 3. Provide database details to the server

Open `src/main/resources/application.properties`:

Fill in the required information.

Explanation of the fields:

Field | Value for the built-in database | Value for other instances
|-----|---------------------------------|---------------------------|
spring.data.mongodb.host | The name of the MongoDB container. | The location of your database.
spring.data.mongodb.username | The name of the created user | The name of the user you want to use to access the database.
spring.data.mongodb.password | The password of the created user. | The password of the user you want to use to access the database.
spring.data.mongodb.database | The name of the created database. | The name of the database you want to use.

Save the file.

### 4. Build and start the server

To build and start the server, run:

    docker compose up -d

The build process takes a while, but only needs to run once.

>Note: If for some reason need to rebuild the server, run `docker compose build` as simply starting the server won't start a new build.

###### This project is not affiliated with Bodrogi Bau Kft.
