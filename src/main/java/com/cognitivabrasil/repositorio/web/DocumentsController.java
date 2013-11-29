package com.cognitivabrasil.repositorio.web;

import cognitivabrasil.obaa.Accessibility.Accessibility;
import cognitivabrasil.obaa.Accessibility.Primary;
import cognitivabrasil.obaa.Accessibility.ResourceDescription;
import cognitivabrasil.obaa.Educational.Context;
import cognitivabrasil.obaa.Educational.Educational;
import cognitivabrasil.obaa.Educational.IntendedEndUserRole;
import cognitivabrasil.obaa.Educational.Interaction;
import cognitivabrasil.obaa.Educational.InteractivityLevel;
import cognitivabrasil.obaa.Educational.LearningContentType;
import cognitivabrasil.obaa.Educational.Perception;
import cognitivabrasil.obaa.Educational.Reciprocity;
import cognitivabrasil.obaa.General.General;
import cognitivabrasil.obaa.General.Identifier;
import cognitivabrasil.obaa.General.Keyword;
import cognitivabrasil.obaa.General.Structure;
import cognitivabrasil.obaa.LifeCycle.LifeCycle;
import cognitivabrasil.obaa.Metametadata.Contribute;
import cognitivabrasil.obaa.Metametadata.MetadataSchema;
import cognitivabrasil.obaa.Metametadata.Metametadata;
import cognitivabrasil.obaa.OBAA;
import cognitivabrasil.obaa.Relation.Relation;
import cognitivabrasil.obaa.Rights.Rights;
import cognitivabrasil.obaa.Technical.*;
import com.cognitivabrasil.repositorio.data.entities.Document;
import com.cognitivabrasil.repositorio.data.entities.Files;
import com.cognitivabrasil.repositorio.data.entities.User;
import com.cognitivabrasil.repositorio.services.DocumentService;
import com.cognitivabrasil.repositorio.util.Message;
import cognitivabrasil.util.VCarder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para documentos
 *
 * @author Paulo Schreiner <paulo@jorjao81.com>
 * @author Marcos Nunes <marcos@cognitiva.com.br>
 */
@Controller("documents")
@RequestMapping("/documents")
public final class DocumentsController {

    public final String LOCAL = "/var/cognitiva/repositorio/";
    private static final Logger log = Logger.getLogger(DocumentsController.class);
    @Autowired
    DocumentService docService;
    @Autowired
    @Qualifier("serverConfig")
    Properties config;

    public DocumentsController() {
        log.info("Loaded DocumentsController");
    }

    @RequestMapping(method = RequestMethod.GET)
    public String main(Model model) {
        // TODO: getAll cannot be used if the collection is very big, have to
        // use server-side pagination
        model.addAttribute("documents", docService.getAll());
        model.addAttribute("currentUser", SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("permDocAdmin", User.MANAGE_DOC);
        model.addAttribute("permCreateDoc", User.CREATE_DOC);
        return "documents/";
    }
    
    @RequestMapping(value = "/filter/{type}", method = RequestMethod.GET)
    public String filterDiscipline(@PathVariable String type, Model model) {
        // TODO: getAll cannot be used if the collection is very big, have to
        // use server-side pagination
        model.addAttribute("documents", docService.getAll());
        model.addAttribute("currentUser", SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("permDocAdmin", User.MANAGE_DOC);
        model.addAttribute("permCreateDoc", User.CREATE_DOC);
        return "documents/";
    }

    @RequestMapping(value = "/", method = RequestMethod.DELETE)
    public String deleteAll(Model model) {
        // TODO: getAll cannot be used if the collection is very big, have to
        // use server-side pagination

        docService.deleteAll();

        model.addAttribute("documents", docService.getAll());
        return "documents/index";
    }

    /**
     * Shows the details of the repository.
     *
     * @param id the repository id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable Integer id, Model model, HttpServletResponse response)
            throws IOException {
        Document d = docService.get(id);
        if(d == null){
            response.sendError(404, "O documento solicitado não existe.");
            return "ajax";
        }else if (d.isDeleted()) {
            response.sendError(410, "O documento solicitado foi deletado.");
            return "ajax";
        }
        d.getMetadata().setLocale("pt-BR");
        model.addAttribute("doc", d);
        return "documents/show";
    }

    @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
    @ResponseBody
    public Message delete(@PathVariable("id") int id, HttpServletRequest request) throws IOException {
        Message msg;

        Document d = docService.get(id);
            if(d==null){
                return new Message(Message.ERROR, "O documento solicitado não foi encontrado.");
            }
            if (!isManagerForThisDocument(d, request)) {
                return new Message(Message.ERROR, "Acesso negado! Você não ter permissão para deletar este documento.");
            }
            docService.delete(d);
            msg = new Message(Message.SUCCESS, "Documento excluido com sucesso");
        
        return msg;
    }

    @RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
    public String edit(Model model, @PathVariable("id") Integer id,
            HttpServletResponse response, HttpServletRequest request) throws IOException {
        Document d = docService.get(id);
        if (!isManagerForThisDocument(d, request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return "ajax";
        }
        model.addAttribute("doc", d);
        model.addAttribute("obaa", d.getMetadata());
        return "documents/new";
    }

    @RequestMapping(value = "/{id}/edit", method = RequestMethod.POST)
    public String editDo(Model model, @PathVariable("id") Integer id,
            final HttpServletRequest request, HttpServletResponse response) throws IOException {
        Document d = docService.get(id);
        if (!isManagerForThisDocument(d, request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return "ajax";
        }
        return setOBAAFiles(d, request);
    }

    @RequestMapping(value = "/new", params = "versionOf", method = RequestMethod.GET)
    public String newVersionOf(Model model, @RequestParam(required = true) Integer versionOf) {

        //Criação de nova versão
        Document d = docService.get(versionOf);
        Document dv = new Document();
        //o documento precisa ser salvo para gerar um id da base
        docService.save(dv);
        //copia o original
        OBAA originalObaa = d.getMetadata();
        OBAA versionObaa = originalObaa.clone();

        //altera o id
        String versionUri = createUri(dv);
        dv.setObaaEntry(versionUri);
        docService.save(dv);
        
        Identifier versionId = new Identifier("URI", versionUri);
        versionObaa.getGeneral().getIdentifiers().clear();

        //esvaziar o location para gerar um novo
        versionObaa.getTechnical().getLocation().clear();
        
        //seta o identifier na versao
        versionObaa.getGeneral().addIdentifier(versionId);

        //Cria relação de versão no orginial
        Relation originalRelation = new Relation();
        originalRelation.setKind("hasVersion");
        originalRelation.getResource().addIdentifier(versionId);
        List relationsList = new ArrayList<>();
        relationsList.add(originalRelation);
        originalObaa.setRelations(relationsList);

        //Cria relação de versão no novo objeto
        Relation versionRelation = new Relation();
        versionRelation.setKind("isVersionOf");
        versionRelation.getResource().addIdentifier(originalObaa.getGeneral().
                getIdentifiers().get(0));
        List<Relation> relations2List = new ArrayList<>();
        relations2List.add(versionRelation);
        versionObaa.setRelations(relations2List);

        dv.setMetadata(versionObaa);
        docService.save(d);
        
        model.addAttribute("doc", dv);
        model.addAttribute("obaa", dv.getMetadata());

        return "documents/new";
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String newShow(Model model) {
        Document d = new Document();
        d.setCreated(new DateTime());
        //o documento precisa ser salvo para gerar um id da base
        docService.save(d);

        String uri = createUri(d);
        d.setObaaEntry(uri);
        docService.save(d);
        OBAA obaa = new OBAA();

        obaa.setGeneral(new General());

        List<Identifier> identifiers = new ArrayList<>();
        Identifier i = new Identifier();
        i.setEntry(uri);
        i.setCatalog("URI");

        identifiers.add(i);
        obaa.getGeneral().setIdentifiers(identifiers);

        d.setMetadata(obaa);

        model.addAttribute("doc", d);
        model.addAttribute("obaa", d.getMetadata());

        return "documents/new";
    }

    @RequestMapping(value = "/new", params = "classPlan", method = RequestMethod.GET)
    public String newClassPlan(Model model) {
        
        String result = newShow(model); //inicializa com o new basico
        
        Document d = (Document) model.asMap().get("doc");
        
//        metadados para planos de aula
        OBAA lo = d.getMetadata();
        General general = lo.getGeneral();
        general.addLanguage("pt-BR");
        Structure s = new Structure();
        s.setText("collection");
        general.setStructure(s);
        general.setAggregationLevel(3);
        general.addKeyword("Plano de Aula");
        general.addKeyword("");
        lo.setGeneral(general);

        LifeCycle lifeCycle = new LifeCycle();
        lifeCycle.setVersion("1");
        lifeCycle.setStatus("finalized");

        cognitivabrasil.obaa.LifeCycle.Contribute contribute;
        contribute = new cognitivabrasil.obaa.LifeCycle.Contribute();

        VCarder autor = new VCarder();
        autor.setName("do Brasil", "Ministério da Educação", "Ministério da Educação do Brasil");

        contribute.addEntity(autor.getVCard());
        contribute.setRole("publisher");

        // today date
        Date date = new Date();
        DateFormat dateFormat;
        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        contribute.setDate(dateFormat.format(date));
        lifeCycle.addContribute(contribute);

        lo.setLifeCycle(lifeCycle);

        Technical technical = new Technical();

        Requirement requirement = new Requirement();
        OrComposite orComposite = new OrComposite();
        orComposite.setType("operatingSystem");
        orComposite.setName("multiOs");

        OrComposite orComposite2 = new OrComposite();
        orComposite2.setType("browser");
        orComposite2.setName("any");

        requirement.addOrComposite(orComposite);
        requirement.addOrComposite(orComposite2);
        technical.addRequirement(requirement);

        List<Location> location = new ArrayList<>();
        technical.setLocation(location);

        technical.setOtherPlatformRequirements("É necessário um programa como o acrobat reader que permite a leitura de arquivos no formato PDF.");
        technical.addSupportedPlatforms(SupportedPlatform.WEB);

        lo.setTechnical(technical);

        Educational educational = new Educational();
        educational.setInteractivityType("expositive");
        educational.addLearningResourceType("lecture");
        educational.setInteractivityLevel(InteractivityLevel.VERYLOW);
        educational.addDescription("Plano de aula envolvendo o uso do computador ou recursos alternativos.");
        educational.addLanguage("pt-BR");
        educational.setLearningContentType(LearningContentType.PROCEDIMENTAL);
        educational.addContext(Context.SCHOOL);

        educational.addIntendedEndUserRole(IntendedEndUserRole.TEACHER);

        Interaction interaction = new Interaction();
        interaction.setInteractionType("Objeto-sujeito");
        interaction.setCoPresence(false);
        interaction.setSynchronism(false);
        interaction.setPerception(Perception.VISUAL);
        interaction.setReciprocity(Reciprocity.ONE_N);

        educational.setInteraction(interaction);

        lo.setEducational(educational);

        Rights rights = new Rights();
        rights.setCost(false);

        lo.setRights(rights);

        Accessibility accessibility = new Accessibility();
        ResourceDescription resourceDescription = new ResourceDescription();
        Primary primary = new Primary();
        primary.setVisual(true);
        primary.setAuditory(false);
        primary.setText(true);
        primary.setTactile(false);

        resourceDescription.setPrimary(primary);
        accessibility.setResourceDescription(resourceDescription);

        lo.setAccessibility(accessibility);

        d.setMetadata(lo);

        
        return result;
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public String newDo(final HttpServletRequest request, @RequestParam int id) {
        Document doc = docService.get(id);
        doc.setOwner(UsersController.getCurrentUser());
        return setOBAAFiles(doc, request);
    }

    private String setOBAAFiles(Document d, final HttpServletRequest request) {
        log.debug("Trying to save");

        Map<String, String[]> parMap = request.getParameterMap();

        OBAA obaa = OBAA.fromHashMap(parMap);

        // split the keywords
        List<Keyword> splittedKeywords = new ArrayList<>();
        if (obaa.getGeneral().getKeywords().size() > 0) {

            for (String k : obaa.getGeneral().getKeywords()) {
                for (String nk : k.split("\\s*[,;]\\s*")) {
                    splittedKeywords.add(new Keyword(nk));
                }
            }
            obaa.getGeneral().setKeywords(splittedKeywords);
        }


        log.debug("Title: " + obaa.getGeneral().getTitles());


        Technical t = obaa.getTechnical();

        Technical originalTechical = d.getMetadata().getTechnical();

        Long size;

        if (originalTechical.getSize() == null) {
            size = 0L;
            for (Files f : d.getFiles()) {
                size += f.getSizeInBytes();
            }
        } else {
            size = Long.valueOf(originalTechical.getSize());
        }

        t.setSize(size); // somatorio to tamanho de todos os arquivos
        obaa.setTechnical(t);
        d.setMetadata(obaa);

        if (obaa.getTechnical() == null) {
            log.warn("Technical was null");
            obaa.setTechnical(new Technical());
        }
        List<String> l = obaa.getTechnical().getLocation();

        if (l == null || l.isEmpty()) {
            obaa.getTechnical().addLocation(obaa.getGeneral().getIdentifiers().get(0).getEntry());
        } else {
            //não faz nada essa operação abaixo, getLocation devolve uma cópia
            obaa.getTechnical().getLocation().set(0, obaa.getGeneral().getIdentifiers().get(0).getEntry());
        }


        Metametadata meta = new Metametadata();

        meta.setLanguage("pt-BR");

        // logged user data
        User currentUser = UsersController.getCurrentUser();
        String userName = currentUser.getUsername();
        Contribute c = new Contribute();

        // Quando fizer o cadastro dos usuários do sistema cuidar para que possa por os dados do vcard
        VCarder grafica = new VCarder();
        grafica.setName("", userName, userName);
        c.addEntity(grafica.getVCard());
        c.setRole("creator");

        // today date
        Date date = new Date();
        DateFormat dateFormat;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        c.setDate(dateFormat.format(date));
        meta.addContribute(c);

        Identifier i = new Identifier("URI", "http://www.w3.org/2001/XMLSchema-instance");

        meta.addIdentifier(i);

        //default metadataSchema
        MetadataSchema metaSchema = new MetadataSchema();
        meta.addSchema(metaSchema);

        d.getMetadata().setMetametadata(meta);

        d.setObaaEntry(obaa.getGeneral().getIdentifiers().get(0).getEntry());

        d.setMetadata(obaa);
        docService.save(d);
        return "redirect:/documents/";
    }

    private String createUri(Document d) {

        String port = config.getProperty("Repositorio.port", "8080");
        return ("http://"
                + config.getProperty("Repositorio.hostname")
                + (port.equals("80") ? "" : (":" + port)) // if port 80, dont
                // put anything
                + config.getProperty("Repositorio.rootPath", "/repositorio")
                + "/documents/" + d.getId());
    }

    private boolean isManagerForThisDocument(Document d, HttpServletRequest request) {
        return (request.isUserInRole(User.MANAGE_DOC) || UsersController.getCurrentUser().equals(d.getOwner()));
    }

    @RequestMapping(value = "/new/generateMetadata", method = RequestMethod.POST)
    @ResponseBody
    public ObaaDto generateMetadata(int id) {
        Document doc = docService.get(id);
        return metadataFromFile(doc);
    }

    /**
     * Works only if the files are uploaded and not in a remote location
     *
     * @param d
     * @return
     */
    private ObaaDto metadataFromFile(Document d) {

        ObaaDto suggestions = new ObaaDto();
        List<Files> files = d.getFiles();

        boolean empty = false;

        /*Images*/
        boolean allImg = true;
        final String IMAGE_STARTS = "image";

        /*Applications*/
        boolean allPdf = true;
        final String PDF_MIMETYPE = "application/pdf";
        boolean allDoc = true;
        final String DOC_MIMETYPE = "application/msword";

        /*Empty verification*/
        if (files.isEmpty()) {
            empty = true;
        }

        String mime = "";
        
        for (Files file : files) {

            mime = file.getContentType();
            System.out.println("MIME Type: " + mime);

            if (!mime.startsWith(IMAGE_STARTS)) {
                allImg = false;
            }

            if (!mime.equals(PDF_MIMETYPE)) {
                allPdf = false;
            }

            if (!mime.equals(DOC_MIMETYPE)) {
                allDoc = false;
            }
            String fileName = file.getName();

            // to remove the file extension
            suggestions.setTitle(fileName.substring(0, fileName.lastIndexOf(".")));
        }

        //all image
        if (allImg && !empty) {

            //General
            suggestions.setStructure("atomic");
            suggestions.setAggregationLevel("1");

            //Educational
            suggestions.setInteractivityType("expositive");            
            suggestions.setPerception("visual");
            suggestions.setSynchronism("false");
            suggestions.setCopresense("false");
            suggestions.setReciprocity(Reciprocity.ONE_ONE);
            suggestions.setInteractivityLevel("very_low");
            suggestions.addSupportedPlatforms(SupportedPlatform.WEB);
            suggestions.addSupportedPlatforms(SupportedPlatform.MOBILE);
            suggestions.addSupportedPlatforms(SupportedPlatform.DTV);

            //Accessibility
            suggestions.setVisual("true");
            suggestions.setAuditory("false");
            suggestions.setTactil("false");
                                               
            if (mime.endsWith("jpeg")||mime.endsWith("jpg")||mime.endsWith("png")||mime.endsWith("gif")){
                System.out.println("true");
                suggestions.setRequirementsType("operatingSystem");
                suggestions.setRequirementsName("any");
            }

        }

        //all PDF
        if (allPdf && !empty) {
            System.out.println("\n*\n*\n*ALL PDF*\n*\n*\n");
        }

        //all DOC
        if (allDoc && !empty) {
            System.out.println("\n*\n*\n*ALL DOC*\n*\n*\n");
        }


        //Title Suggestion
        if (files.size() != 1) {
            suggestions.setTitle("");
        }

        return suggestions;
    }
}