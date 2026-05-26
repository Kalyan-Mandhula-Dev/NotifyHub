
CREATE TABLE templates
(
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    tenant_id       VARCHAR(36)   NOT NULL,
    event_type      VARCHAR(100)  NOT NULL,
    template_name   VARCHAR(100)  NOT NULL,
    s3_key          VARCHAR(255)  NOT NULL,
    is_active       BOOLEAN       DEFAULT TRUE,
    created_at      DATETIME
);