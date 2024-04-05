CREATE TABLE member (
                        member_id VARCHAR(255) NOT NULL,
                        name VARCHAR(255) NOT NULL,
                        password VARCHAR(255) NOT NULL,
                        email VARCHAR(255) NOT NULL,
                        PRIMARY KEY (member_id)
);

CREATE TABLE parenting (
                           parenting_id BIGINT NOT NULL,
                           parent_id VARCHAR(255) NOT NULL,
                           child_id VARCHAR(255) NOT NULL,
                           PRIMARY KEY (parenting_id),
                           FOREIGN KEY (parent_id) REFERENCES Member (member_id),
                           FOREIGN KEY (child_id) REFERENCES Child (child_id)
);

CREATE TABLE helping (
                         helping_id BIGINT NOT NULL,
                         helper_id VARCHAR(255) NOT NULL,
                         child_id VARCHAR(255) NOT NULL,
                         PRIMARY KEY (helping_id),
                         FOREIGN KEY (helper_id) REFERENCES Member (member_id),
                         FOREIGN KEY (child_id) REFERENCES Child (child_id)
);

CREATE TABLE child (
                       child_id BIGINT NOT NULL,
                       child_name VARCHAR(255) NOT NULL,
                       child_password VARCHAR(255) NOT NULL,
                       PRIMARY KEY (child_id)
);

CREATE TABLE coordinate (
                            coordinate_id BIGINT NOT NULL,
                            child_id VARCHAR(255) NOT NULL,
                            is_living_area TINYINT(1) NOT NULL,
                            x DOUBLE NOT NULL,
                            y DOUBLE NOT NULL,
                            PRIMARY KEY (coordinate_id),
                            FOREIGN KEY (child_id) REFERENCES Child (child_id)
);

/*
CREATE TABLE living_area (
    living_area_id BIGINT NOT NULL,
    child_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (living_area_id),
    FOREIGN KEY (child_id) REFERENCES Child (child_id)
);

CREATE TABLE forbidden_area (
    forbidden_area_id BIGINT NOT NULL,
    child_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (forbidden_area_id),
    FOREIGN KEY (child_id) REFERENCES Child (child_id)
);
*/

CREATE TABLE notice (
                        notice_id BIGINT NOT NULL AUTO_INCREMENT,
                        title VARCHAR(255) NOT NULL,
                        content TEXT NOT NULL,
                        level ENUM('INFO', 'WARN', 'FATAL', 'CALL') NOT NULL,
                        child_id VARCHAR(255) NOT NULL,
                        created_at DATETIME NOT NULL,
                        PRIMARY KEY (notice_id),
                        FOREIGN KEY (child_id) REFERENCES Child (child_id)
);

CREATE TABLE confirm (
                         confirm_id BIGINT NOT NULL AUTO_INCREMENT,
                         title VARCHAR(255) NOT NULL,
                         content TEXT NOT NULL,
                         type ENUM('ARRIVED', 'DEPART', 'UNCONFIRMED') NOT NULL,
                         child_id VARCHAR(255) NOT NULL,
                         created_at DATETIME NOT NULL,
                         PRIMARY KEY (confirm_id),
                         FOREIGN KEY (child_id) REFERENCES Child (child_id)
);

CREATE TABLE comment (
                         comment_id BIGINT NOT NULL AUTO_INCREMENT,
                         notice_id BIGINT NOT NULL,
                         commentator_id VARCHAR(255) NOT NULL,
                         comment TEXT NOT NULL,
                         commented_at DATETIME NOT NULL,
                         PRIMARY KEY (comment_id),
                         FOREIGN KEY (notice_id) REFERENCES Notice (notice_id)
);
