alter session set "_ORACLE_SCRIPT"=true;
CREATE USER catalogue IDENTIFIED BY "micronaut";
GRANT CONNECT, RESOURCE TO catalogue;
GRANT UNLIMITED TABLESPACE TO catalogue;
