package com.cognitivabrasil.repositorio.data.entities;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author cei-incubadora4
 */
@Entity(name = "files")
public class Files implements Serializable {

    private Integer id;
    private String name;
    private String location;
    private String contentType;
    private Document document;
    private Long sizeInBytes;
    private String randomName;    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        if (location.endsWith("/")) {
            return location + document.getId()+"/"+name;
        }
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @ManyToOne
    @JoinColumn(name = "document")
    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    @Column(name="random_name")
    public String getRandomName() {
        return randomName;
    }

    public void setRandomName(String randonName) {
        this.randomName = randonName;
    }

    @Column(name = "content_type")
    public String getContentType() {
        // TODO Auto-generated method stub
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Column(name = "file_size")
    public Long getSizeInBytes() {
        return sizeInBytes;
    }

    public void setSizeInBytes(Long size) {
        this.sizeInBytes = new Long(size);
    }

    public void setPartialSize(long size) {
        if (this.sizeInBytes == null) {
            this.sizeInBytes = new Long(size);
        } else {
            this.sizeInBytes += size;
        }
    }

    @Transient
    public String getSizeFormatted() {
        String[] powerOfByte = {"Bytes", "KB", "MB", "GB", "TB"};
        if (this.sizeInBytes == null || this.sizeInBytes <= 0) {
            return "Tamanho não definido";
        }
        int potencia = 0;
        int proxima;
        boolean testaPotenciaActual;
        boolean testaPotenciaSeguinte;
        do {
            proxima = potencia + 1;
            testaPotenciaActual = (Math.pow(2L, potencia * 10) <= this.sizeInBytes);
            testaPotenciaSeguinte = (this.sizeInBytes < Math.pow(2L, proxima * 10));
            potencia++;

        } while (!(testaPotenciaActual && testaPotenciaSeguinte));

        potencia--;

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        otherSymbols.setDecimalSeparator(',');
        otherSymbols.setGroupingSeparator('.');

        DecimalFormat myFormatter = new DecimalFormat("##.#", otherSymbols);


        return myFormatter.format(this.sizeInBytes / Math.pow(2L, potencia * 10)) + " " + powerOfByte[potencia];
    }

    public void deleteFile() throws IOException {
        FileUtils.forceDelete(new java.io.File(getLocation()));
    }

    public static boolean isFileItemInFileList(List<Files> filesList, FileItem file) {
        for (Files f : filesList) {
            if (f.getName().compareTo(file.getName()) == 0) {
                return true;
            }
        }
        return false;
    }
}