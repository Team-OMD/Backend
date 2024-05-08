#mysql
INSERT INTO authority (authority_name) VALUES ('ROLE_USER') ON DUPLICATE KEY UPDATE authority_name='ROLE_USER';
INSERT INTO authority (authority_name) VALUES ('ROLE_ADMIN') ON DUPLICATE KEY UPDATE authority_name='ROLE_ADMIN';


# h2
# MERGE INTO authority (authority_name) KEY (authority_name) VALUES ('ROLE_USER');
# MERGE INTO authority (authority_name) KEY (authority_name) VALUES ('ROLE_ADMIN');


