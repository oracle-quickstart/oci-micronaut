create table USERS (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    version NUMBER(19),
    username VARCHAR2(100) NOT NULL,
    password VARCHAR2(200) NOT NULL,
    salt VARCHAR2(200) NOT NULL,
    first_name VARCHAR2(100) NOT NULL,
    last_name VARCHAR2(100) NOT NULL,
    email VARCHAR2(50),
    phone VARCHAR2(50),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
create table USER_ADDRESSES (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    version NUMBER(19),
    user_id VARCHAR2(36) NOT NULL,
    nmbr VARCHAR2(200) NOT NULL,
    street VARCHAR2(200) NOT NULL,
    city VARCHAR2(200) NOT NULL,
    country VARCHAR2(200) NOT NULL,
    postcode VARCHAR2(200) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);
create table USER_CARDS (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    version NUMBER(19),
    user_id VARCHAR2(36) NOT NULL,
    nmbr VARCHAR2(4) NOT NULL,
    long_num VARCHAR2(16) NOT NULL,
    expires VARCHAR2(4) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);