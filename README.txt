INSTALAÇÃO DO REPOSITÓRIO
------------------------------------------------------------

PASSO 1 - Dependências

Primeiramente, é necessário instalar as dependências:

 * Java 7.
 * Tomcat 7 (ou superior) ou outro container
 * PostgreSQL versão >= 8.4
 

É importante que estes componentes estejam funcionando corretamente antes de prosseguir com a instalação.

------------------------------------------------------------

PASSO 2

1 - Baixe o arquivo tar.gz com os arquivos de instalação da vesão corrente e descompacte-o. Abra um terminal e entre na pasta em que os arquivos foram descompactados.

2 - Criação da base de dados para 1a instalação
    Certifique-se que não existe a base de dados "repositorio". Se já existir, remova.
    sudo dropdb repositorio;

    Após execute os seguintes comandos:
        sudo -u postgres psql -c "CREATE USER cognitiva WITH PASSWORD 'rep@cognitiva'"
        sudo -u postgres createdb -O cognitiva repositorio
        sudo -u postgres psql repositorio -f schema.sql

4 - Crie o diretório para armazenamento dos logs
	sudo mkdir /var/log/cognitiva/
     Altere altere as permissões da pasta para o usuário que roda servidor possa utilizar. Neste caso do tomcat7:
	sudo chown tomcat7:tomcat7 /var/log/cognitiva/

5 - Crie a pasta onde serão salvos os objetos educacionais
        sudo mkdir -p /var/cognitiva/repositorio/
    Altere as permissões para o tomcat7 (se for este o servidor)
        sudo chown tomcat7:tomcat7 /var/cognitiva/


---------------------------------------------------------------

PASSO 3 - Instalação dos arquivos

repositorio.war -> fazer o deploy do arquivo no servidor.
    Copiar o repositorio.war para a pasta webapps do tomcat
    Reiniciar o serviço: sudo service tomcat7 restart

---------------------------------------------------------------

PASSO 4 - Configuração do endereço do servidor

Esse passo deve ser executado para que o repositório possa gerar os link dos objetos com o endereço correto. Ex: http://endereco.com.br/repositorio/3

Encontre o diretório onde o repositorio está instalado. Vamos chamá-lo
de ${REP} e edite o arquivo "config.properties". Neste caso:

    vim ${REP}/WEB-INF/classes/config.properties

Preencha as informações corretamente, e reinicie o servidor de Servlet onde o repositorio está rodando. 

    No caso do tomcat7: sudo service tomcat7 restart

---------------------------------------------------------------

PASSO 5 - Testar

Acesse a porta 8080/repositorio do servidor onde foi instalado.

Acesse o log em /var/log/cognitiva/repositorio.log e veja se está tudo correto (sem erros).
