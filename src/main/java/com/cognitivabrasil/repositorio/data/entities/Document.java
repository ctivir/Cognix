/*
 * /*******************************************************************************
 *  * Copyright (c) 2016 Cognitiva Brasil - Tecnologias educacionais.
 *  * All rights reserved. This program and the accompanying materials
 *  * are made available either under the terms of the GNU Public License v3
 *  * which accompanies this distribution, and is available at
 *  * http://www.gnu.org/licenses/gpl.html or for any other uses contact 
 *  * contato@cognitivabrasil.com.br for information.
 *  ******************************************************************************
 *
 */
package com.cognitivabrasil.repositorio.data.entities;

import ORG.oclc.oai.models.OaiDocument;
import cognitivabrasil.obaa.OBAA;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author Paulo Schreiner <paulo@cognitivabrasil.com.br>
 * @author Marcos Nunes <marcosn@gmail.com>
 *
 */
@Entity
@Table(name = "documents")
public class Document implements OaiDocument, java.io.Serializable {

    private static final Logger LOG = Logger.getLogger(Document.class);
    private Integer id;
    private String obaaEntry;
    @DateTimeFormat(style = "M-")
    private DateTime created;
    private Boolean deleted;
    private Boolean active;
    private String obaaXml;
    private List<Files> files;
    private OBAA metadata;
    private User owner;
    private Subject subject;

    public Document() {
        obaaEntry = "";
        deleted = false;
        active = false;
        created = new DateTime();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return this.id;
    }

    protected void setId(Integer id) {
        this.id = id;
    }

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getCreated() {
        return this.created;
    }

    @Transient
    @Override
    public Date getTimestamp() {
        return this.created.toDate();
    }

    public void setCreated(DateTime date) {
        this.created = date;
    }

    /**
     * Format the created in this format: dd/MM/yyyy HH:mm:ss
     *
     * @return String whith the formatted created
     */
    @Transient
    public String getTimestampFormatted() {
        if (this.created == null) {
            return "";
        } else {
            return this.created.toString("dd/MM/yyyy HH:mm:ss");
        }
    }

    @Column(name = "obaa_entry")
    public String getObaaEntry() {
        return this.obaaEntry;
    }

    public void setObaaEntry(String entry) {
        this.obaaEntry = entry;
    }

    /**
     * @return the excluido
     */
    @Override
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * @param excluido the excluido to set
     */
    public void setDeleted(Boolean excluido) {
        this.deleted = excluido;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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
    @Transient
    public OBAA getMetadata() {
        if (metadata == null) {
            if (getObaaXml() == null) {
                metadata = new OBAA();
                LOG.warn("XML esta em branco");
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
        
        try {
            setObaaXml(metadata.toXml());
        } catch (RuntimeException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("name ASC")
    public List<Files> getFiles() {
        return files;
    }

    public void setFiles(List<Files> files) {
        this.files = files;
    }

    @Transient
    public String getTitle() {
        if (getMetadata().getGeneral() == null || getMetadata().getGeneral().getTitles().isEmpty()) {
            return "Sem título";
        }
        return getMetadata().getGeneral().getTitles().get(0);
    }

    /**
     * Return the OBAA xml. To satisfy OaiDocument.
     * @return OBAA xml
     */
    @Transient
    @Override
    public String getXml() {
        return getObaaXml();
    }

    @Transient
    @Override
    /**
     * To satisfy OaiDocument.
     * return ObaaEntry
     */
    public String getOaiIdentifier() {
        return getObaaEntry();
    }

    @Transient
    @Override
    /**
     * To satisfy OaiDocument. Sets not yet implemented.
     * return Empty set.
     */
    public Collection<String> getSets() {
        // TODO: currently we return an empty set, should implement this functionality
        return new HashSet<>();
    }

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "owner")
    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @ManyToOne
    @JoinColumn(name = "subject")
    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }
}
