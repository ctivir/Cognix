/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cognitivabrasil.repositorio.web;

import cognitivabrasil.obaa.General.Identifier;
import cognitivabrasil.obaa.OBAA;
import com.cognitivabrasil.repositorio.data.entities.Document;
import com.cognitivabrasil.repositorio.data.entities.User;
import com.cognitivabrasil.repositorio.services.DocumentService;
import com.cognitivabrasil.repositorio.services.UserService;
import java.io.IOException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import static org.hamcrest.MatcherAssert.assertThat;
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

    @Before
    public void init() {
        uiModel = new ExtendedModelMap();
    }
    
    @Test
    public void testListDocuments(){
        User loggerUser = new User();
        loggerUser.setName("marcos");
        
        Authentication auth = new UsernamePasswordAuthenticationToken(loggerUser, "nada");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(auth);
        
        String result = controller.main(uiModel);
        assertThat(result, equalTo("documents/"));
       
        List<Document> docs = (List<Document>) uiModel.get("documents");
        assertThat(docs, notNullValue());
        assertThat(docs, hasSize(2)); //a ordem já é testada no service
        
        String currentUser = (String) uiModel.get("currentUser");
//        assertThat(currentUser, equalTo(loggerUser.getName()));
        
        String permDocAdmin = (String) uiModel.get("permDocAdmin");
        String permCreateDoc = (String) uiModel.get("permCreateDoc");
        assertThat(permDocAdmin, equalTo(User.MANAGE_DOC));
        assertThat(permCreateDoc, equalTo(User.CREATE_DOC));
        
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
        assertThat(uri.getEntry(), equalTo("http://cognitivabrasil.com.br/repositorio/documents/"+id));

    }


    @Test
    public void testManagerEditing() throws IOException{
        User loggerUser = new User();
        loggerUser.setName("marcos");
        
        Authentication auth = new UsernamePasswordAuthenticationToken(loggerUser, "nada");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(auth);
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.isUserInRole(User.MANAGE_DOC)).thenReturn(Boolean.TRUE);
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
    public void testAuthorEditing() throws IOException{
        User loggerUser = userService.get(3);
        
        Authentication auth = new UsernamePasswordAuthenticationToken(loggerUser, "nada");
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(auth);
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        
        String result = controller.edit(uiModel, 5, response, request);
        assertThat(result, equalTo("documents/new"));
        
    }


//    /**
//     * Teste de um aluno tentando ver um documento de professor. O aluno tem
//     * permissão de para ver revistinhas mas o documento não da permissão para
//     * aluno.
//     *
//     * @throws IOException
//     */
//    @Test
//    public void testGetDocError2() throws IOException {
//        MockHttpServletResponse response = new MockHttpServletResponse();
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        when(request.isUserInRole(User.VIEW_COMICS)).thenReturn(Boolean.TRUE);
//        String result = controller.main(uiModel, 1L, request, response);
//        assertThat(result, equalTo("ajax"));
//        assertThat(response.getStatus(), equalTo(HttpServletResponse.SC_FORBIDDEN));
//    }
//
//    @Test
//    public void testGetDoc() throws IOException {
//        MockHttpServletResponse response = new MockHttpServletResponse();
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        when(request.isUserInRole(User.VIEW_COMICS)).thenReturn(Boolean.TRUE);
//        String result = controller.main(uiModel, 4L, request, response);
//        assertThat(result, equalTo("comics/show"));
//
//        List<Files> files = (List<Files>) uiModel.get("pages");
//        assertThat(files, hasSize(0));
//    }
//
//    @Test
//    public void testDelete() {
//        int docsBefore = docService.getAll().size();
//        Message msg = controller.delete(1L);
//        assertThat(msg.getType(), equalTo(Message.SUCCESS));
//        assertThat(msg.getMessage(), equalTo("Documento excluido com sucesso, mas os seus arquivos não foram encontrados"));
//
//        em.flush();
//        em.clear();
//
//        assertThat(docService.getAll().size(), equalTo(docsBefore - 1));
//    }
//
//    @Test
//    public void testDeleteError() {
//        int docsBefore = docService.getAll().size();
//        Message msg = controller.delete(99L);
//        assertThat(msg.getType(), equalTo(Message.ERROR));
//        assertThat(msg.getMessage(), equalTo("Erro ao excluir documento"));
//
//        em.flush();
//        em.clear();
//
//        assertThat(docService.getAll().size(), equalTo(docsBefore));
//    }
//
//    @Test
//    public void testShowMetadataInvalidDoc() {
//        String result = controller.showMetadata(99L, uiModel);
//        assertThat(result, equalTo("redirect:/"));
//    }
//
//    @Test
//    public void testShowMetadata() {
//        String result = controller.showMetadata(1L, uiModel);
//        assertThat(result, equalTo("documents/show"));
//        Document d = (Document) uiModel.get("doc");
//        assertThat(d, notNullValue());
//        assertThat(d.getObaa(), notNullValue());
//        assertThat(d.getObaa().getGeneral(), notNullValue());
//        assertThat(d.getObaa().getGeneral().getTitles().get(0), equalTo("Ataque a o TCP - Mitnick"));
//
//        assertThat(d.getObaa().getGeneral().getStructure().getLanguage(), equalTo("pt"));
//        assertThat(d.getObaa().getGeneral().getStructure().getCountry(), equalTo("BR"));
//    }
//
//    @Test
//    public void testGetThumbnailError() throws IOException {
//        MockHttpServletResponse response = new MockHttpServletResponse();
//        controller.getThumbnail(null, response);
//        assertThat(response.getStatus(), equalTo(410));
//
//        response = new MockHttpServletResponse();
//
//        controller.getThumbnail(0L, response);
//        assertThat(response.getStatus(), equalTo(410));
//
//    }
//
//    @Test
//    public void testGetThumbnailError2() throws IOException {
//        MockHttpServletResponse response = new MockHttpServletResponse();
//        controller.getThumbnail(1L, response);
//        assertThat(response.getStatus(), equalTo(410));
//
//    }
//    
//    @Test
//    public void testNewDo() throws IOException {
//        /**
//         *
//         * TODO: FAZER TUDO COM MOCK, ELIMINAR A BASE DE DADOS
//         *
//         */
//        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
//        MockHttpServletRequest request = new MockHttpServletRequest();
//
//        Document doc = new Document();
//
//        Files file = new Files();
//        file.setName("001.jpg");
//        file.setLocation("./src/test/resources/files/001.jpg");
//        file.setDocument(doc);
//        file.setSizeInBytes(90L);
//        List<Files> listF = new ArrayList<>();
//        listF.add(file);
//        doc.setFiles(listF);
//        docService.save(doc);
//
//        request.addParameter("obaa.general.identifiers[0].catalog", "URI");
//        request.addParameter("obaa.general.identifiers[0].entry", "5");
//        request.addParameter("obaa.general.titles[0]", "title1");
//        request.addParameter("obaa.general.titles[1]", "title2");
//        request.addParameter("obaa.general.keywords[0]", "keyword1");
//        request.addParameter("obaa.general.keywords[1]", "keyword2");
//        request.addParameter("obaa.general.keywords[2]", "");
//        request.addParameter("obaa.general.languages[0]", "pt-BR");
//        request.addParameter("obaa.general.languages[1]", "en-US");
//        request.addParameter("obaa.general.descriptions[0]", "descrição do objeto");
//        request.addParameter("obaa.general.coverages[0]", "cobertura");
//        request.addParameter("obaa.general.structure", "collection");
//        request.addParameter("obaa.general.aggregationLevel", "2");
//        request.addParameter("obaa.lifeCycle.version", "1");
//        request.addParameter("obaa.lifeCycle.status", "finalized");
//        request.addParameter("obaa.lifeCycle.contribute[0].role", "graphical_designer");
//        request.addParameter("obaa.lifeCycle.contribute[0].entity[0]", "NES");
//        request.addParameter("obaa.lifeCycle.contribute[0].date", "20/11/2013");
//        request.addParameter("obaa.rights.cost", "true");
//        request.addParameter("obaa.rights.copyright", "true");
//        request.addParameter("obaa.rights.description", "Todos os direitos autorais reservados a Nes Editora.");
//        request.addParameter("obaa.educational.interactivityType", "mixed");
//        request.addParameter("obaa.educational.intendedEndUserRole[2]", "manager");
//        request.addParameter("obaa.educational.learningResourceType[0]", "problem_statement");
//        request.addParameter("obaa.educational.interactivityLevel", "low");
//        request.addParameter("obaa.educational.semanticDensity", "high");
//        request.addParameter("obaa.educational.difficulty", "medium");
//        request.addParameter("obaa.educational.typicalAgeRanges[0]", "1 - 18 anos");
//        request.addParameter("obaa.educational.typicalLearningTime", "PT15M");
//        request.addParameter("obaa.educational.description[0]", "descricao educacional");
//        request.addParameter("obaa.educational.language[0]", "es-UY");
//        request.addParameter("obaa.educational.contexts[3]", "training");
//        request.addParameter("obaa.educational.learningContentType", "factual");
//        request.addParameter("obaa.educational.interaction.interactionType", "Objeto-sujeito");
//        request.addParameter("obaa.educational.interaction.perception", "mixed");
//        request.addParameter("obaa.educational.interaction.synchronism", "true");
//        request.addParameter("obaa.educational.interaction.coPresence", "true");
//        request.addParameter("obaa.educational.interaction.reciprocity", "1-n");
//        request.addParameter("obaa.accessibility.resourceDescription.primary.hasVisual", "true");
//        request.addParameter("obaa.accessibility.resourceDescription.primary.hasAuditory", "true");
//        request.addParameter("obaa.accessibility.resourceDescription.primary.hasText", "true");
//        request.addParameter("obaa.accessibility.resourceDescription.primary.hasTactile", "true");
//        request.addParameter("obaa.technical.format[0]", "pdf");
//        request.addParameter("obaa.technical.location[0]", "www.marcosnunes.com/5");
//        request.addParameter("obaa.technical.requirement[0].orComposite[0].type", "operatingSystem");
//        request.addParameter("obaa.technical.requirement[0].orComposite[0].name", "IOS");
//        request.addParameter("obaa.technical.requirement[0].orComposite[0].minimumVersion", "6.1");
//        request.addParameter("obaa.technical.requirement[0].orComposite[0].maximumVersion", "7.0");
//        request.addParameter("obaa.technical.requirement[0].orComposite[1].type", "browser");
//        request.addParameter("obaa.technical.requirement[0].orComposite[1].name", "any");
//        request.addParameter("obaa.technical.installationRemarks", "nnf");
//        request.addParameter("obaa.technical.otherPlatformRequirements", "internet");
//        request.addParameter("obaa.technical.duration", "15 min");
//        request.addParameter("obaa.technical.supportedPlatforms[1]", "dtv");
//        request.addParameter("obaa.technical.platformSpecificFeatures[0].specificRequirements[0].specificOrComposites[0].specificType", "tipo");
//        request.addParameter("obaa.technical.platformSpecificFeatures[0].specificRequirements[0].specificOrComposites[0].specificName", "nome");
//        request.addParameter("obaa.technical.platformSpecificFeatures[0].specificRequirements[0].specificOrComposites[0].specificMinimumVersion", "1.0");
//        request.addParameter("obaa.technical.platformSpecificFeatures[0].specificRequirements[0].specificOrComposites[0].specificMaximumVersion", "2.0");
//        request.addParameter("obaa.technical.platformSpecificFeatures[0].specificInstallationRemarks", "installationRemarks");
//        request.addParameter("obaa.technical.platformSpecificFeatures[0].specificOtherPlatformRequirements", "otherPlatformRequirements");
//
//        String result = controller.newDo(doc.getId(), request, redirectAttributes);
//        assertThat(result, equalTo("redirect:/"));
//        assertThat(redirectAttributes.getFlashAttributes().containsKey("message"), equalTo(false)); //se tiver message eh pq deu erro
//
//        File fileDest = new File("./src/test/resources/files/thumbnail");
//        BufferedImage bimgDest = ImageIO.read(fileDest);
//        assertThat(bimgDest.getWidth(), equalTo(186));
//        assertThat(bimgDest.getHeight(), equalTo(258));
//
//        long id = doc.getId();
//        em.flush();
//        em.clear();
//
//        Document docResult = docService.get(id);
//        assertThat(docResult.getObaa().getGeneral().getTitles(), hasSize(2));
//        assertThat(docResult.getObaa().getGeneral().getKeywords(), hasSize(2));
//        assertThat(docResult.getObaa().getGeneral().getIdentifiers().get(0).getCatalog(), equalTo("URI"));
//        assertThat(docResult.getObaa().getGeneral().getIdentifiers().get(0).getEntry(), equalTo("5"));
//        assertThat(docResult.getFiles(), hasSize(1));
//
//        assertThat(docResult.getObaa().getGeneral().getLanguages().get(0).toString(), equalTo("pt-BR"));
//
//        assertThat(docResult.getObaa().getGeneral().getLanguages().get(1), equalTo("en-US"));
//        assertThat(docResult.getObaa().getGeneral().getDescriptions().get(0), equalTo("descrição do objeto"));
//        assertThat(docResult.getObaa().getGeneral().getCoverages().get(0), equalTo("cobertura"));
//        assertThat(docResult.getObaa().getGeneral().getStructure().toString(), equalTo("collection"));
//        assertThat(docResult.getObaa().getGeneral().getAggregationLevel().toString(), equalTo("2"));
//        assertThat(docResult.getObaa().getLifeCycle().getVersion(), equalTo("1"));
//        assertThat(docResult.getObaa().getLifeCycle().getStatus(), equalTo("finalized"));
//        assertThat(docResult.getObaa().getLifeCycle().getContribute().get(0).getRole().toString(), equalTo("graphical_designer"));
//        assertThat(docResult.getObaa().getLifeCycle().getContribute().get(0).getEntities().get(0), equalTo("NES"));
//        assertThat(docResult.getObaa().getLifeCycle().getContribute().get(0).getDate(), equalTo("20/11/2013"));
//        assertThat(docResult.getObaa().getRights().getCost().toString(), equalTo("true"));
//        assertThat(docResult.getObaa().getRights().getCopyright().toString(), equalTo("true"));
//        assertThat(docResult.getObaa().getRights().getDescription(), equalTo("Todos os direitos autorais reservados a Nes Editora."));
//        Educational educational = docResult.getObaa().getEducational();
//        assertThat(educational.getInteractivityType(), equalTo("mixed"));
//        assertThat(educational.getIntendedEndUserRoles().get(0), equalTo("manager"));
//        assertThat(educational.getLearningResourceTypes().get(0).toString(), equalTo("problem_statement"));
//        assertThat(educational.getInteractivityLevel().toString(), equalTo("low"));
//        assertThat(educational.getSemanticDensity().toString(), equalTo("high"));
//        assertThat(educational.getDifficulty().toString(), equalTo("medium"));
//        assertThat(educational.getTypicalAgeRanges().get(0), equalTo("1 - 18 anos"));
//        assertThat(educational.getTypicalLearningTime().toString(), equalTo("PT15M"));
//        assertThat(educational.getDescription().get(0).toString(), equalTo("descricao educacional"));
//        assertThat(educational.getLanguage().get(0).toString(), equalTo("es-UY"));
//        assertThat(educational.getContexts().get(0), equalTo("training"));
//        assertThat(educational.getLearningContentType(), equalTo("factual"));
//        assertThat(educational.getInteraction().getInteractionType().toString(), equalTo("Objeto-sujeito"));
//        assertThat(educational.getInteraction().getPerception().toString(), equalTo("mixed"));
//        assertThat(educational.getInteraction().getSynchronism().toString(), equalTo("true"));
//        assertThat(educational.getInteraction().getCoPresence().toString(), equalTo("true"));
//        assertThat(educational.getInteraction().getReciprocity().toString(), equalTo("1-n"));
//        Accessibility accessibility = docResult.getObaa().getAccessibility();
//        assertThat(accessibility.getResourceDescription().getPrimary().getHasVisual(), equalTo("true"));
//        assertThat(accessibility.getResourceDescription().getPrimary().getHasAuditory(), equalTo("true"));
//        assertThat(accessibility.getResourceDescription().getPrimary().getHasText(), equalTo("true"));
//        assertThat(accessibility.getResourceDescription().getPrimary().getHasTactile(), equalTo("true"));
//        Technical technical = docResult.getObaa().getTechnical();
//        assertThat(technical.getFormat().get(0).toString(), equalTo("pdf"));
//        assertThat(technical.getLocation().get(0).toString(), equalTo("www.marcosnunes.com/5"));
//        assertThat(technical.getRequirement().get(0).getOrComposite().get(0).getType(), equalTo("operatingSystem"));
//        assertThat(technical.getRequirement().get(0).getOrComposite().get(0).getName(), equalTo("IOS"));
//        assertThat(technical.getRequirement().get(0).getOrComposite().get(0).getMinimumVersion(), equalTo("6.1"));
//        assertThat(technical.getRequirement().get(0).getOrComposite().get(0).getMaximumVersion(), equalTo("7.0"));
//        assertThat(technical.getRequirement().get(0).getOrComposite().get(1).getType(), equalTo("browser"));
//        assertThat(technical.getRequirement().get(0).getOrComposite().get(1).getName(), equalTo("any"));
//        assertThat(technical.getInstallationRemarks(), equalTo("nnf"));
//        assertThat(technical.getOtherPlatformRequirements(), equalTo("internet"));
//        assertThat(technical.getDuration(), equalTo("15 min"));
//        assertThat(technical.getSupportedPlatforms().get(0), equalTo("dtv"));
//        assertThat(technical.getPlatformSpecificFeatures().get(0).getSpecificRequirements().get(0).getSpecificOrComposites().get(0).getSpecificType(), equalTo("tipo"));
//        assertThat(technical.getPlatformSpecificFeatures().get(0).getSpecificRequirements().get(0).getSpecificOrComposites().get(0).getSpecificName(), equalTo("nome"));
//        assertThat(technical.getPlatformSpecificFeatures().get(0).getSpecificRequirements().get(0).getSpecificOrComposites().get(0).getSpecificMinimumVersion(), equalTo("1.0"));
//        assertThat(technical.getPlatformSpecificFeatures().get(0).getSpecificRequirements().get(0).getSpecificOrComposites().get(0).getSpecificMaximumVersion(), equalTo("2.0"));
//        assertThat(technical.getPlatformSpecificFeatures().get(0).getSpecificInstallationRemarks(), equalTo("installationRemarks"));
//        assertThat(technical.getPlatformSpecificFeatures().get(0).getSpecificOtherPlatformRequirements(), equalTo("otherPlatformRequirements"));
//    }

}
