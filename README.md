# MET-CS622-ChatBot-Project-Backend


### OOPIFY idea

1. Composite pattern for database and database holder to add data into all of them
1. Observer pattern for when new data entry is found and want to add it to all databases.


### Get started

1. MongoDB
    - Run docker container for mongodb
    ```shell script
    docker run -d -p 27017:27017 mongodb:3.6-xenial
    ```

1. MySQL
    - Run docker container for MySQL
    ```shell script
    docker run --name mysql57 -p 3306:3306 -e MYSQL_ROOT_PASSWORD=admin -e MYSQL_USER=admin -e MYSQL_PASSWORD=admin -e MYSQL_DATABASE=sensordata -d mysql/mysql-server:5.7
    ```
   - Access shell inside mysql container
    ```shell script
    docker exec -it mysql57 bash
    ```
   - Access mysql commands inside container shell
    ```shell script
    mysql -h localhost -u root -p
    ```