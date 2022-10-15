CREATE TABLE user_profile(
  id SERIAL NOT NULL PRIMARY KEY,
  u_key VARCHAR(200) NOT NULL,
  u_value TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX user_profile_key ON user_profile(u_key);