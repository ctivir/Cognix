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

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Feed;
import org.swordapp.server.AuthCredentials;
import org.swordapp.server.CollectionListManager;
import org.swordapp.server.SwordAuthException;
import org.swordapp.server.SwordConfiguration;
import org.swordapp.server.SwordError;
import org.swordapp.server.SwordServerException;

/**
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
public class CollectionListManagerImpl implements CollectionListManager {

    /**
     * O repositório só tem uma coleção, então este método retorna null.
     *
     * @param iri
     * @param ac
     * @param sc
     * @return
     * @throws SwordServerException
     * @throws SwordAuthException
     * @throws SwordError
     */
    @Override
    public Feed listCollectionContents(IRI iri, AuthCredentials ac, SwordConfiguration sc) throws SwordServerException, SwordAuthException, SwordError {
        Abdera abdera = new Abdera();
        Feed feed = abdera.newFeed();
        return feed;
//        return null;
    }
}
