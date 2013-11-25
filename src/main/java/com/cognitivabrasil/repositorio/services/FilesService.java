package com.cognitivabrasil.repositorio.services;

import com.cognitivabrasil.repositorio.data.entities.Files;
import java.io.IOException;
import java.util.List;

public interface FilesService {
	public Files get(int id);
        public void deleteFile(Files f) throws IOException;
        public void save(Files f);
        public List<Files> getAll();
}
