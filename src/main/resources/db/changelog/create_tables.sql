CREATE TABLE tb_user (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE COMMENT 'unique identifier of the user',
    email VARCHAR(254) NOT NULL UNIQUE COMMENT 'email for user',
    password VARCHAR(120) NOT NULL COMMENT 'password',
    name VARCHAR(120) NULL COMMENT 'name of user',
    PRIMARY KEY(id)
)   ENGINE=INNODB;

CREATE TABLE tb_project (
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT UNIQUE COMMENT 'unique identifier of the project',
    user_id BIGINT UNSIGNED NOT NULL COMMENT 'unique identifier of the user',
    name VARCHAR(120) NULL COMMENT 'name of project',
    PRIMARY KEY(id),
    INDEX user_id_idx (user_id),
    FOREIGN KEY (user_id)
            REFERENCES tb_user(id)
            ON DELETE CASCADE
)   ENGINE=INNODB;
