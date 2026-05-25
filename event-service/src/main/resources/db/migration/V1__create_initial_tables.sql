
CREATE TABLE events(
    id              BIGINT        NOT NULL AUTO_INCREMENT,
    tenant_id       VARCHAR(36)   NOT NULL,
    event_type      VARCHAR(100)  NOT NULL,
    channel         ENUM('EMAIL','WEBHOOK')  NOT NULL,
    recipient       VARCHAR(255)  NOT NULL,
    payload         JSON          NOT NULL,
    status          ENUM('RECEIVED', 'PUBLISHED', 'FAILED')  NOT NULL,
    created_at      DATETIME      NOT NULL,
    CONSTRAINT pk_events PRIMARY KEY (id)
);


CREATE TABLE event_outbox(
    id BIGINT NOT NULL AUTO_INCREMENT,
    event_id BIGINT NOT NULL,
    kafka_topic VARCHAR(100) NOT NULL,
    published boolean default false,
    created_at DATETIME NOT NULL,
    CONSTRAINT pk_event_outbox PRIMARY KEY (id)
)