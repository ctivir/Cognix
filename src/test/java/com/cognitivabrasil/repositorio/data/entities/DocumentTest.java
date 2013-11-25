package com.cognitivabrasil.repositorio.data.entities;

import cognitivabrasil.obaa.OBAA;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
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
    
    @Test
    public void testTimestampFormatted(){
        Document d = new Document();
        d.setCreated(new DateTime(1984, 8, 21, 0, 0, 0));
        
        assertThat(d.getTimestampFormatted(), equalTo("21/08/1984 00:00:00"));
    }
}
