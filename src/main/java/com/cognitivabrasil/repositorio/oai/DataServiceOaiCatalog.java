/*
 * /*******************************************************************************
 *  * Copyright (c) 2016 Cognitiva Brasil - Tecnologias educacionais.
 *  * All rights reserved. This program and the accompanying materials
 *  * are made available either under the terms of the GNU Public License v3
 *  * which accompanies this distribution, and is available at
 *  * http://www.gnu.org/licenses/gpl.html or for any other uses contact 
 *  * contato@cognitivabrasil.com.br for information.
 *  ******************************************************************************/

package com.cognitivabrasil.repositorio.oai;

import ORG.oclc.oai.server.catalog.AbstractServiceOaiCatalog;
import ORG.oclc.oai.server.catalog.OaiDocumentService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.springframework.context.ApplicationContext;
import spring.ApplicationContextProvider;

/**
 * DummyOAICatalog is an example of how to implement the AbstractCatalog
 * interface. Pattern an implementation of the AbstractCatalog interface after
 * this class to have OAICat work with your database. Your effort may be
 * minimized by confining your changes to areas identified by "YOUR CODE GOES
 * HERE" comments. In truth, though, you can do things however you want, as long
 * as the non-private methods return what they're supposed to.
 *
 * @author Jeffrey A. Young, OCLC Online Computer Library Center
 */
public class DataServiceOaiCatalog extends AbstractServiceOaiCatalog {

    public DataServiceOaiCatalog(Properties properties) {
        super(properties);
    }

    /**
     * Retrieve a list of sets that satisfy the specified criteria
     *
     * @return a Map object containing "sets" Iterator object (contains
     * <setSpec/> XML Strings) as well as an optional resumptionMap Map.
     * @exception OAIBadRequestException signals an http status code 400 problem
     */
    @Override
    public Map listSets() {

        // clean out old resumptionTokens
        purge();
        Map listSetsMap = new HashMap();
        
        List sets = new ArrayList();

        // TODO: Implement getSets()
        StringBuilder s = new StringBuilder(200);
        s.append("<set>\n");
        s.append("<setSpec>");
        s.append("notImplemented");
        s.append("</setSpec>\n");
        s.append("<setName>");
        s.append("notImplemented");
        s.append("</setName>\n");
        s.append("</set>\n");
        sets.add(s.toString());

        listSetsMap.put("sets", sets.iterator());
        return listSetsMap;
    }


	@Override
	public OaiDocumentService getDocumentService() {
		  ApplicationContext ctx = ApplicationContextProvider
                  .getApplicationContext();
          return ctx.getBean(OaiDocumentService.class);
	}


}
