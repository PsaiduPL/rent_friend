# rent_friend

## How to connect to database from terminal
Run spring from folder where is pom.xml and type
```bash
 ./mvnw spring-boot:run 
 docker ps -- to find docker name
 docker exec -it nazwa_dockera -U user -d rentfriend 
```
Port of our db is **5433** because of mapping in docker
compose
or just use if you have **postgres downloaded**
```bash
psql -U user -d rentfriend -p 5433
```

## Useful wesites
> https://www.conventionalcommits.org/en/v1.0.0/


