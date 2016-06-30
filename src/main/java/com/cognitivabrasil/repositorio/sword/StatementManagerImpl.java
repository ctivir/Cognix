/*
 * Copyright (c) 2016 Cognitiva Brasil Tecnologias Educacionais
 * http://www.cognitivabrasil.com.br
 *
 * All rights reserved. This program and the accompanying materials
 * are made available either under the terms of the GNU Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html or for any other uses contact
 * contato@cognitivabrasil.com.br for information.
 */
package com.cognitivabrasil.repositorio.sword;

import java.util.Map;
import org.apache.log4j.Logger;
import org.swordapp.server.AuthCredentials;
import org.swordapp.server.Statement;
import org.swordapp.server.StatementManager;
import org.swordapp.server.SwordAuthException;
import org.swordapp.server.SwordConfiguration;
import org.swordapp.server.SwordError;
import org.swordapp.server.SwordServerException;

/**
 *
 * @author
 */
public class StatementManagerImpl implements StatementManager {

    private static final Logger log = Logger.getLogger(StatementManagerImpl.class);

    public Statement getStatement(String iri, Map<String, String> accept, AuthCredentials auth, SwordConfiguration config)
    throws SwordServerException, SwordError, SwordAuthException
    {
	return null;
    }

}
