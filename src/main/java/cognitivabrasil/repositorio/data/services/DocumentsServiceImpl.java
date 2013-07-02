/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cognitivabrasil.repositorio.data.services;

import cognitivabrasil.repositorio.data.entities.Document;
import cognitivabrasil.repositorio.data.entities.Files;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The Class DocumentsServiceImpl.
 *
 * @author Paulo Schreiner <paulo@jorjao81.com>
 */
@Service("documentsService")
public class DocumentsServiceImpl implements DocumentsService {

    @Autowired
    FilesService filesService;
    @Autowired
    SessionFactory sessionFactory;
    Logger log = Logger.getLogger(DocumentsServiceImpl.class.getName());

    /**
     * Gets a List of Documents by obaa entry.
     *
     * @param e the ObaaEntry
     * @return List of documentos with this obaaEntry
     */
    @SuppressWarnings("unchecked")
    private List<Document> getByObaaEntry(String e) {
        return getSession().createCriteria(Document.class).add(Restrictions.eq("obaaEntry", e)).list();

    }

    /*
     * (non-Javadoc)
     *
     * @see modelos.DocumentosDAO#get(java.lang.String)
     */
    @Override
    public Document get(String e) {
        return (Document) getSession().createCriteria(Document.class).
                add(Restrictions.eq("obaaEntry", e)).uniqueResult();

    }

    @Override
    public Document get(int i) {
        return (Document) getSession().get(Document.class, i);

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Document> getAll() {
        return getSession().createCriteria(Document.class).add(Restrictions.eq("deleted", false)).
                addOrder(Order.desc("timestamp")).list();
    }

    @Override
    public void deleteByObaaEntry(String e) {
        for (Document d : getByObaaEntry(e)) {
            log.trace("DeleteByObaaEntry: " + e);
            delete(d);
        }
    }

    @Override
    public void delete(Document d) {
        for (Files f : d.getFiles()) {
            try {
                f.deleteFile();
            } catch (IOException e) {
                log.error("Could not delete file", e);
            }
        }
        d.setObaaXml(null);
        d.setTimestamp(new Date());
        d.isDeleted(true);
        save(d);
        flush();
    }

    @Override
    public void deleteAll() {
        for (Document d : (List<Document>) getSession().createCriteria(Document.class).list()) {
            for (Files f : d.getFiles()) {
                try {
                    f.deleteFile();
                } catch (IOException e) {
                    log.error("Could not delete file", e);
                }
            }
            getSession().delete(d);
        }
        flush();
    }

    @Override
    public void deleteFromDatabase(Document d) {
        getSession().delete(d);
        flush();
    }


    /*
     * (non-Javadoc)
     *
     * @see modelos.DocumentosDAO#save(OBAA.OBAA, metadata.Header)
     */
    @Override
    public void save(Document d) throws IllegalStateException {
        getSession().saveOrUpdate(d);

    }

    /**
     * Gets the session.
     *
     * @return the session
     */
    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void flush() {
        getSession().flush();
    }
}
