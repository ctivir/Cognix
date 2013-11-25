#!/bin/sh
echo "Executando o script de inicialização da base de dados..."
echo "#Creating user..."
sudo -u postgres psql -c "CREATE USER cognitiva WITH PASSWORD 'rep@cognitiva'"
echo "Tentando deletar a base de dados antiga..."
sudo -u postgres dropdb repositorio
echo "#Criando base local..."
sudo -u postgres createdb -O cognitiva -E 'UTF-8' repositorio

echo "#Criando as tabelas..."
sudo -u postgres psql repositorio < ./src/main/resources/schema.sql
echo "#Inserindo esquema do postgres..."
sudo -u postgres psql repositorio < ./src/main/resources/postgres_schema.sql

echo "Criando a pasta para o log"
sudo mkdir /var/log/cognitiva
sudo chown tomcat7:tomcat7 /var/log/cognitiva/

echo "Criando a pasta onde serão salvos os objetos educacionais"
sudo mkdir -p /var/cognitiva/repositorio/
sudo chown -R tomcat7:tomcat7 /var/cognitiva/
