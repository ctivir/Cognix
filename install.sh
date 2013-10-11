#!/bin/sh
echo "Executando o script de inicialização da base de dados..."
sudo -u postgres psql -c "CREATE USER cognitiva WITH PASSWORD 'rep@cognitiva'"
sudo -u postgres dropdb repositorio
sudo -u postgres createdb -O cognitiva -E 'UTF-8' repositorio

sudo -u postgres psql repositorio -f ./dataBase/schema.sql

echo "Criando a pasta para o log"
sudo mkdir /var/log/cognitiva
sudo chown tomcat7:tomcat7 /var/log/cognitiva/

echo "Criando a pasta onde serão salvos os objetos educacionais"
sudo mkdir -p /var/cognitiva/repositorio/
sudo chown -R tomcat7:tomcat7 /var/cognitiva/
