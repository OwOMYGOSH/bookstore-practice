CREATE TABLE "author" (
    "id" BIGSERIAL PRIMARY KEY,
    "name" VARCHAR(50) NOT NULL,
    "bio" VARCHAR(50),
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "book" (
    "id" BIGSERIAL PRIMARY KEY,
    "title" VARCHAR(50) NOT NULL,
    "isbn" VARCHAR(50) NOT NULL UNIQUE,
    "price" DECIMAL NOT NULL,
    "stock" int NOT NULL,
    "published_at" DATE,
    "is_active" BOOLEAN DEFAULT TRUE,
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "author_id" BIGINT NOT NULL REFERENCES author(id)
);