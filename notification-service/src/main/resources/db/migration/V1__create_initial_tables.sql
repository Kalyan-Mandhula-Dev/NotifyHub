
CREATE TABLE delivery_logs
(
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    event_id        BIGINT        NOT NULL,
    tenant_id       VARCHAR(36)   NOT NULL,
    event_type      VARCHAR(100)  NOT NULL,
    channel         ENUM('EMAIL','WEBHOOK') NOT NULL,
    recipient       VARCHAR(255)  NOT NULL,
    status          ENUM('PENDING', 'SENT', 'FAILED') NOT NULL,
    error_message   VARCHAR(500),
    sent_at         DATETIME,
    created_at      DATETIME NOT NULL
);