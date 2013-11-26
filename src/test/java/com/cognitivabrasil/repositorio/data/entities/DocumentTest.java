package com.cognitivabrasil.repositorio.data.entities;

import cognitivabrasil.obaa.OBAA;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import org.joda.time.DateTime;
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
    public void testTimestampFormatted() {
        Document d = new Document();
        d.setCreated(new DateTime(1984, 8, 21, 0, 0, 0));

        assertThat(d.getTimestampFormatted(), equalTo("21/08/1984 00:00:00"));
    }

    @Test
    public void testGetTitle() {
        Document d = new Document();

        assertThat(d.getTitle(), notNullValue());
        assertThat(d.getTitle(), equalTo("Sem título"));

        d = new Document();
        d.setObaaXml("<obaa></obaa>");
        assertThat(d.getTitle(), notNullValue());
        assertThat(d.getTitle(), equalTo("Sem título"));

        d = new Document();

        d.setObaaXml("<obaa:obaa xsi:schemaLocation=\"http://ltsc.ieee.org/xsd/LOM http://ltsc.ieee.org/xsd/obaav1.0/lom.xsd\" xmlns:obaa=\"http://ltsc.ieee.org/xsd/LOM\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
                + "<obaa:general><obaa:keyword>TCP</obaa:keyword><obaa:structure>atomic</obaa:structure></obaa:general></obaa:obaa>");
        assertThat(d.getMetadata().getGeneral(), notNullValue());

        assertThat(d.getTitle(), notNullValue());
        assertThat(d.getTitle(), equalTo("Sem título"));
    }
}
