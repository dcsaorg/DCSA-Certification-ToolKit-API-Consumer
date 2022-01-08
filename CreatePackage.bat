@echo off
call mvn -Dmaven.test.skip=true clean package
set version=2.2.0
set package=Consumer-v%version%
rmdir /S /Q %package%
mkdir %package%\ctk
mkdir %package%\documents
copy target\dcsa_ctk_consumer-*.jar %package%\ctk\
move %package%\ctk\dcsa_ctk_consumer-*.jar %package%\ctk\dcsa_ctk_consumer.jar
copy Dockerfile %package%\
copy docker-compose.yml %package%\docker-compose.yml
Xcopy DCSA-Information-Model\ %package%\DCSA-Information-Model\ /E /I