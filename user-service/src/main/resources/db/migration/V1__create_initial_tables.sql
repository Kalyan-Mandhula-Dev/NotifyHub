/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  Dell
 * Created: May 17, 2026
 */

CREATE TABLE tenants (
    id          VARCHAR(36)     NOT NULL,
    company_name VARCHAR(100)   NOT NULL,
    email       VARCHAR(100)    NOT NULL,
    is_active   BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at  DATETIME        NOT NULL,
    CONSTRAINT pk_tenants PRIMARY KEY (id),
    CONSTRAINT uq_tenants_email UNIQUE (email)
);

CREATE TABLE subscriptions (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    tenant_id   VARCHAR(36)     NOT NULL,
    channel     ENUM('EMAIL','WEBHOOK') NOT NULL,
    destination VARCHAR(255)    NOT NULL,
    is_active   BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at  DATETIME        NOT NULL,
    CONSTRAINT pk_subscriptions PRIMARY KEY (id),
    CONSTRAINT fk_subscriptions_tenant FOREIGN KEY (tenant_id)
        REFERENCES tenants(id)
);