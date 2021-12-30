# appStore

## Description
API for an app store where the user can register apps with name, price and type. The application provides search by 
name and/or type, and by type ordered by lower price.

## Usage
The application can be run 
by using docker-compose. 
If not already you can follow these [instructions](https://docs.docker.com/engine/install/).

Once installed, in the application main folder, run
`$ docker-compose up --build`

Then access the API via the url
 * For a list of all apps `GET localhost:8080/api/v1/apps`
 * To save a new valid app `POST localhost:8080/api/v1/apps`
   * A valid app is one with all three attributes non-null and not empty, and zero or positive price value 
 * To search by name and/or type with pagination `GET localhost:8080/api/v1/apps/byNameAndType`
 * To search by type ordered by lower prices with pagination `GET localhost:8080/api/v1/apps/byTypeAndLowerPrice`