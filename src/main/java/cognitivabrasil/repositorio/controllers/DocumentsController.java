package cognitivabrasil.repositorio.controllers;

import cognitivabrasil.obaa.Accessibility.Accessibility;
import cognitivabrasil.obaa.Accessibility.Primary;
import cognitivabrasil.obaa.Accessibility.ResourceDescription;
import cognitivabrasil.obaa.Educational.Educational;
import cognitivabrasil.obaa.Educational.Interaction;
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
import cognitivabrasil.repositorio.data.entities.Document;
import cognitivabrasil.repositorio.data.services.DocumentsService;
import cognitivabrasil.repositorio.data.entities.User;
import cognitivabrasil.util.Message;
import cognitivabrasil.util.VCarder;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * Controller para documentos
 *
 * @author Paulo Schreiner <paulo@jorjao81.com>
 */
@Controller("documents")
@RequestMapping("/documents")
public final class DocumentsController {

    public final String LOCAL = "/var/cognitiva/repositorio/";
    Logger log = Logger.getLogger(DocumentsController.class);
    @Autowired
    DocumentsService docService;
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

    @RequestMapping(value = "/recall", method = RequestMethod.GET)
    public String recall(Model model) {
        // TODO: getAll cannot be used if the collection is very big, have to
        // use server-side pagination

        List<Document> l = docService.getAll();

        for (Document d : l) {

            if (!d.isDeleted()) {
                OBAA metadata = d.getMetadata();
                String entry = metadata.getGeneral().getIdentifiers().get(0).getEntry();

                String location = entry.replaceAll(":8080", "");
                ArrayList<Location> locationList = new ArrayList<Location>();
                locationList.add(new Location(location));
                metadata.getTechnical().setLocation(locationList);

                d.setMetadata(metadata);

                docService.save(d);
                docService.flush();
            }

        }
        return "documents/index";
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
        if (d.isDeleted()) {
            response.sendError(410, "O documento solicitado foi deletado.");
        }
        d.getMetadata().setLocale("pt-BR");
        model.addAttribute("doc", d);
        return "documents/show";
    }

    @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
    @ResponseBody
    public Message delete(@PathVariable("id") int id, HttpServletResponse response, HttpServletRequest request) throws IOException {
        Message msg;

        try {
            Document d = docService.get(id);
            if (!isManagerForThisDocument(d, request)) {
                return new Message(Message.ERROR, "Acesso negado! Você não ter permissão para deletar este documento.");
            }
            docService.delete(d);
            docService.flush();
            msg = new Message(Message.SUCCESS, "Documento excluido com sucesso");
        } catch (DataAccessException e) {
            log.error("Não foi possivel excluir o documento.", e);
            msg = new Message(Message.ERROR, "Erro ao excluir documento");
        }
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
    public String newVersionOf(Model model, @RequestParam(required = true) Integer versionOf,
            final HttpServletRequest request) {

        //Criação de nova versão
        Document d = docService.get(versionOf);
        Document dVersion = new Document();

        //copia o original
        OBAA originalObaa = d.getMetadata();
        OBAA versionObaa = originalObaa.clone();

        //altera o id
        String versionUri = createUri(dVersion);
        Identifier versionId = new Identifier("URI", versionUri);
        versionObaa.getGeneral().getIdentifiers().clear();

        //esvaziar o location para gerar um novo
        versionObaa.getTechnical().getLocation().clear();

        //Cria relação de versão no orginial
        Relation originalRelation = new Relation();
        originalRelation.setKind("hasVersion");
        originalRelation.getResource().addIdentifier(versionId);
        originalObaa.getRelations().add(originalRelation);

        //Cria relação de versão no novo objeto
        Relation versionRelation = new Relation();
        versionRelation.setKind("isVersionOf");
        versionRelation.getResource().addIdentifier(originalObaa.getGeneral().
                getIdentifiers().get(0));
        versionObaa.getRelations().add(versionRelation);

        //abre a tela de edição de metadados da versão
        Document dv = new Document();
        dv.setMetadata(versionObaa);

        model.addAttribute("doc", dv);
        model.addAttribute("obaa", dv.getMetadata());

        return "documents/new";
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String newShow(Model model) {
        Document d = new Document();

        //o documento precisa ser salvo para gerar um id da base
        docService.save(d);
        docService.flush();

        String uri = createUri(d);

        OBAA obaa = new OBAA();

        obaa.setGeneral(new General());

        List<Identifier> identifiers = new ArrayList();
        Identifier i = new Identifier();
        i.setEntry(uri);
        i.setCatalog("URI");

        identifiers.add(i);
        obaa.getGeneral().setIdentifiers(identifiers);

        d.setObaaEntry(i.getEntry());

        d.setMetadata(obaa);
        docService.save(d);
        docService.flush();

        model.addAttribute("doc", d);
        model.addAttribute("obaa", d.getMetadata());

        return "documents/new";
    }

    @RequestMapping(value = "/new", params = "classPlan", method = RequestMethod.GET)
    public String newClassPlan(Model model) {
        Document d = new Document();

        OBAA lo = new OBAA();

        General general = new General();
        general.addLanguage("pt-BR");
        Structure s = new Structure();
        s.setText("coleção");
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
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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

        List location = new ArrayList();
        technical.setLocation(location);

        technical.setOtherPlatformRequirements("É necessário um programa como o acrobat reader que permite a leitura de arquivos no formato PDF.");
        technical.addSupportedPlatforms("Web");

        lo.setTechnical(technical);

        Educational educational = new Educational();
        educational.setInteractivityType("expositive");
        educational.addLearningResourceType("lecture");
        educational.setInteractivityLevel("veryLow");
        educational.addDescription("Plano de aula envolvendo o uso do computador ou recursos alternativos.");
        educational.addLanguage("pt-BR");
        educational.setLearningContentType("procedimental");
        educational.addContext("school");

        educational.addIntendedEndUserRole("teacher");

        Interaction interaction = new Interaction();
        interaction.setInteractionType("Objeto-sujeito");
        interaction.setCoPresence(false);
        interaction.setSynchronism(false);
        interaction.setPerception("visual");
        interaction.setReciprocity("1-n");

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

        model.addAttribute("doc", d);
        model.addAttribute("obaa", d.getMetadata());
        return "documents/new";
    }

    @RequestMapping(value = "/new", params = "hq", method = RequestMethod.GET)
    public String newHq(Model model) {
        Document d = new Document();

        OBAA lo = new OBAA();

        General general = new General();
        general.addLanguage("pt-BR");
        Structure s = new Structure();
        s.setText("atomic");
        general.setStructure(s);
        general.setAggregationLevel(1);
        general.addKeyword("quadrinhos");
        general.addKeyword("reciclagem");
        general.addKeyword("");
        lo.setGeneral(general);

        LifeCycle lifeCycle = new LifeCycle();
        lifeCycle.setVersion("1");
        lifeCycle.setStatus("finalized");

        cognitivabrasil.obaa.LifeCycle.Contribute contribute;
        contribute = new cognitivabrasil.obaa.LifeCycle.Contribute();

        VCarder autor = new VCarder();
        autor.setName("Editora", "Nes", "Nes Editora");

        contribute.addEntity(autor.getVCard());
        contribute.setRole("publisher");

        // today date
        Date date = new Date();
        DateFormat dateFormat;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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

        List location = new ArrayList();
        technical.setLocation(location);

        technical.setOtherPlatformRequirements("É necessário um programa como o Acrobat Reader que permite a leitura de arquivos no formato PDF.");
        technical.addSupportedPlatforms("Web");

        lo.setTechnical(technical);

        Educational educational = new Educational();
        educational.setInteractivityType("expositive");
        educational.addLearningResourceType("narrative_text");
        educational.setInteractivityLevel("very_low");
        educational.addLanguage("pt-BR");
        educational.addContext("school");
        educational.setDifficulty("very_easy");
        Duration dur = new Duration();
        dur.set(15, Calendar.MINUTE);
        educational.setTypicalLearningTime(dur);

        educational.addIntendedEndUserRole("student");

        Interaction interaction = new Interaction();
        interaction.setInteractionType("Objeto-sujeito");
        interaction.setCoPresence(false);
        interaction.setSynchronism(false);
        interaction.setPerception("visual");
        interaction.setReciprocity("1-1");

        educational.setInteraction(interaction);

        lo.setEducational(educational);

        Rights rights = new Rights();
        rights.setCost(false);
        rights.setCopyright(true);
        rights.setDescription("Todos os direitos autorais reservados a Nes Editora sob registro na Fundação Biblioteca Nacional e no Ministério da Cultura.");

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

        model.addAttribute("doc", d);
        model.addAttribute("obaa", d.getMetadata());
        return "documents/new";
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public String newDo(final HttpServletRequest request, @RequestParam int id) {
        Document doc = docService.get(id);
        doc.setOwner(UsersController.getCurrentUser());
        return setOBAAFiles(doc, request);
    }

    private String setOBAAFiles(Document d, final HttpServletRequest request) {
        log.debug("Trying to save");

        final MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;

        Map<String, String[]> parMap = multiRequest.getParameterMap();

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

        int size;

        if (originalTechical.getSize() == null) {
            size = 0;
        } else {
            size = Integer.valueOf(originalTechical.getSize());
        }

        //TODO: não é mais multipart
/*        for (MultipartFile file : files.values()) {
         cognitivabrasil.repositorio.data.entities.Files f = new cognitivabrasil.repositorio.data.entities.Files();

         if (file.getOriginalFilename() != null
         && !file.getOriginalFilename().isEmpty()) {

         f.setName(file.getOriginalFilename());
         f.setContentType(file.getContentType());
         f.setLocation("/var/cognitiva/repositorio/");
         f.setSize(file.getSize());
         f.setDocument(d);

         size += (int) file.getSize();

         List<String> format = t.getFormat();
                
         if (format != null) {
         if (!format.contains(file.getContentType())) {
         t.addFormat(file.getContentType());
         }
         }

         d.addFile(f);
         //                docService.save(d);
         //                docService.flush();

         try {
         log.debug("Saving " + file.getOriginalFilename());
         file.transferTo(new File(LOCAL, f.getId().toString()));
         } catch (IllegalStateException | IOException e) {
         log.error(e);
         } 
         }
         }*/

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
        dateFormat = new SimpleDateFormat("yyyy-mm-dd");
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
        d.setTimestamp(new Date());
        docService.save(d);
        docService.flush();
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
        return (UsersController.getCurrentUser().equals(d.getOwner()) || request.isUserInRole(User.MANAGE_DOC));
    }
}
