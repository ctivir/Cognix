/*
 * /*******************************************************************************
 *  * Copyright (c) 2016 Cognitiva Brasil - Tecnologias educacionais.
 *  * All rights reserved. This program and the accompanying materials
 *  * are made available either under the terms of the GNU Public License v3
 *  * which accompanies this distribution, and is available at
 *  * http://www.gnu.org/licenses/gpl.html or for any other uses contact 
 *  * contato@cognitivabrasil.com.br for information.
 *  ******************************************************************************/

package metadata.conversor;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Paulo Schreiner 
 */
public class Conversor {
	private List<Rule> rules;

	Conversor() {
		rules = new ArrayList<>();
	}
	
	public void add(Rule r) {
		rules.add(r);
	}

	public void convert(Object ob1, Object ob2) {
		for(Rule r : rules) {
			r.apply(ob1, ob2);
		}	
	}
	
}
