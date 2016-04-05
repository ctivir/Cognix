![Cognix logo](http://i.imgur.com/zgvtNms.png?1)

Desenvolvido por [Cognitiva Brasil](http://cognitivabrasil.com.br/)

#Repositório Cognix

Repositório de Objetos de Aprendizagem

Tem como objetivo principal facilitar a catalogação de materiais educacionais no padrão de metadados OBAA.
Possui um sistema de catalogação semiautomática, com inferência de diversos metadados.

Imagem docker pronta para ser usada disponível neste [link] (http://hub.docker.com/r/rafaelescrich/cognix)


INSTALAÇÃO DO REPOSITÓRIO
===================================================================================

PASSO 1 - Dependências
-----------------------------------------------------------------------------------

 * Java 7.
 * Tomcat 7 (ou superior) ou outro container
 * PostgreSQL versão >= 8.4
 * Ubuntu 14.04 ou superior (não é uma dependência porém neste tutorial utilizarei o 14.04)

Instalação Java 7
------------------------------------------------------------------------------------
```bash
sudo apt-get install openjdk-7-jre
```

Instalação Tomcat 7
------------------------------------------------------------------------------------
```bash
sudo apt-get install tomcat7
```

Instalação PostgreSQL
------------------------------------------------------------------------------------
```bash
sudo apt-get install postgresql postgresql-contrib
```

PASSO 2
------------------------------------------------------------------------------------

1 - Baixe o código fonte do programa, ou extraindo do controle de versões ou extraindo 
de um .tar.gz. Abra um terminal e entre na pasta em que os arquivos (descompactados) 
se encontram.

2 - Rode o instalador:
```bash
sudo sh install.sh
```


PASSO 3 - Instalação dos arquivos
-------------------------------------------------------------------------------------

repositorio.war -> fazer o deploy do arquivo no servidor.
    Copiar o repositorio.war para a pasta webapps do tomcat
    Reiniciar o serviço: 
```bash
sudo service tomcat7 restart
```


PASSO 4 - Configuração do endereço do servidor
-------------------------------------------------------------------------------------

Esse passo deve ser executado para que o repositório possa gerar os link dos objetos com o endereço correto. Ex: http://endereco.com.br/repositorio/3

Encontre o diretório onde o repositorio está instalado. Vamos chamá-lo
de ${REP} e edite o arquivo "config.properties". Neste caso:

>    vim ${REP}/WEB-INF/classes/config.properties

Preencha as informações corretamente, e reinicie o servidor de Servlet onde o repositorio está rodando. 

No caso do tomcat7:
```bash
sudo service tomcat7 restart
```


PASSO 5 - Testar
------------------------------------------------------------------------------------------

Acesse a porta 8080/repositorio do servidor onde foi instalado.

Acesse o log em /var/log/cognitiva/repositorio.log e veja se está tudo correto (sem erros).

LICENÇA 
------------------------------------------------------------------------------------------
/*******************************************************************************
 * Copyright (c) 2016 Cognitiva Brasil - Tecnologias educacionais.
 * All rights reserved. This program and the accompanying materials
 * are made available either under the terms of the GNU Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html or for any other uses contact 
 * contato@cognitivabrasil.com.br for information.
 ******************************************************************************/
