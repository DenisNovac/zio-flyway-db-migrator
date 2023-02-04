CREATE TABLE users(
  u_id INTEGER NOT NULL PRIMARY KEY,
  u_key VARCHAR(200) NOT NULL,
  u_value TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX user_profile_key ON users(u_key);

CREATE TABLE statuses (
  u_id integer REFERENCES users(u_id),
  u_status VARCHAR(200) NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

-- H2 does not support UNIQUE constraint inside the CREATE TABLE
ALTER TABLE
  statuses
ADD
  CONSTRAINT u_id_unique UNIQUE(u_id)