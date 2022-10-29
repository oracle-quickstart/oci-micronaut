create table USERS (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    version BIGINT(19),
    username VARCHAR(100) NOT NULL,
    password VARCHAR(200) NOT NULL,
    salt VARCHAR(200) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(50),
    phone VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
create table USER_ADDRESSES (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    version BIGINT(19),
    user_id VARCHAR(36) NOT NULL,
    nmbr VARCHAR(200) NOT NULL,
    street VARCHAR(200) NOT NULL,
    city VARCHAR(200) NOT NULL,
    country VARCHAR(200) NOT NULL,
    postcode VARCHAR(200) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
create table USER_CARDS (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    version BIGINT(19),
    user_id VARCHAR(36) NOT NULL,
    nmbr VARCHAR(4) NOT NULL,
    long_num VARCHAR(16) NOT NULL,
    expires VARCHAR(4) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);