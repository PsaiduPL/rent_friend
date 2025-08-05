DROP TABLE IF EXISTS users CASCADE ;
DROP TABLE IF EXISTS profiles;

CREATE TABLE users(
  id BIGSERIAL PRIMARY KEY ,
  role varchar(20) not null,
  username varchar(50) not null unique,
  email varchar(100) not null,
  password varchar(255) not null
);

CREATE TABLE profiles
(
    id   BIGSERIAL PRIMARY KEY ,
    age  integer NOT NULL,
    city VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    name VARCHAR(255) NOT NULL,
    user_id bigint NOT NULL ,
    CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES users(id)
);
CREATE INDEX idx_profiles_user_id ON profiles(user_id);


CREATE INDEX idx_profiles_city ON profiles(city);