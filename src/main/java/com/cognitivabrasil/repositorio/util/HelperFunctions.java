/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitivabrasil.repositorio.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import metadata.TextElement;

/**
 * Classe com m&eacute;todos que efetuam opera&ccedil;otilde;es diversas. Como
 * testes e remo&ccedil;&otilde;es de acentua&ccedil;&atilde;o
 * 
 * @author Marcos
 */
public class HelperFunctions {
	/**
	 * Substitui letras acentudas por letras sem acentos (remove acentos das
	 * letras), e remove todo tipo de caracter que n&atilde;o seja letra e
	 * n&uacute;mero.
	 * 
	 * @param texto
	 *            texto que deseja remover os acentos.
	 * @return texto sem acentos e apenas com letras e n&uacute;meros.
	 */
	public static String removeAcentuacao(String texto) {
		texto = texto.toLowerCase();
		texto = texto.replaceAll("á|à|â|ã|ä", "a");
		texto = texto.replaceAll("é|è|ê|ë", "e");
		texto = texto.replaceAll("í|ì|î|ï", "i");
		texto = texto.replaceAll("ó|ò|ô|õ|ö", "o");
		texto = texto.replaceAll("ú|ù|û|ü", "u");
		texto = texto.replaceAll("ç", "c");
		texto = texto.replaceAll("ñ", "n");
		texto = texto.replaceAll("\\W", " ");
		texto = texto.trim();
		return texto;
	}

	/**
	 * Recebe um ArrayList<String> percorre esse array e armazena em uma string
	 * todas as posi&ccedil;%otilde;es do array separados por um espa&ccedil;o.
	 * 
	 * @param array
	 *            ArrayList<String> contendo as string que ser&atildeo
	 *            concatenadas.
	 * @return um String contendo todo o conte&uacute;do do array separados por
	 *         um espa&ccedil;o.
	 */
	public static String arrayListToString(ArrayList<String> array) {
		String resultado = "";
		for (int i = 0; i < array.size(); i++) {
			resultado += " " + array.get(i);
		}

		return resultado.trim();
	}

	/**
	 * Testa se a data informada &eacute; anterior a data atual.
	 * 
	 * @param data
	 *            Vari&aacute;vel do tipo Date que ser&aacute; testada.
	 * @return true se a data for anterior e falso caso contr&aacute;rio.
	 */
	public static boolean dataAnteriorAtual(Date data) {
		if (data == null || data.before(new Date())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * formata a data recebida para o padr&atilde;o do OAI-PMH. Ex:
	 * 2012-04-20T19:09:32Z
	 * 
	 * @param date
	 *            Objeto Date para ser formatado
	 * @return String contendo a data formatada.
	 */
	public static String formatDateOAIPMH(Date date) {
		if (date == null) {
			return null;
		} else {
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss'Z'");
			return format.format(date);
		}
	}

	public static List<String> toStringList(List<? extends TextElement> elements) {
		List<String> s = new ArrayList<String>();
		if(elements == null) { return s; }
		for (TextElement e : elements) {
			s.add(e.getTranslated());
		}
		return s;
	}

	public static List<? extends TextElement> fromStringList(
			List<String> titles, Class<? extends TextElement> c) {
		List l = new ArrayList();

		for (String text : titles) {
			TextElement element;
			try {
				element = c.newInstance();
				element.setText(text);
				l.add(element);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block1
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return l;

	}

}
