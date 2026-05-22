/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  Dell
 * Created: May 21, 2026
 */


CREATE TABLE auth_credentials(
    ID BIGINT NOT NULL AUTO_INCREMENT,
    TENANT_ID VARCHAR(36) NOT NULL UNIQUE,
    COMPANY_NAME VARCHAR(100) NOT NULL,
    EMAIL VARCHAR(36) NOT NULL UNIQUE,
    PASSWORD VARCHAR(255) NOT NULL,
    CREATED_AT DATETIME NOT NULL,
    CONSTRAINT pk_auth_credentials PRIMARY KEY(ID)
);