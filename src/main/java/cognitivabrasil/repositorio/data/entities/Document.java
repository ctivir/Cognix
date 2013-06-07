package cognitivabrasil.repositorio.data.entities;

import ORG.oclc.oai.models.HibernateOaiDocument;
import cognitivabrasil.obaa.OBAA;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 *
 *
 * @author Paulo Schreiner <paulo@jorjao81.com>
 * @author Marcos Nunes <marcosn@gmail.com>
 *
 */
public class Document implements java.io.Serializable, HibernateOaiDocument {

    Logger log = Logger.getLogger(Document.class);
    private Integer id;
    private String obaaEntry;
    private Date timestamp;
    private Boolean deleted;
    private String obaaXml;
    private Set<Files> files;
    private OBAA metadata;
    private User owner;

    public Document() {
        obaaEntry = "";
        deleted = false;
        files = new HashSet<Files>();
    }

    public Integer getId() {
        return this.id;
    }

    protected void setId(Integer id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Date date) {
        this.timestamp = date;
    }

    /**
     * Format the timestamp in this format: dd/MM/yyyy HH:mm:ss
     *
     * @return String whith the formatted timestamp
     */
    public String getTimestampFormatted() {
        if (this.timestamp == null) {
            return "";
        } else {
            SimpleDateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            return dataFormat.format(this.timestamp);
        }
    }

    public String getObaaEntry() {
        return this.obaaEntry;
    }

    public void setObaaEntry(String entry) {
        this.obaaEntry = entry;
    }

    /**
     * @return the excluido
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * @param excluido the excluido to set
     */
    public void setDeleted(Boolean excluido) {
        this.deleted = excluido;
    }

    public void isDeleted(boolean excluido) {
        this.deleted = excluido;
    }

    /**
     * Gets the OBAA XML directly, consider using getMetadata() instead.
     *
     * @return the obaaXml
     */
    public String getObaaXml() {
        return obaaXml;
    }

    /**
     * Sets the OBAA XML directly, consider using setMetadata instead.
     *
     * @param obaaXml the obaaXml to set
     */
    public void setObaaXml(String obaaXml) {
        this.obaaXml = obaaXml;
    }

    /**
     * Returns the document metadata.
     *
     * @return the metadata
     * @throws IllegalStateException if there is no XML metadata associated with
     * the document
     */
    public OBAA getMetadata() {
        if (metadata == null) {
            if (getObaaXml() == null) {
                metadata = new OBAA();
//				throw new IllegalStateException("No XML metadata associated with the Object");
            } else {
                metadata = OBAA.fromString(getObaaXml());
            }
        }
        return metadata;
    }

    /**
     * Sets the metadata of the object, and updates the corresponding XML.
     *
     * @param metadata the metadata to set
     */
    public void setMetadata(OBAA metadata) {
        this.metadata = metadata;
//		updateIndexes();
        try {
            setObaaXml(metadata.toXml());
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
        }
    }

    public Set<Files> getFiles() {
        return files;
    }

    public void setFiles(Set<Files> files) {
        this.files = files;
    }

    public void addFile(Files f) {
        if (files == null) {
            files = new HashSet<Files>();
        }
        files.add(f);
    }

    public String getTitle() {
        if (getObaaXml() == null || getObaaXml().isEmpty()) {
            return "Sem título";
        }
        try {
            return getMetadata().getGeneral().getTitles().get(0);
        } catch (NullPointerException e) {
            log.error(e);
            return "NullPointer";
        } catch (IndexOutOfBoundsException e) {
            log.error(e);
            return "IndexOutOfBounds";
        }
    }

    /**
     * Removes a file from the collection. (does NOT delete it from database)
     *
     * @param item
     */
    public void removeFile(Files item) {
        this.files.remove(item);
    }

    @Override
    public String getXml() {
        return getObaaXml();
    }

    @Override
    public String getOaiIdentifier() {
        return getObaaEntry();
    }

    @Override
    public Collection<String> getSets() {
        // TODO: currently we return an empty set, should implement this functionality
        Collection<String> c = new HashSet<String>();
        return c;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

}