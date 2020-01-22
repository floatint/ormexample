
CREATE TABLE IF NOT EXISTS users(
    id INTEGER AUTO_INCREMENT,
    first_name NVARCHAR(512) NOT NULL,
    last_name NVARCHAR(512) NOT NULL,
    phone INTEGER NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS transactions(
    id INTEGER AUTO_INCREMENT,
    user_id INTEGER NOT NULL,
    value REAL,
    date DATE,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS accounts(
  id INTEGER AUTO_INCREMENT,
  balance REAL,
  transaction_id INTEGER,
  PRIMARY KEY (id),
  FOREIGN KEY (transaction_id) REFERENCES transactions(id)
);

CREATE TABLE IF NOT EXISTS account_transactions(
    id INTEGER AUTO_INCREMENT,
    account_id INTEGER  NOT NULL,
    transaction_id INTEGER NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (account_id) REFERENCES accounts(id),
    FOREIGN KEY (transaction_id) REFERENCES transactions(id)
);

CREATE TABLE IF NOT EXISTS user_accounts(
    id INTEGER AUTO_INCREMENT,
    user_id INTEGER NOT NULL,
    account_id INTEGER NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);