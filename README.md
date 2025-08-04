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

# API for Radek

## Authentication

### Signup
Creating account
* **URL** `/signup/{role} , role = {"SELLER","BUYER"}`
* **METHOD** `POST`
#### Params
* `username`
* `email`
* `password`

**Content type** : **json** or **x-www-form-urlencoded**

#### Success Response(`200 OK`)

#### Failure Response(`400 Bad Request`)
Response params
* `status`
* `message` - reason why your request is bad
```json
{
    "status": 400,
    "message": "User with this email/username already exists"
}

```
### Signup
Logging 
* **URL** `/login`
* **METHOD** `POST`

**Content type** : `x-www-form-urlencoded`
#### Params
* `username`
* `password`

#### Success Response(`202 ACCEPTED`)
Returns session cookie
#### Failure Responses
(`404 NOT FOUND`)
Response params
* `status`
* `message`
```json
{
    "message": "Bad credentials",
    "status": 404
}
```


