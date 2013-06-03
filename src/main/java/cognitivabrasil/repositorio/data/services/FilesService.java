package cognitivabrasil.repositorio.data.services;

import cognitivabrasil.repositorio.data.entities.Files;
import java.io.IOException;

public interface FilesService {
	public Files get(int id);
        public void deleteFile(Files f) throws IOException;
        public void save(Files f);
        public void delete(Files item);
}
