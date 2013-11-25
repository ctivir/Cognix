package com.cognitivabrasil.repositorio.services;

import com.cognitivabrasil.repositorio.data.entities.Files;
import com.cognitivabrasil.repositorio.data.repositories.FileRepository;
import java.io.IOException;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Marcos Freitas Nunes <marcosn@gmail.com>
 */
@Service
public class FilesServiceImpl implements FilesService {

    private final Logger log = Logger.getLogger(FilesServiceImpl.class);
    @Autowired
    private FileRepository fileRep;

    /* (non-Javadoc)
     * @see cognitivabrasil.repositorio.models.AbstractServiceImpl#delete(java.lang.Object)
     */
    @Override
    public void deleteFile(Files file) throws IOException {
        try {
            file.deleteFile();
        } catch (IOException e) {
            log.error("Ao tentar deletar, não foi possível encontrar o arquivo: " + file.getLocation(), e);
            throw e;
        } finally {
            fileRep.delete(file);
        }
    }

    @Override
    public void save(Files f) {
        fileRep.save(f);
    }

    @Override
    public Files get(int id) {
        return fileRep.findOne(id);
    }
    
    @Override
    public List<Files> getAll(){
        return fileRep.findAll();
    }

}
