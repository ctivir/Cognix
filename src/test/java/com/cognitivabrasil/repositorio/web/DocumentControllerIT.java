/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitivabrasil.repositorio.web;

import cognitivabrasil.obaa.Accessibility.Accessibility;
import cognitivabrasil.obaa.Accessibility.Primary;
import cognitivabrasil.obaa.Educational.Context;
import cognitivabrasil.obaa.Educational.Educational;
import cognitivabrasil.obaa.Educational.IntendedEndUserRole;
import cognitivabrasil.obaa.Educational.Interaction;
import cognitivabrasil.obaa.Educational.InteractionType;
import cognitivabrasil.obaa.Educational.InteractivityLevel;
import cognitivabrasil.obaa.Educational.InteractivityType;
import cognitivabrasil.obaa.Educational.LearningContentType;
import cognitivabrasil.obaa.Educational.LearningResourceType;
import cognitivabrasil.obaa.Educational.Perception;
import cognitivabrasil.obaa.Educational.Reciprocity;
import cognitivabrasil.obaa.General.Identifier;
import cognitivabrasil.obaa.General.Structure;
import cognitivabrasil.obaa.LifeCycle.Role;
import cognitivabrasil.obaa.LifeCycle.Status;
import cognitivabrasil.obaa.Metametadata.Metametadata;
import cognitivabrasil.obaa.OBAA;
import cognitivabrasil.obaa.Relation.Kind;
import cognitivabrasil.obaa.Technical.Name;
import cognitivabrasil.obaa.Technical.SupportedPlatform;
import cognitivabrasil.obaa.Technical.Technical;
import cognitivabrasil.obaa.Technical.Type;
import com.cognitivabrasil.repositorio.data.entities.Document;
import com.cognitivabrasil.repositorio.data.entities.Files;
import com.cognitivabrasil.repositorio.data.entities.User;
import com.cognitivabrasil.repositorio.services.DocumentService;
import com.cognitivabrasil.repositorio.services.UserService;
import com.cognitivabrasil.repositorio.util.Message;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import static org.hamcrest.Matchers.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 *
 * @author Marcos Freitas Nunes <marcos@cognitivabrasil.com.br>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "file:src/main/webapp/WEB-INF/spring/root-context.xml")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class DocumentControllerIT extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private DocumentService docService;
    @Autowired
    private UserService userService;
    @Autowired
    private DocumentsController controller;
    @PersistenceContext
    private EntityManager em;

    private ExtendedModelMap uiModel;
    private MockHttpServletResponse response;

    @Before
    public void init() {
        uiModel = new ExtendedModelMap();
        response = new MockHttpServletResponse();
    }

    private HttpServletRequest logUserAndPermission(boolean docEditor) {
        User loggerUser = new User();
        loggerUser.setName("marcos");

        Authentication auth = new UsernamePasswordAuthenticationToken(loggerUser, "nada");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(auth);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.isUserInRole(User.MANAGE_DOC)).thenReturn(docEditor);
        return request;
    }

    @Test
    public void testGetDocError() throws IOException {

        String result = controller.show(99, uiModel, response);
        assertThat(result, equalTo("ajax"));
        assertThat(response.getStatus(), equalTo(404));
    }

    @Test
    public void testGetDocDeleted() throws IOException {

        String result = controller.show(2, uiModel, response);
        assertThat(result, equalTo("ajax"));
        assertThat(response.getStatus(), equalTo(410));
    }

    @Test
    public void testGetDoc() throws IOException {

        String result = controller.show(5, uiModel, response);
        assertThat(result, equalTo("documents/show"));
        Document d = (Document) uiModel.get("doc");
        assertThat(d, notNullValue());
        assertThat(d.getMetadata(), notNullValue());
        assertThat(d.getMetadata().getGeneral(), notNullValue());
        assertThat(d.getMetadata().getGeneral().getTitles().get(0), equalTo("Ataque a o TCP - Mitnick"));

        assertThat(d.getMetadata().getGeneral().getStructure().getLanguage(), equalTo("pt"));
        assertThat(d.getMetadata().getGeneral().getStructure().getCountry(), equalTo("BR"));
    }

    @Test
    public void testDelete(){
        HttpServletRequest request = logUserAndPermission(true);

        int docsBefore = docService.getAll().size();

        Message msg = controller.delete(1, request);
        assertThat(msg.getType(), equalTo(Message.SUCCESS));
        assertThat(msg.getMessage(), equalTo("Documento excluido com sucesso, mas os seus arquivos não foram encontrados"));

        em.flush();
        em.clear();

        assertThat(docService.getAll().size(), equalTo(docsBefore - 1));
    }

    @Test
    public void testDeleteError() throws IOException {

        HttpServletRequest request = mock(HttpServletRequest.class);

        Message msg = controller.delete(99, request);
        assertThat(msg.getType(), equalTo(Message.ERROR));
        assertThat(msg.getMessage(), equalTo("O documento solicitado não foi encontrado."));
    }

    @Test
    public void testDeleteErrorPermission() throws IOException {
        HttpServletRequest request = logUserAndPermission(false);

        int docsBefore = docService.getAll().size();

        Message msg = controller.delete(1, request);
        assertThat(msg.getType(), equalTo(Message.ERROR));
        assertThat(msg.getMessage(), equalTo("Acesso negado! Você não ter permissão para deletar este documento."));

        em.flush();
        em.clear();

        assertThat(docService.getAll().size(), equalTo(docsBefore));
    }
    
    @Test
    public void testNewDocument() {
        DateTime before = new DateTime();
        String result = controller.newShow(uiModel);

        assertThat(result, equalTo("documents/new"));

        Document d = (Document) uiModel.get("doc");
        assertThat(d, notNullValue());
        assertThat(d.getCreated().isAfter(before.minusMillis(10)), equalTo(true));
        assertThat(d.getCreated().isBefore(new DateTime()), equalTo(true));

        int id = d.getId();
        assertThat(id, notNullValue());

        OBAA obaa = (OBAA) uiModel.get("obaa");
        assertThat(obaa, notNullValue());

        List<Identifier> idList = obaa.getGeneral().getIdentifiers();
        assertThat(idList, hasSize(1));
        Identifier uri = idList.get(0);
        assertThat(uri.getCatalog(), equalTo("URI"));
        assertThat(uri.getEntry(), equalTo("http://cognitivabrasil.com.br/repositorio/documents/" + id));

    }
    
    @Test
    public void testNewClassPlan() {
        DateTime before = new DateTime();
        String result = controller.newClassPlan(uiModel);

        assertThat(result, equalTo("documents/new"));

        Document d = (Document) uiModel.get("doc");
        assertThat(d, notNullValue());
        assertThat(d.getCreated(), notNullValue());
        assertThat(d.getCreated().isAfter(before.minusMillis(10)), equalTo(true));
        assertThat(d.getCreated().isBefore(new DateTime()), equalTo(true));

        int id = d.getId();
        assertThat(id, notNullValue());

        OBAA obaa = (OBAA) uiModel.get("obaa");
        assertThat(obaa, notNullValue());

        List<Identifier> idList = obaa.getGeneral().getIdentifiers();
        assertThat(idList, hasSize(1));
        Identifier uri = idList.get(0);
        assertThat(uri.getCatalog(), equalTo("URI"));
        assertThat(uri.getEntry(), equalTo("http://cognitivabrasil.com.br/repositorio/documents/" + id));
        
        //testes para classPlan realmente
        assertThat(obaa.getGeneral().getStructure().toString(), equalTo(Structure.COLLECTION));
        assertThat(obaa.getGeneral().getAggregationLevel().toString(), equalTo("3"));
        assertThat(obaa.getGeneral().getKeywords(), hasSize(2));
        assertThat(obaa.getGeneral().getKeywords(), hasItem("Plano de Aula"));
        
        assertThat(obaa.getLifeCycle().getVersion(), equalTo("1"));
        assertThat(obaa.getLifeCycle().getStatus(), equalTo(Status.FINALIZED));
        assertThat(obaa.getLifeCycle().getContribute().get(0).getRole().toString(), equalTo(Role.PUBLISHER));
        assertThat(obaa.getLifeCycle().getContribute().get(0).getFirstEntity(), equalTo("BEGIN:VCARD\nVERSION:3.0\nN:Ministério da Educação;do Brasil;;;\nFN:Ministério da Educação do Brasil\nEND:VCARD"));
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        assertThat(obaa.getLifeCycle().getContribute().get(0).getDate(), equalTo(dateFormat.format(date)));
        Technical technical = obaa.getTechnical();
        assertThat(technical.getRequirement().get(0).getOrComposite().get(0).getType(), equalTo(Type.OPERATING_SYSTEM));
        assertThat(technical.getRequirement().get(0).getOrComposite().get(0).getName(), equalTo(Name.MULTI_OS));
        assertThat(technical.getRequirement().get(0).getOrComposite().get(1).getType(), equalTo(Type.BROWSER));
        assertThat(technical.getRequirement().get(0).getOrComposite().get(1).getName(), equalTo(Name.ANY));
        
        assertThat(technical.getOtherPlatformRequirements(), equalTo("É necessário um programa como o acrobat reader que permite a leitura de arquivos no formato PDF."));
        assertThat(technical.getSupportedPlatforms().get(0), equalTo(SupportedPlatform.WEB));
        
        Educational educational = obaa.getEducational();
        assertThat(educational.getInteractivityType(), equalTo(InteractivityType.EXPOSITIVE));
        assertThat(educational.getLearningResourceTypesString(), hasItem(LearningResourceType.LECTURE));
        assertThat(educational.getInteractivityLevel().toString(), equalTo(InteractivityLevel.VERY_LOW));
        assertThat(educational.getDescriptions().get(0), equalTo("Plano de aula envolvendo o uso do computador ou recursos alternativos."));
        assertThat(educational.getLanguages().get(0), equalTo("pt-BR"));
        assertThat(educational.getLearningContentType(), equalTo(LearningContentType.PROCEDIMENTAL));
        assertThat(educational.getContexts().get(0), equalTo(Context.SCHOOL));
        assertThat(educational.getIntendedEndUserRoles().get(0), equalTo(IntendedEndUserRole.TEACHER));
        
        Interaction interaction = obaa.getEducational().getInteraction();
        assertThat(interaction.getInteractionType().toString(), equalTo(InteractionType.OBJECT_INDIVIDUAL));
        assertThat(interaction.getCoPresence().getBoolean(), equalTo(false));
        assertThat(interaction.getSynchronism().getBoolean(), equalTo(false));
        assertThat(interaction.getPerception().toString(), equalTo(Perception.VISUAL));
        assertThat(interaction.getReciprocity().toString(), equalTo(Reciprocity.ONE_N));
        
        assertThat(obaa.getRights().getCost().getBoolean(), equalTo(false));
        
        Primary primary = obaa.getAccessibility().getResourceDescription().getPrimary();
        assertThat(primary.isVisual(), equalTo(true));
        assertThat(primary.isAuditory(), equalTo(false));
        assertThat(primary.isText(), equalTo(true));
        assertThat(primary.isTactile(), equalTo(false));
    }

    
    @Test
    public void testNewDo() throws IOException {
        DateTime before = new DateTime();
        User loggerUser = new User();
        loggerUser.setName("marcos");
        loggerUser.setUsername("marcos");

        Authentication auth = new UsernamePasswordAuthenticationToken(loggerUser, "nada");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(auth);
        
        MockHttpServletRequest request = new MockHttpServletRequest();

        Document doc = new Document();
        
        Files file = new Files();
        file.setName("001.jpg");
        file.setLocation("./src/test/resources/files/001.jpg");
        file.setDocument(doc);
        file.setSizeInBytes(90L);
        List<Files> listF = new ArrayList<>();
        listF.add(file);
        doc.setFiles(listF);
        docService.save(doc);

        request.addParameter("obaa.general.identifiers[0].catalog", "URI");
        request.addParameter("obaa.general.identifiers[0].entry", "5");
        request.addParameter("obaa.general.titles[0]", "title1");
        request.addParameter("obaa.general.titles[1]", "title2");
        request.addParameter("obaa.general.keywords[0]", "keyword1");
        request.addParameter("obaa.general.keywords[1]", "keyword2");
        request.addParameter("obaa.general.keywords[2]", "");
        request.addParameter("obaa.general.languages[0]", "pt-BR");
        request.addParameter("obaa.general.languages[1]", "en-US");
        request.addParameter("obaa.general.descriptions[0]", "descrição do objeto");
        request.addParameter("obaa.general.coverages[0]", "cobertura");
        request.addParameter("obaa.general.structure", "collection");
        request.addParameter("obaa.general.aggregationLevel", "2");
        request.addParameter("obaa.lifeCycle.version", "1");
        request.addParameter("obaa.lifeCycle.status", "finalized");
        request.addParameter("obaa.lifeCycle.contribute[0].role", "graphical_designer");
        request.addParameter("obaa.lifeCycle.contribute[0].entity[0]", "MFN");
        request.addParameter("obaa.lifeCycle.contribute[0].date", "20/11/2013");
        request.addParameter("obaa.rights.cost", "true");
        request.addParameter("obaa.rights.copyright", "true");
        request.addParameter("obaa.rights.description", "Todos os direitos autorais reservados");
        request.addParameter("obaa.educational.interactivityType", "mixed");
        request.addParameter("obaa.educational.intendedEndUserRole[2]", "manager");
        request.addParameter("obaa.educational.learningResourceType[0]", "problem_statement");
        request.addParameter("obaa.educational.interactivityLevel", "low");
        request.addParameter("obaa.educational.semanticDensity", "high");
        request.addParameter("obaa.educational.difficulty", "medium");
        request.addParameter("obaa.educational.typicalAgeRanges[0]", "1 - 18 anos");
        request.addParameter("obaa.educational.typicalLearningTime", "PT15M");
        request.addParameter("obaa.educational.description[0]", "descricao educacional");
        request.addParameter("obaa.educational.language[0]", "es-UY");
        request.addParameter("obaa.educational.contexts[3]", "training");
        request.addParameter("obaa.educational.learningContentType", "factual");
        request.addParameter("obaa.educational.interaction.interactionType", InteractionType.OBJECT_INDIVIDUAL);
        request.addParameter("obaa.educational.interaction.perception", "mixed");
        request.addParameter("obaa.educational.interaction.synchronism", "true");
        request.addParameter("obaa.educational.interaction.coPresence", "true");
        request.addParameter("obaa.educational.interaction.reciprocity", "1-n");
        request.addParameter("obaa.accessibility.resourceDescription.primary.hasVisual", "true");
        request.addParameter("obaa.accessibility.resourceDescription.primary.hasAuditory", "true");
        request.addParameter("obaa.accessibility.resourceDescription.primary.hasText", "true");
        request.addParameter("obaa.accessibility.resourceDescription.primary.hasTactile", "true");
        request.addParameter("obaa.technical.format[0]", "pdf");
        request.addParameter("obaa.technical.location[0]", "www.marcosnunes.com/5");
        request.addParameter("obaa.technical.requirement[0].orComposite[0].type", "operatingSystem");
        request.addParameter("obaa.technical.requirement[0].orComposite[0].name", "IOS");
        request.addParameter("obaa.technical.requirement[0].orComposite[0].minimumVersion", "6.1");
        request.addParameter("obaa.technical.requirement[0].orComposite[0].maximumVersion", "7.0");
        request.addParameter("obaa.technical.requirement[0].orComposite[1].type", "browser");
        request.addParameter("obaa.technical.requirement[0].orComposite[1].name", "any");
        request.addParameter("obaa.technical.installationRemarks", "nnf");
        request.addParameter("obaa.technical.otherPlatformRequirements", "internet");
        request.addParameter("obaa.technical.duration", "15 min");
        request.addParameter("obaa.technical.supportedPlatforms[1]", "dtv");
        request.addParameter("obaa.technical.platformSpecificFeatures[0].specificRequirements[0].specificOrComposites[0].specificType", "tipo");
        request.addParameter("obaa.technical.platformSpecificFeatures[0].specificRequirements[0].specificOrComposites[0].specificName", "nome");
        request.addParameter("obaa.technical.platformSpecificFeatures[0].specificRequirements[0].specificOrComposites[0].specificMinimumVersion", "1.0");
        request.addParameter("obaa.technical.platformSpecificFeatures[0].specificRequirements[0].specificOrComposites[0].specificMaximumVersion", "2.0");
        request.addParameter("obaa.technical.platformSpecificFeatures[0].specificInstallationRemarks", "installationRemarks");
        request.addParameter("obaa.technical.platformSpecificFeatures[0].specificOtherPlatformRequirements", "otherPlatformRequirements");

        String result = controller.newDo(request, doc.getId());
        assertThat(result, equalTo("redirect:/documents/"));

        int id = doc.getId();


        Document docResult = docService.get(id);
        assertThat(docResult.getCreated().isAfter(before.minusMillis(10)), equalTo(true));
        assertThat(docResult.getCreated().isBefore(new DateTime()), equalTo(true));
        
        assertThat(docResult.getMetadata().getGeneral().getTitles(), hasSize(2));
        assertThat(docResult.getMetadata().getGeneral().getKeywords(), hasSize(2));
        assertThat(docResult.getMetadata().getGeneral().getIdentifiers().get(0).getCatalog(), equalTo("URI"));
        assertThat(docResult.getMetadata().getGeneral().getIdentifiers().get(0).getEntry(), equalTo("5"));
        assertThat(docResult.getFiles(), hasSize(1));

        assertThat(docResult.getMetadata().getGeneral().getLanguages().get(0).toString(), equalTo("pt-BR"));

        assertThat(docResult.getMetadata().getGeneral().getLanguages().get(1), equalTo("en-US"));
        assertThat(docResult.getMetadata().getGeneral().getDescriptions().get(0), equalTo("descrição do objeto"));
        assertThat(docResult.getMetadata().getGeneral().getCoverages().get(0), equalTo("cobertura"));
        assertThat(docResult.getMetadata().getGeneral().getStructure().toString(), equalTo("collection"));
        assertThat(docResult.getMetadata().getGeneral().getAggregationLevel().toString(), equalTo("2"));
        assertThat(docResult.getMetadata().getLifeCycle().getVersion(), equalTo("1"));
        assertThat(docResult.getMetadata().getLifeCycle().getStatus(), equalTo("finalized"));
        assertThat(docResult.getMetadata().getLifeCycle().getContribute().get(0).getRole().toString(), equalTo("graphical_designer"));
        assertThat(docResult.getMetadata().getLifeCycle().getContribute().get(0).getEntities().get(0), equalTo("MFN"));
        assertThat(docResult.getMetadata().getLifeCycle().getContribute().get(0).getDate(), equalTo("20/11/2013"));
        assertThat(docResult.getMetadata().getRights().getCost().toString(), equalTo("true"));
        assertThat(docResult.getMetadata().getRights().getCopyright().toString(), equalTo("true"));
        assertThat(docResult.getMetadata().getRights().getDescription(), equalTo("Todos os direitos autorais reservados"));
        Educational educational = docResult.getMetadata().getEducational();
        assertThat(educational.getInteractivityType(), equalTo("mixed"));
        assertThat(educational.getIntendedEndUserRoles().get(0), equalTo(IntendedEndUserRole.MANAGER));
        assertThat(educational.getLearningResourceTypes().get(0).toString(), equalTo("problem_statement"));
        assertThat(educational.getInteractivityLevel().toString(), equalTo(InteractivityLevel.LOW));
        assertThat(educational.getSemanticDensity().toString(), equalTo("high"));
        assertThat(educational.getDifficulty().toString(), equalTo("medium"));
        assertThat(educational.getTypicalAgeRanges().get(0), equalTo("1 - 18 anos"));
        assertThat(educational.getTypicalLearningTime().toString(), equalTo("PT15M"));
        assertThat(educational.getDescription().get(0).toString(), equalTo("descricao educacional"));
        assertThat(educational.getLanguage().get(0).toString(), equalTo("es-UY"));
        assertThat(educational.getContexts().get(0), equalTo(Context.TRAINING));
        assertThat(educational.getLearningContentType(), equalTo(LearningContentType.FACTUAL));
        assertThat(educational.getInteraction().getInteractionType().toString(), equalTo(InteractionType.OBJECT_INDIVIDUAL));
        assertThat(educational.getInteraction().getPerception().toString(), equalTo(Perception.MIXED));
        assertThat(educational.getInteraction().getSynchronism().toString(), equalTo("true"));
        assertThat(educational.getInteraction().getCoPresence().toString(), equalTo("true"));
        assertThat(educational.getInteraction().getReciprocity().toString(), equalTo(Reciprocity.ONE_N));
        Accessibility accessibility = docResult.getMetadata().getAccessibility();
        assertThat(accessibility.getResourceDescription().getPrimary().getHasVisual(), equalTo("true"));
        assertThat(accessibility.getResourceDescription().getPrimary().getHasAuditory(), equalTo("true"));
        assertThat(accessibility.getResourceDescription().getPrimary().getHasText(), equalTo("true"));
        assertThat(accessibility.getResourceDescription().getPrimary().getHasTactile(), equalTo("true"));
        Technical technical = docResult.getMetadata().getTechnical();
        assertThat(technical.getFormat().get(0).toString(), equalTo("pdf"));
        assertThat(technical.getLocation().get(0).toString(), equalTo("www.marcosnunes.com/5"));
        assertThat(technical.getRequirement().get(0).getOrComposite().get(0).getType(), equalTo("operatingSystem"));
        assertThat(technical.getRequirement().get(0).getOrComposite().get(0).getName(), equalTo("IOS"));
        assertThat(technical.getRequirement().get(0).getOrComposite().get(0).getMinimumVersion(), equalTo("6.1"));
        assertThat(technical.getRequirement().get(0).getOrComposite().get(0).getMaximumVersion(), equalTo("7.0"));
        assertThat(technical.getRequirement().get(0).getOrComposite().get(1).getType(), equalTo("browser"));
        assertThat(technical.getRequirement().get(0).getOrComposite().get(1).getName(), equalTo("any"));
        assertThat(technical.getInstallationRemarks(), equalTo("nnf"));
        assertThat(technical.getOtherPlatformRequirements(), equalTo("internet"));
        assertThat(technical.getDuration(), equalTo("15 min"));
        assertThat(technical.getSupportedPlatforms().get(0), equalTo(SupportedPlatform.DTV));
        assertThat(technical.getPlatformSpecificFeatures().get(0).getSpecificRequirements().get(0).getSpecificOrComposites().get(0).getSpecificType(), equalTo("tipo"));
        assertThat(technical.getPlatformSpecificFeatures().get(0).getSpecificRequirements().get(0).getSpecificOrComposites().get(0).getSpecificName(), equalTo("nome"));
        assertThat(technical.getPlatformSpecificFeatures().get(0).getSpecificRequirements().get(0).getSpecificOrComposites().get(0).getSpecificMinimumVersion(), equalTo("1.0"));
        assertThat(technical.getPlatformSpecificFeatures().get(0).getSpecificRequirements().get(0).getSpecificOrComposites().get(0).getSpecificMaximumVersion(), equalTo("2.0"));
        assertThat(technical.getPlatformSpecificFeatures().get(0).getSpecificInstallationRemarks(), equalTo("installationRemarks"));
        assertThat(technical.getPlatformSpecificFeatures().get(0).getSpecificOtherPlatformRequirements(), equalTo("otherPlatformRequirements"));
        Metametadata meta = docResult.getMetadata().getMetametadata();
        assertThat(meta.getContribute().get(0).getFirstEntity(),equalTo("BEGIN:VCARD\nVERSION:3.0\nN:marcos;;;;\nFN:marcos\nEND:VCARD"));
        assertThat(meta.getContribute().get(0).getRole(),equalTo("author"));
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        assertThat(meta.getContribute().get(0).getDate(), equalTo(dateFormat.format(date)));
        assertThat(meta.getIdentifier().get(0).getCatalog(), equalTo("URI"));
        assertThat(meta.getIdentifier().get(0).getEntry(), equalTo("http://www.w3.org/2001/XMLSchema-instance"));
        assertThat(meta.getSchema(), hasSize(1));
    }
    
    @Test
    public void testNewDoSizeExisting() throws IOException {
        User loggerUser = new User();
        loggerUser.setName("marcos");
        loggerUser.setUsername("marcos");

        Authentication auth = new UsernamePasswordAuthenticationToken(loggerUser, "nada");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(auth);
        
        MockHttpServletRequest request = new MockHttpServletRequest();

        Document doc = new Document();
        doc.setFiles(new ArrayList<Files>());
        docService.save(doc);
        
        request.addParameter("obaa.general.identifiers[0].catalog", "URI");
        request.addParameter("obaa.general.identifiers[0].entry", "5");
        request.addParameter("obaa.general.titles[0]", "title1");
        
        String result = controller.newDo(request, doc.getId());
        assertThat(result, equalTo("redirect:/documents/"));

        int id = doc.getId();


        Document docResult = docService.get(id);
        assertThat(docResult.getMetadata().getGeneral().getTitles(), hasSize(1));
        assertThat(docResult.getMetadata().getGeneral().getTitles().get(0), equalTo("title1"));
    }
    
    @Test
    public void testNewVersionOf(){
        String result = controller.newVersionOf(uiModel, 1);
        assertThat(result, equalTo("documents/new"));

        Document dv = (Document) uiModel.get("doc");
        assertThat(dv, notNullValue());
        
        em.flush();
        
        Document dOrg = docService.get(1);
        
        System.out.println(dOrg.getMetadata().getGeneral().getTitles().get(0));
        
        assertThat(dOrg.getMetadata().getGeneral().getTitles().get(0), equalTo(dv.getMetadata().getGeneral().getTitles().get(0)));
        assertThat(dv.getMetadata().getGeneral().getIdentifiers().get(0).getEntry(), equalTo("http://cognitivabrasil.com.br/repositorio/documents/"+dv.getId()));
        
        assertThat(dOrg.getMetadata().getRelations(), hasSize(1));
        assertThat(dOrg.getMetadata().getRelations().get(0).getKind(), equalTo(Kind.HAS_VERSION));
        assertThat(dOrg.getMetadata().getRelations().get(0).getResource().getIdentifier(), hasSize(1));
        String entryVersionOf = dv.getMetadata().getGeneral().getIdentifiers().get(0).getEntry();
        assertThat(dOrg.getMetadata().getRelations().get(0).getResource().getIdentifier().get(0).getEntry(), equalTo(entryVersionOf));
        
        assertThat(dv.getMetadata().getRelations().get(0).getKind(), equalTo(Kind.IS_VERSION_OF));
        String entryOrg = dOrg.getMetadata().getGeneral().getIdentifiers().get(0).getEntry();
        assertThat(dv.getMetadata().getRelations().get(0).getResource().getIdentifier().get(0).getEntry(), equalTo(entryOrg));
    }
    
    @Test
    public void testEditErrorPermission() throws IOException {
        HttpServletRequest request = logUserAndPermission(false);
        String result = controller.edit(uiModel, 1, response, request);
        assertThat(result, equalTo("ajax"));
        assertThat(response.getStatus(), equalTo(HttpServletResponse.SC_FORBIDDEN));
    }
    
    @Test
    public void testManagerEditing() throws IOException {
        HttpServletRequest request = logUserAndPermission(true);

        String result = controller.edit(uiModel, 1, response, request);
        assertThat(result, equalTo("documents/new"));

        Document d = (Document) uiModel.get("doc");
        assertThat(d, notNullValue());
        OBAA obaa = (OBAA) uiModel.get("obaa");
        assertThat(obaa, notNullValue());

        DateTime date = DateTime.parse("2013-05-08T03:00:00Z");
        assertThat(d.getCreated().withZone(DateTimeZone.UTC).isEqual(date), equalTo(true));

        assertThat(obaa.getGeneral().getTitles().get(0), equalTo("Ataque a o TCP - Mitnick"));
        assertThat(obaa.getGeneral().getKeywords(), hasSize(1));
        assertThat(obaa.getGeneral().getKeywords(), hasItem("TCP"));
    }

    @Test
    public void testAuthorEditing() throws IOException {
        User loggerUser = userService.get(3);

        Authentication auth = new UsernamePasswordAuthenticationToken(loggerUser, "nada");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(auth);

        MockHttpServletRequest request = new MockHttpServletRequest();

        String result = controller.edit(uiModel, 5, response, request);
        assertThat(result, equalTo("documents/new"));

    }
    
    @Test
    public void testEditDoErrorPermission() throws IOException {
        HttpServletRequest request = logUserAndPermission(false);
        String result = controller.editDo(uiModel, 1, request, response);
        assertThat(result, equalTo("ajax"));
        assertThat(response.getStatus(), equalTo(HttpServletResponse.SC_FORBIDDEN));
    }
    
    @Test
    public void testEditDo() throws IOException {
        User loggerUser = userService.get(2);

        Authentication auth = new UsernamePasswordAuthenticationToken(loggerUser, "nada");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(auth);
        
        MockHttpServletRequest request = new MockHttpServletRequest();

        Document doc = docService.get(1);

        request.addParameter("obaa.general.titles[0]", "title1");
        request.addParameter("obaa.general.keywords[0]", "keyword1");
        request.addParameter("obaa.general.keywords[1]", "keyword2");
        request.addParameter("obaa.general.identifiers[0].catalog", doc.getMetadata().getGeneral().getIdentifiers().get(0).getCatalog());
        request.addParameter("obaa.general.identifiers[0].entry", doc.getMetadata().getGeneral().getIdentifiers().get(0).getEntry());
        
        String result = controller.editDo(uiModel, doc.getId(), request, response);
        assertThat(result, equalTo("redirect:/documents/"));

        Document docResult = docService.get(1);
        
        assertThat(docResult.getMetadata().getGeneral().getTitles(), hasSize(1));
        assertThat(docResult.getMetadata().getGeneral().getKeywords(), hasSize(2));
        assertThat(docResult.getMetadata().getGeneral().getIdentifiers().get(0).getCatalog(), equalTo("URI"));
        assertThat(docResult.getMetadata().getGeneral().getIdentifiers().get(0).getEntry(), equalTo("http://cognitivabrasil.com.br/repositorio/documents/1"));
    }
    
}
