package com.cognitivabrasil.repositorio.web;

import cognitivabrasil.obaa.Accessibility.Accessibility;
import cognitivabrasil.obaa.Accessibility.Primary;
import cognitivabrasil.obaa.Accessibility.ResourceDescription;
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
import cognitivabrasil.obaa.General.General;
import cognitivabrasil.obaa.General.Identifier;
import cognitivabrasil.obaa.General.Keyword;
import cognitivabrasil.obaa.General.Structure;
import cognitivabrasil.obaa.LifeCycle.LifeCycle;
import cognitivabrasil.obaa.LifeCycle.Role;
import cognitivabrasil.obaa.LifeCycle.Status;
import cognitivabrasil.obaa.Metametadata.Contribute;
import cognitivabrasil.obaa.Metametadata.MetadataSchema;
import cognitivabrasil.obaa.Metametadata.Metametadata;
import cognitivabrasil.obaa.OBAA;
import cognitivabrasil.obaa.Relation.Kind;
import cognitivabrasil.obaa.Relation.Relation;
import cognitivabrasil.obaa.Rights.Rights;
import cognitivabrasil.obaa.Technical.*;
import com.cognitivabrasil.repositorio.data.entities.Document;
import com.cognitivabrasil.repositorio.data.entities.Files;
import com.cognitivabrasil.repositorio.data.entities.User;
import com.cognitivabrasil.repositorio.services.DocumentService;
import com.cognitivabrasil.repositorio.util.Message;
import cognitivabrasil.util.VCarder;
import com.cognitivabrasil.repositorio.data.entities.Subject;
import com.cognitivabrasil.repositorio.services.SubjectService;
import java.io.IOException;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para documentos
 *
 * @author Paulo Schreiner <paulo@cognitivabrasil.com.br>
 * @author Marcos Nunes <marcos@cognitivabrasil.com.br>
 */
@Controller("documents")
@RequestMapping("/documents")
public final class DocumentsController {

    public final String LOCAL = "/var/cognitiva/repositorio/";
    private static final Logger log = Logger.getLogger(DocumentsController.class);
    @Autowired
    DocumentService docService;
    @Autowired
    SubjectService subService;
    @Autowired
    @Qualifier("serverConfig")
    Properties config;

    public DocumentsController() {
        log.debug("Loaded DocumentsController");
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

    @RequestMapping(value = "/filter/{subject}", method = RequestMethod.GET)
    public String filterDiscipline(@PathVariable String subject, Model model) {
        // TODO: getAll Documents by the specific subject name
        Subject s = subService.getSubjectByName(subject);
        model.addAttribute("documents", docService.getBySubject(s));
        model.addAttribute("currentUser", SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("permDocAdmin", User.MANAGE_DOC);
        model.addAttribute("permCreateDoc", User.CREATE_DOC);
        return "documents/";
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
        if (d == null) {
            response.sendError(404, "O documento solicitado não existe.");
            return "ajax";
        } else if (d.isDeleted()) {
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
        if (d == null) {
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
        setOBAAFiles(d, request);

        return ("redirect:/documents/");
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
        originalRelation.setKind(Kind.HAS_VERSION);
        originalRelation.getResource().addIdentifier(versionId);
        List<Relation> relationsList = new ArrayList<>();
        relationsList.add(originalRelation);
        originalObaa.setRelations(relationsList);

        //Cria relação de versão no novo objeto
        Relation versionRelation = new Relation();
        versionRelation.setKind(Kind.IS_VERSION_OF);
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
        s.setText(Structure.COLLECTION);
        general.setStructure(s);
        general.setAggregationLevel(3);
        general.addKeyword("Plano de Aula");
        general.addKeyword("");
        lo.setGeneral(general);

        LifeCycle lifeCycle = new LifeCycle();
        lifeCycle.setVersion("1");
        lifeCycle.setStatus(Status.FINALIZED);

        cognitivabrasil.obaa.LifeCycle.Contribute contribute;
        contribute = new cognitivabrasil.obaa.LifeCycle.Contribute();

        VCarder autor = new VCarder();
        autor.setName("do Brasil", "Ministério da Educação", "Ministério da Educação do Brasil");

        contribute.addEntity(autor.getVCard());
        contribute.setRole(Role.PUBLISHER);

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
        orComposite.setType(Type.OPERATING_SYSTEM);
        orComposite.setName(Name.MULTI_OS);

        OrComposite orComposite2 = new OrComposite();
        orComposite2.setType(Type.BROWSER);
        orComposite2.setName(Name.ANY);

        requirement.addOrComposite(orComposite);
        requirement.addOrComposite(orComposite2);
        technical.addRequirement(requirement);

        List<Location> location = new ArrayList<>();
        technical.setLocation(location);

        technical.setOtherPlatformRequirements("É necessário um programa como o acrobat reader que permite a leitura de arquivos no formato PDF.");
        technical.addSupportedPlatforms(SupportedPlatform.WEB);

        lo.setTechnical(technical);

        Educational educational = new Educational();
        educational.setInteractivityType(InteractivityType.EXPOSITIVE);
        educational.addLearningResourceType(LearningResourceType.LECTURE);
        educational.setInteractivityLevel(InteractivityLevel.VERY_LOW);
        educational.addDescription("Plano de aula envolvendo o uso do computador ou recursos alternativos.");
        educational.addLanguage("pt-BR");
        educational.setLearningContentType(LearningContentType.PROCEDIMENTAL);
        educational.addContext(Context.SCHOOL);

        educational.addIntendedEndUserRole(IntendedEndUserRole.TEACHER);

        Interaction interaction = new Interaction();
        interaction.setInteractionType(InteractionType.OBJECT_INDIVIDUAL);
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
        setOBAAFiles(doc, request);

        return ("redirect:/documents/");
    }

    private void setOBAAFiles(Document d, final HttpServletRequest request) {
        log.debug("Trying to save");
        
        Subject s;
        
        if (d != null && d.getMetadata() != null && d.getMetadata().getGeneral() != null) {
            List<String> keysObaa = d.getMetadata().getGeneral().getKeywords();
            List<Subject> allSubjects = subService.getAll();
            String NameSubject = "";
            for (String key : keysObaa) {
                if (allSubjects.contains(retiraAcentos(key).toLowerCase())) {
                    NameSubject = retiraAcentos(key).toLowerCase();
                }
            }
            System.out.println("\n\n"+NameSubject+"\n\n");
            if(!NameSubject.equals("")){
                s = subService.getSubjectByName(NameSubject);
                d.setSubject(s);
            }
        }
        
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

        //TODO: isso tem que sair daqui e não precisa mais do else
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
        c.setRole(Role.AUTHOR);

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
        final String IMAGE = "image";

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
            log.debug("MIME Type: " + mime);

            if (!mime.startsWith(IMAGE)) {
                allImg = false;
            } else if (!mime.equals(PDF_MIMETYPE)) {
                allPdf = false;
            } else if (!mime.equals(DOC_MIMETYPE)) {
                allDoc = false;
            }

            String fileName = file.getName();

            // to remove the file extension
            if (fileName.contains(".")) {
                suggestions.setTitle(fileName.substring(0, fileName.lastIndexOf(".")));
            } else {
                suggestions.setTitle(fileName);
            }
        }

        //all image
        if (allImg && !empty) {

            //General
            suggestions.setStructure(Structure.ATOMIC);
            suggestions.setAggregationLevel("1");

            //Educational
            suggestions.setInteractivityType(InteractivityType.EXPOSITIVE);
            suggestions.setPerception(Perception.VISUAL);
            suggestions.setSynchronism("false");
            suggestions.setCopresense("false");
            suggestions.setReciprocity(Reciprocity.ONE_ONE);
            suggestions.setInteractivityLevel(InteractivityLevel.VERY_LOW);

            //Accessibility
            suggestions.setVisual("true");
            suggestions.setAuditory("false");
            suggestions.setTactil("false");

            //Technical
            suggestions.addSupportedPlatforms(SupportedPlatform.WEB);
            suggestions.addSupportedPlatforms(SupportedPlatform.MOBILE);
            suggestions.addSupportedPlatforms(SupportedPlatform.DTV);

            if (mime.endsWith("jpeg") || mime.endsWith("jpg") || mime.endsWith("png") || mime.endsWith("gif")) {

                suggestions.setRequirementsType(Type.OPERATING_SYSTEM);
                suggestions.setRequirementsName(Name.ANY);
            }

        } else if (allPdf && !empty) { //all PDF
            //General
            suggestions.setStructure(Structure.ATOMIC);
            suggestions.setAggregationLevel("1");

            //Educational
            suggestions.setInteractivityType(InteractivityType.EXPOSITIVE);
            suggestions.setPerception(Perception.VISUAL);
            suggestions.setSynchronism("false");
            suggestions.setCopresense("false");
            suggestions.setReciprocity(Reciprocity.ONE_ONE);
            suggestions.setInteractivityLevel(InteractivityLevel.VERY_LOW);
            suggestions.addSupportedPlatforms(SupportedPlatform.WEB);
            suggestions.addSupportedPlatforms(SupportedPlatform.MOBILE);
            suggestions.addSupportedPlatforms(SupportedPlatform.DTV);

            //Accessibility
            suggestions.setVisual("true");
            suggestions.setAuditory("false");
            suggestions.setTactil("false");
            suggestions.setTextual("true");

            //Technical
            suggestions.addSupportedPlatforms(SupportedPlatform.WEB);
            suggestions.addSupportedPlatforms(SupportedPlatform.MOBILE);

            suggestions.setOtherPlatformRequirements("É necessário um programa como o Adobe Reader para ver esse arquivo.");
            suggestions.setRequirementsType(Type.OPERATING_SYSTEM);
            suggestions.setRequirementsName(Name.ANY);

        } else if (allDoc && !empty) { //all DOC
            //General
            suggestions.setStructure(Structure.ATOMIC);
            suggestions.setAggregationLevel("1");

            //Educational
            suggestions.setInteractivityType(InteractivityType.EXPOSITIVE);
            suggestions.setPerception(Perception.VISUAL);
            suggestions.setSynchronism("false");
            suggestions.setCopresense("false");
            suggestions.setReciprocity(Reciprocity.ONE_ONE);
            suggestions.setInteractivityLevel(InteractivityLevel.VERY_LOW);
            suggestions.addSupportedPlatforms(SupportedPlatform.WEB);
            suggestions.addSupportedPlatforms(SupportedPlatform.MOBILE);
            suggestions.addSupportedPlatforms(SupportedPlatform.DTV);

            //Accessibility
            suggestions.setVisual("true");
            suggestions.setAuditory("false");
            suggestions.setTactil("false");
            suggestions.setTextual("true");

            //Technical
            suggestions.addSupportedPlatforms(SupportedPlatform.WEB);
            suggestions.addSupportedPlatforms(SupportedPlatform.MOBILE);

            suggestions.setOtherPlatformRequirements("É necessário um programa como o Microsoft Word para ver esse arquivo.");
            suggestions.setRequirementsType(Type.OPERATING_SYSTEM);
            suggestions.setRequirementsName(Name.ANY);
        }

        //Title Suggestion
        if (files.size() != 1) {
            suggestions.setTitle("");
        }

        return suggestions;
    }

    /**
     *
     * Return s without accent mark
     *
     * @param s
     * @return
     */
    private String retiraAcentos(String s) {

        String output = Normalizer.normalize(s, Normalizer.Form.NFD);
        output = output.replaceAll("[^\\p{ASCII}]", "");
        return output;

    }
}
