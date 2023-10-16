CREATE TABLE handle (
    user_handle VARCHAR(200) PRIMARY KEY,
    type VARCHAR(55),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT type_id_constraint UNIQUE (type, user_handle)
);

CREATE TABLE "user" (
    user_handle VARCHAR(200) PRIMARY KEY,
    email VARCHAR(200) NOT NULL,
    image_url VARCHAR(200) NOT NULL,
    provider VARCHAR(50) DEFAULT 'local' NOT NULL,
    pword VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT user_email_constraint UNIQUE (email),
    FOREIGN KEY (user_handle) REFERENCES handle (user_handle)
);


CREATE TABLE user_profile (
    user_handle VARCHAR(200) PRIMARY KEY,
    first_name VARCHAR(200) NOT NULL,
    last_name VARCHAR(200) NOT NULL,
    dob DATE NOT NULL,
    FOREIGN KEY (user_handle) REFERENCES handle (user_handle)
);
