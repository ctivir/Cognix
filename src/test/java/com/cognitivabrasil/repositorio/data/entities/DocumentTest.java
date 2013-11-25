package com.cognitivabrasil.repositorio.data.entities;

import cognitivabrasil.obaa.OBAA;
import com.cognitivabrasil.repositorio.data.entities.Document;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import org.junit.Ignore;
import org.junit.Test;


public class DocumentTest {

    @Test
    public void testXmlDeserialization() throws IOException {
        Document d = new Document();

        String obaaXml = FileUtils.readFileToString(new File("src/test/resources/obaa1.xml"));

        d.setObaaXml(obaaXml);

        OBAA m = d.getMetadata();

        assertThat(m.getGeneral().getTitles(), hasItem("Título 1"));

    }

    /**
     * Test that calling getMetadata on an object that does not have XML
     * metadata throws exception.
     */
    @Ignore("We are currently creating a new Metadata if it doesnt exist yet.")
    @Test(expected = IllegalStateException.class)
    public void testThrowsException() {
        Document d = new Document();

        d.getMetadata();
    }
}
