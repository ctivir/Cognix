package com.cognitivabrasil.repositorio.web;

import java.io.IOException;

public class FileUploadException extends IOException {

	public FileUploadException(String string, IOException e) {
		super(string);
		this.initCause(e);
	}

}
