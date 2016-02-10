/*
 * /*******************************************************************************
 *  * Copyright (c) 2016 Cognitiva Brasil - Tecnologias educacionais.
 *  * All rights reserved. This program and the accompanying materials
 *  * are made available either under the terms of the GNU Public License v3
 *  * which accompanies this distribution, and is available at
 *  * http://www.gnu.org/licenses/gpl.html or for any other uses contact 
 *  * contato@cognitivabrasil.com.br for information.
 *  ******************************************************************************/
 
package com.cognitivabrasil.repositorio.services;

import com.cognitivabrasil.repositorio.data.entities.Files;
import java.io.IOException;
import java.util.List;

public interface FileService {
	public Files get(int id);
        public void deleteFile(Files f) throws IOException;
        public void save(Files f);
        public List<Files> getAll();
}
