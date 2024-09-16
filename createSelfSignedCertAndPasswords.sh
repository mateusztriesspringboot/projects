#!/bin/bash
#
# Simple script to quickly create passwords and self signed certificate to be used in docker compose env for
# demonstration purpose. All *_password files and keystore are referenced in the compose.yaml configuration.
# certificate.pem file can be used in clients such as Postman to test projects app https endpoints.
# usage: ./createSelfSignedCertAndPasswords.sh
#

keytoolPassword=$(openssl rand -base64 32)
mysqlPassword=$(openssl rand -base64 32)
mysqlProjectsPassword=$(openssl rand -base64 32)

keytool -genkeypair -v \
        -alias tomcat \
        -keyalg RSA \
        -keysize 2048 \
        -keystore keystore.jks \
        -storepass ${keytoolPassword} \
        -keypass ${keytoolPassword} \
        -dname "CN=localhost, OU=NA, O=NA, L=NA, ST=NA, C=NA" \
        -ext SAN=DNS:localhost,IP:127.0.0.1

echo "$mysqlPassword" > projects_root_password.txt
echo "$mysqlProjectsPassword" > projects_user_password.txt
echo "$keytoolPassword" > keytool_password.txt

echo "$keytoolPassword" | keytool -exportcert -alias tomcat -keystore keystore.jks -rfc -file certificate.pem