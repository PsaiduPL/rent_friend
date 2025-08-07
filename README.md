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

## Profile

### Create profile

* **URL** `/profile`
* **METHOD** `POST`

#### Example request
```json
{
    "name":"John Mielon", - max 50 char
    "age":18, - 18 - 100
    "city":"Krakow", - max 150 char
    "description":"giga chad from cracow", - max 1500 char
    "gender":"female", 
    "bodyParameter":{
        "height":190.5,
        "weight":120.2
    },
    "interestList":[
        {
            "id":1
        },
        {
            "id":2
        }
    ]
}
```
`bodyParameter` is optional 

#### Response success (CREATED `201`)
* `Location` - uri to profile in public profile search endpoint example : `/profiles/3`


#### Response failure (BAD REQUEST `401`)
Example
```json
{
  "status" : 401,
  "message" : "name cannot be blank"
}
```


### Get your own profile
* **URL** `/profile`
* **METHOD** `GET`

**Example Success Response(`OK` 200)**

```json
{
    "profile": {
        "id": 2,
        "role": "BUYER",
        "name": "Hot juan",
        "age": 19,
        "city": "WARSAW",
        "description": "Young lady from warsaw",
        "gender": "female",
        "bodyParameter": {
            "height": 160.5,
            "weight": 75.2
        },
        "interestList": [
            {
                "id": 1,
                "interest": "Bieganie"
            },
            {
                "id": 2,
                "interest": "Jazda na rowerze"
            }
        ]
    },
    "offers": []
}
```
If profile doesnt exists
**Success Response(`NoContent` 204)**

## Offers `for SELLER role only`

### Create offers
* **URL** `/profile/offers`
*  **METHOD** `POST`

#### Example request

```json
{
  "title" : "wyjazd na narty", - max 250 char
  "description" : "czesc czy chcialby ktos w ten weekend pojechac na narty", - max 2500
  "pricePerHour" : 40 - 1 - 99999
}
```
#### Success response( `Created` 201)
* `Location` - uri to offer in your profile example : `/profile/offers/2`

#### Failure response (`Bad Request` 400)
```json
{
  "status" : 400,
  "message" : "reason of failure"
}
```

### Get offers
* **URL** `/profile/offers`
* **METHOD** `GET`

#### Example Success Response (`OK` 200)
```json
[
    {
        "id": 1,
        "title": "Przejscie na spacer",
        "description": "Tylko w godzinach 17 - 20",
        "pricePerHour": 50.5
    }
]
```
### Update offer
* **URL** `/profile/offers/{id}` , id - id of offer you want to update
* **METHOD** `PUT`

#### Example request `Remeber that your request change all data so if you dont want to change something send the same as it was before`
```json
{
  "title" : "wyjazd na narty w gory", - max 250 char
  "description" : "czesc czy chcialby ktos w ten weekend pojechac na narty", - max 2500
  "pricePerHour" : 40 - 1 - 99999
}
```

#### Success response(`OK` 200)

#### Failure response (`Not found` 404)

#### 2 Failure response (`Bad Request` 400)

### Get single offer
* **URL** `/profile/offers/{id}` , id - id of offer you want to get
* **METHOD** `GET`

#### Success response (`OK` 200)
```json
  {
        "id": 1,
        "title": "Przejscie na spacer",
        "description": "Tylko w godzinach 17 - 20",
        "pricePerHour": 50.5
    }

```
#### Failure response (`NOT FOUND` 404)
```json
{
  "status" : 404,
  "message" : "offer doesnt exists"
}
```

### Delete offer
* **URL** `/profile/offers/{id}` , id - id of offer you want to get
* **METHOD** `DELETE`

#### Success response (`NO CONTENT` 204)

#### Failure response (`NOT FOUND` 404)
```json
{
  "status" : 404,
  "message" : "offer doesnt exists"
}
```
