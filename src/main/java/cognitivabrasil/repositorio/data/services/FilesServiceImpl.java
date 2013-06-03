package cognitivabrasil.repositorio.data.services;

import cognitivabrasil.repositorio.data.entities.Document;
import cognitivabrasil.repositorio.data.entities.Files;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * @author paulo
 *
 */
@Service("filesService")
public class FilesServiceImpl extends AbstractServiceImpl<Files> implements FilesService {

    Logger log = Logger.getLogger(FilesServiceImpl.class);

    /* (non-Javadoc)
     * @see cognitivabrasil.repositorio.models.AbstractServiceImpl#delete(java.lang.Object)
     */
    @Override
    public void deleteFile(Files file) throws IOException {
        try {
           file.deleteFile();
        } catch (IOException e) {
            log.error("Ao tentar deletar, não foi possível encontrar o arquivo: " + file.getLocation(),e);
            throw e;
        } finally {
            Document d = file.getDocument();
            d.removeFile(file);
            this.sessionFactory.getCurrentSession().delete(file);
            this.sessionFactory.getCurrentSession().flush();
        }
    }
   
}
