#!/usr/bin/env bash
# sourced from AWS documentation:
# https://docs.aws.amazon.com/documentdb/latest/developerguide/connect_programmatically.html#connect_programmatically-tls_enabled
#
# Script extends the cacerts by AWS RDS certificates



# password from argument
storepassword=changeit

curl -sS "https://s3.amazonaws.com/rds-downloads/rds-combined-ca-bundle.pem" > /tmp/rds-combined-ca-bundle.pem
awk 'split_after == 1 {n++;split_after=0} /-----END CERTIFICATE-----/ {split_after=1}{print > "rds-ca-" n ".pem"}' < /tmp/rds-combined-ca-bundle.pem

for CERT in rds-ca-*; do
  alias=$(openssl x509 -noout -text -in $CERT | perl -ne 'next unless /Subject:/; s/.*(CN=|CN = )//; print')
  echo "Importing $alias"
  keytool -import -file ${CERT} -trustcacerts -alias "${alias}" -storepass ${storepassword} -cacerts -noprompt
  rm $CERT
done
rm /tmp/rds-combined-ca-bundle.pem


