package cognitivabrasil.repositorio.data.entities;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;

public class Files {

    private Integer id;
    private String name;
    private String location;
    private String contentType;
    private Document document;
    private Long size;
    private String randomName;
    
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
            return location + id;
        }
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public String getRandomName() {
        return randomName;
    }

    public void setRandomName(String randonName) {
        this.randomName = randonName;
    }

    public String getContentType() {
        // TODO Auto-generated method stub
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public void setPartialSize(long size) {
        if (this.size == null) {
            this.size = new Long(size);
        } else {
            this.size += size;
        }
    }

    public String getSizeFormatted() {
        String[] powerOfByte = {"Bytes", "KB", "MB", "GB", "TB"};
        int potencia = 0;
        int proxima = 0;
        boolean testaPotenciaActual;
        boolean testaPotenciaSeguinte;
        do {
            proxima = potencia + 1;
            testaPotenciaActual = (Math.pow(2, potencia * 10) <= this.size);
            testaPotenciaSeguinte = (this.size < Math.pow(2, proxima * 10));
            potencia++;

        } while (!(testaPotenciaActual && testaPotenciaSeguinte));

        potencia--;
        DecimalFormat myFormatter = new DecimalFormat("##.#");

        return myFormatter.format(this.size / Math.pow(2, potencia * 10)) + " " + powerOfByte[potencia];
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
