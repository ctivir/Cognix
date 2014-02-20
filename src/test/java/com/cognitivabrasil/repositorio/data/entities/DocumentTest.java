package com.cognitivabrasil.repositorio.data.entities;

import cognitivabrasil.obaa.General.General;
import cognitivabrasil.obaa.OBAA;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;
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
    public void testTimestamp() {
        Document d = new Document();
        d.setCreated(new DateTime(1984, 8, 21, 0, 0, 0));
        assertThat(d.getTimestamp(), equalTo(new GregorianCalendar(1984, GregorianCalendar.AUGUST, 21).getTime()));
    }

    @Test
    public void testTimestampFormatted() {
        Document d = new Document();
        d.setCreated(new DateTime(1984, 8, 21, 0, 0, 0));

        assertThat(d.getTimestampFormatted(), equalTo("21/08/1984 00:00:00"));
    }

    @Test
    public void testTimestampFormattedNull() {
        Document d = new Document();
        d.setCreated(null);
        assertThat(d.getTimestampFormatted(), equalTo(""));
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

    @Test
    /**
     * verifica se as operações feitas em getMetadata não alteram o OBAA, se já
     * existir.
     */
    public void testGetMetadata() {
        Document d = new Document();
        assertThat(d.getMetadata(), notNullValue());

        d = new Document();
        OBAA obaa = new OBAA();
        obaa.setGeneral(new General());
        obaa.getGeneral().addTitle("Marcos");
        d.setMetadata(obaa);

        assertThat(d.getTitle(), equalTo("Marcos"));
    }

    @Test
    public void testGetXmlEqualToGetObaaXML() {
        Document d = new Document();
        d.setObaaXml("<xml>marcos</xml>");
        assertThat(d.getXml(), equalTo("<xml>marcos</xml>"));
        assertThat(d.getObaaXml(), equalTo(d.getXml()));
    }

    @Test
    public void testGetOAiIdentifier() {
        Document d = new Document();
        d.setObaaEntry("marcos");
        assertThat(d.getOaiIdentifier(), equalTo("marcos"));
    }

    @Test
    /**
     * Just to test the existence.
     */
    public void testGetSets() {
        Document d = new Document();
        assertThat(d.getSets(), notNullValue());
    }

    @Test
    public void testActive() {
        Document d = new Document();
        assertThat(d.isActive(), equalTo(false));
    }
}
