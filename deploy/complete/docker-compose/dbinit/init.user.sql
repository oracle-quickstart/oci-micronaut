alter session set "_ORACLE_SCRIPT"=true;
CREATE USER muser IDENTIFIED BY "micronaut";
GRANT CONNECT, RESOURCE TO muser;
GRANT UNLIMITED TABLESPACE TO muser;