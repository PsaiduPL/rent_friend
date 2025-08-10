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
    "id": 101,
    "role": "SELLER",
    "name": "elna.emmerich",
    "age": 19,
    "city": "ŁÓDŹ",
    "description": "Revenge fantasies never work out the way you want.",
    "gender": "male",
    "bodyParameter": {
      "height": 188.0,
      "weight": 87.0
    },
    "interestList": [
      {
        "id": 7,
        "interest": "Czytanie książek"
      },
      {
        "id": 20,
        "interest": "Joga"
      }
    ],
    "profilePicture": {
      "profileURL": "/img/feba69da-ff52-4674-8470-3bd56f10f1ca"
    }
  },
  "offers": [
    {
      "id": 168,
      "title": "aaaaaaaaaaa",
      "description": "aaaaaaaaaaaaaaaa",
      "pricePerHour": 12.0,
      "creationDate": "2025-08-10"
    }
  ]
}
```
If profile doesnt exists
**Success Response(`NoContent` 204)**

### Update your profile
* **URL** `/profile`
*  **METHOD** `PUT`

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
#### `Remeber to leave the data you dont change, everything is updated`

#### Success response (`NoContent` 204)

#### Failure response (`Not Found`404)

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
        "pricePerHour": 50.5,
      "creationDate": "2025-08-7"
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
        "pricePerHour": 50.5,
        "creationDate": "2025-08-07"
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

## Interest

### Get interest list `pageable`

* **URL** `/interests` 
* **METHOD** `GET`

#### Pageable modiferies

`/interests?page=?&size=?&sort=?`
Example
* **page=2**
* **size=10**
* **sort=field,(ASC|DESC)** example : `sort=interest,DESC`
#### Success Response(`OK` 200)
```json

{
  "pages": 8,
  "pageSize": 5,
  "interests": [
    {
      "id": 16,
      "interest": "Rysowanie"
    },
    {
      "id": 17,
      "interest": "Szydełkowanie"
    },
    {
      "id": 18,
      "interest": "Robienie na drutach"
    },
    {
      "id": 19,
      "interest": "Taniec"
    },
    {
      "id": 20,
      "interest": "Joga"
    }
  ]
}
      
```

## Searching profiles


## **Profile**

### Get profiles `pageable`
* **URL** `/profiles`
* **METHOD** `GET`

Search profiles which match filters and sorting
#### **Pageable and filter modifiers**

`?page=?&size=?&sort=?` - **pageable**

`city=?&gender=?&maxAge=?&minAge=?` - **filters**

**Example**
* **sort=age,ASC**
* **city=KRAKÓW**
* **gender=female**
* **maxAge=35**
* **minAge=28**
* **page=0**
* **size=10**

#### **Example usage**
`http://localhost:8080/profiles?size=2&page=1&sort=age,ASC&city=KRAKÓW&gender=fedmale&maxAge=100&minAge=18`
#### **Success Response (`OK` 200)**
```json
{
  "pages": 3,
  "pageSize": 20,
  "profilesPreview": [
    {
      "id": 11,
      "joinedIn": "2025-08-10",
      "url": "/profiles/11",
      "name": "wes.hansen",
      "age": 37,
      "city": "OPOLE",
      "gender": "female",
      "top3InterestsList": [
        {
          "id": 10,
          "interest": "Wędkarstwo"
        },
        {
          "id": 12,
          "interest": "Majsterkowanie"
        },
        {
          "id": 19,
          "interest": "Taniec"
        }
      ],
      "top3offerPreviewList": [
        {
          "title": "You sort of start thinking anything’s possible if you’ve got enough nerve.",
          "pricePerHour": 174.6
        }
      ],
      "profilePicture": null
    },
    {
      "id": 12,
      "joinedIn": "2025-08-10",
      "url": "/profiles/12",
      "name": "thuy.zieme",
      "age": 22,
      "city": "TORUŃ",
      "gender": "male",
      "top3InterestsList": [
        {
          "id": 37,
          "interest": "Medytacja"
        },
        {
          "id": 30,
          "interest": "Ceramika"
        },
        {
          "id": 29,
          "interest": "Rzeźbiarstwo"
        }
      ],
      "top3offerPreviewList": [
        {
          "title": "It takes a great deal of bravery to stand up to our enemies, but just as much to stand up to our friends.",
          "pricePerHour": 139.2
        }
      ],
      "profilePicture": null
    },
```

#### **Failure response (`BAD REQUEST` 400)**
Usually bad filters
```json
{
  "status": 400,
  "message":  "wrong min age"
}
```

## Profile picture

### Post profile picture 
* **URL** `/img`
* **METHOD** `POST`
* **Content type** `multipart form data`
```
file : image.jpg
```
**Example react**
```js
export const uploadProfilePicture = (file) => {
   
    const formData = new FormData();
    formData.append('file', file);

 
    return apiService.post('/img', formData);
};
```
#### Success response(`Created`201)
* `Location` - uri to file `relative` example `/img/feba69da-ff52-4674-8470-3bd56f10f1ca`

#### Failure response(`Bad request`400)
```json
{
  "status": 400,
  "message":  "Image already exists"
}
```

### Delete profile picture

* **URL** `/img/{uuid}` - id of your picture
* **METHOD** `DELETE`

#### Success response(`No content` 204)

#### Failure response()
```json
{
  "status": 404,
  "message":  "Image not found"
}
```

### Get profile picture
* **URL** `/img/{uuid}` - id of your picture
* **METHOD** `GET`

#### Success response(`Ok`200)
Image file

#### Failure response (`Not found` 404)
```json
{
  "status": 404,
  "message":  "Image not found"
}
```

