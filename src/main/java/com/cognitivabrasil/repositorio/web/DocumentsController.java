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
import cognitivabrasil.obaa.LifeCycle.Entity;
import cognitivabrasil.obaa.LifeCycle.LifeCycle;
import cognitivabrasil.obaa.LifeCycle.Role;
import cognitivabrasil.obaa.LifeCycle.Status;
import cognitivabrasil.obaa.Metametadata.Contribute;
import cognitivabrasil.obaa.Metametadata.MetadataSchema;
import cognitivabrasil.obaa.Metametadata.Metametadata;
import cognitivabrasil.obaa.OBAA;
import cognitivabrasil.obaa.Relation.Kind;
import cognitivabrasil.obaa.Relation.Relation;
import cognitivabrasil.obaa.Relation.Resource;
import cognitivabrasil.obaa.Rights.Rights;
import cognitivabrasil.obaa.Technical.*;
import com.cognitivabrasil.repositorio.data.entities.Document;
import com.cognitivabrasil.repositorio.data.entities.Files;
import com.cognitivabrasil.repositorio.data.entities.User;
import com.cognitivabrasil.repositorio.services.DocumentService;
import com.cognitivabrasil.repositorio.util.Message;
import com.cognitivabrasil.repositorio.data.entities.Subject;
import com.cognitivabrasil.repositorio.services.SubjectService;
import com.cognitivabrasil.repositorio.util.Config;
import java.io.File;
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
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import static org.springframework.util.FileCopyUtils.copy;
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

    private static final Logger LOG = Logger.getLogger(DocumentsController.class);
    private static final int pageSize = 9;
    private static final int pagesToPresent = 5;
    @Autowired
    private DocumentService docService;
    @Autowired
    private SubjectService subService;
    @Autowired
    @Qualifier("serverConfig")
    private Properties config;

    public DocumentsController() {
        LOG.debug("Loaded DocumentsController");
    }

    @RequestMapping(method = RequestMethod.GET)
    public String main(Model model) {
        docService.deleteEmpty();
        return mainPage(model, 0);
    }

    @RequestMapping(value = "/page/{page}", method = RequestMethod.GET)
    public String mainPage(Model model, @PathVariable Integer page) {

        Pageable limit = new PageRequest(page, pageSize);
        Page pageResult = docService.getPage(limit);

        int divisor = pagesToPresent / 2;

        //Criando o array com as páginas a serem apresentadas
        int totalPage = pageResult.getTotalPages();
        int sobraDePaginasDireita = 0;
        int sobraDePaginasEsquerda = 0;
        List<Integer> pagesAvaliable = new ArrayList<>();
        pagesAvaliable.add(page);
        for (int i = 1; i <= divisor; i++) {
            //Teste de sobras na esquerda
            if ((page - i) >= 0) {
                pagesAvaliable.add(page - i);
            } else {
                sobraDePaginasEsquerda++;
            }
            //Teste de sobras na direita
            if ((page + i) < totalPage) {
                pagesAvaliable.add(page + i);
            } else {
                sobraDePaginasDireita++;
            }
        }
        if (sobraDePaginasEsquerda == 0 || sobraDePaginasDireita == 0) {
            int i;
            for (i = 1; i <= sobraDePaginasDireita; i++) {
                if (page - divisor - i >= 0) {
                    pagesAvaliable.add(page - divisor - i);
                }
            }
            sobraDePaginasDireita = sobraDePaginasDireita - i + 1;
            for (i = 1; i <= sobraDePaginasEsquerda; i++) {
                if (page + divisor + i < totalPage) {
                    pagesAvaliable.add(page + divisor + i);
                }
            }
            sobraDePaginasEsquerda = sobraDePaginasEsquerda - i + 1;
        }
        Collections.sort(pagesAvaliable);
//        LOG.debug("Sobra de paginas esquerda: "+sobraDePaginasEsquerda+" direita: "+sobraDePaginasDireita+" pagina: "+page);

        model.addAttribute("documents", pageResult);
        model.addAttribute("pages", pagesAvaliable);
        model.addAttribute("currentUser", SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("permDocAdmin", User.MANAGE_DOC);
        model.addAttribute("permCreateDoc", User.CREATE_DOC);
        return "documents/";
    }

    @RequestMapping(value = "/filter/{subject}", method = RequestMethod.GET)
    public String filterDiscipline(@PathVariable String subject, Model model) {
        return filterDisciplinePage(subject, model, 0);
    }

    @RequestMapping(value = "/filter/{subject}/page/{page}", method = RequestMethod.GET)
    public String filterDisciplinePage(@PathVariable String subject, Model model, @PathVariable Integer page) {

        Pageable limit = new PageRequest(page, pageSize);
        Subject s = subService.getSubjectByName(subject);
        Page pageResult = docService.getPageBySubject(s, limit);

        LOG.debug("pagina " + page);

        int divisor = pagesToPresent / 2;

        //Criando o array com as páginas a serem apresentadas
        int totalPage = pageResult.getTotalPages();
        int sobraDePaginasDireita = 0;
        int sobraDePaginasEsquerda = 0;
        List<Integer> pagesAvaliable = new ArrayList<>();
        pagesAvaliable.add(page);
        for (int i = 1; i <= divisor; i++) {
            //Teste de sobras na esquerda
            if ((page - i) >= 0) {
                pagesAvaliable.add(page - i);
            } else {
                sobraDePaginasEsquerda++;
            }
            //Teste de sobras na direita
            if ((page + i) < totalPage) {
                pagesAvaliable.add(page + i);
            } else {
                sobraDePaginasDireita++;
            }
        }
        if (sobraDePaginasEsquerda == 0 || sobraDePaginasDireita == 0) {
            int i;
            for (i = 1; i <= sobraDePaginasDireita; i++) {
                if (page - divisor - i >= 0) {
                    pagesAvaliable.add(page - divisor - i);
                }
            }
            sobraDePaginasDireita = sobraDePaginasDireita - i + 1;
            for (i = 1; i <= sobraDePaginasEsquerda; i++) {
                if (page + divisor + i < totalPage) {
                    pagesAvaliable.add(page + divisor + i);
                }
            }
            //TODO: ISSO AQUI ESTÁ FORA DO FOR E NO FIM DO IF E NÃO É USADO PARA NADA (MARCOS).
            sobraDePaginasEsquerda = sobraDePaginasEsquerda - i + 1;
        }
        Collections.sort(pagesAvaliable);

        model.addAttribute("documents", pageResult);
        model.addAttribute("pages", pagesAvaliable);
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

        //mostrar a relação para o usuário
        if (d.getMetadata() != null && !d.getMetadata().getRelations().isEmpty()) {
            for (Relation rel : d.getMetadata().getRelations()) {
                switch (rel.getKind().getText()) {
                    case Kind.IS_VERSION_OF:
                        if (!rel.getResource().getIdentifier().isEmpty()) {
                            model.addAttribute("isversion", rel.getResource().getIdentifier().get(0).getEntry());
                        }
                        break;
                    case Kind.HAS_VERSION:
                        if (!rel.getResource().getIdentifier().isEmpty()) {
                            model.addAttribute("hasversion", rel.getResource().getIdentifier().get(0).getEntry());
                        }
                        break;
                }
            }
        }
        return "documents/show";
    }

    @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST)
    @ResponseBody
    public Message delete(@PathVariable("id") int id, HttpServletRequest request) {
        Message msg;

        LOG.info("Deletando o objeto: " + id);
        try {
            Document d = docService.get(id);
            if (d == null) {
                return new Message(Message.ERROR, "O documento solicitado não foi encontrado.");
            }
            if (d.isDeleted()) {
                return new Message(Message.ERROR, "O documento solicitado já foi deletado.");
            }
            if (!isManagerForThisDocument(d, request)) {
                return new Message(Message.ERROR, "Acesso negado! Você não ter permissão para deletar este documento.");
            }
            docService.delete(d);
            msg = new Message(Message.SUCCESS, "Documento excluido com sucesso");
        } catch (IOException io) {
            msg = new Message(Message.SUCCESS, "Documento excluido com sucesso, mas os seus arquivos não foram encontrados", "");
        } catch (DataAccessException e) {
            LOG.error("Não foi possivel excluir o documento.", e);
            msg = new Message(Message.ERROR, "Erro ao excluir o documento.", "");
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
        setOBAAFiles(d, request);

        return ("redirect:/documents/");
    }

    @RequestMapping(value = "/new", params = "versionOf", method = RequestMethod.GET)
    public String newVersionOf(Model model, @RequestParam(required = true) Integer versionOf) {

        //Criação de nova versão
        Document d = docService.get(versionOf);
        Document dv = new Document();
        dv.setCreated(new DateTime());
        //o documento precisa ser salvo para gerar um id da base
        docService.save(dv);
        //copia o original
        OBAA originalObaa = d.getMetadata();
        OBAA versionObaa = originalObaa.clone();

        //altera o id
        String versionUri = createUri(dv);
        dv.setObaaEntry(versionUri);

        Identifier versionId = new Identifier("URI", versionUri);
        versionObaa.getGeneral().getIdentifiers().clear();

        //esvaziar o location para gerar um novo
        versionObaa.getTechnical().getLocation().clear();

        //seta o identifier na versao
        versionObaa.getGeneral().addIdentifier(versionId);

        //Cria relação de versão no novo objeto
        Relation versionRelation = new Relation();
        versionRelation.setKind(Kind.IS_VERSION_OF);
        versionRelation.setResource(new Resource());
        versionRelation.getResource().addIdentifier(originalObaa.getGeneral().
                getIdentifiers().get(0));
        List<Relation> relations2List = new ArrayList<>();
        relations2List.add(versionRelation);
        versionObaa.setRelations(relations2List);

        docService.save(d);

        dv.setMetadata(versionObaa);

        model.addAttribute("doc", dv);
        model.addAttribute("obaa", dv.getMetadata());

        return "documents/new";
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String newShow(Model model) {

        Document d = new Document();
        d.setCreated(new DateTime());
        d.setOwner(UsersController.getCurrentUser());
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

        //inicializa com o new basico
        String result = newShow(model);

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

        Entity e = new Entity();

        e.setName("Ministério da Educação", "do Brasil");

        contribute.addEntity(e);
        contribute.setRole(Role.PUBLISHER);

        // today date
        Date date = new Date();
        DateFormat dateFormat;
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
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
        LOG.debug("Trying to save");

        Subject s;

        if (d != null && d.getMetadata() != null && d.getMetadata().getGeneral() != null) {
            List<String> keysObaa = d.getMetadata().getGeneral().getKeywords();
            List<Subject> allSubjects = subService.getAll();
            String nameSubject = "";
            for (String key : keysObaa) {
                if (allSubjects.contains(retiraAcentos(key).toLowerCase())) {
                    nameSubject = retiraAcentos(key).toLowerCase();
                }
            }
            LOG.trace("Assunto do OA: " + nameSubject);
            if (!nameSubject.equals("")) {
                s = subService.getSubjectByName(nameSubject);
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

        LOG.debug("Title: " + obaa.getGeneral().getTitles());

        Technical t = obaa.getTechnical();

        Long size;

        size = 0L;
        for (Files f : d.getFiles()) {
            size += f.getSizeInBytes();
        }

        // somatorio to tamanho de todos os arquivos
        t.setSize(size);
        obaa.setTechnical(t);
        d.setMetadata(obaa);

        if (obaa.getTechnical() == null) {
            LOG.warn("Technical was null");
            obaa.setTechnical(new Technical());
        }
        List<Location> l = obaa.getTechnical().getLocation();

        //if doesn't have location, an entry based is created
        if (l == null || l.isEmpty()) {
            obaa.getTechnical().addLocation(obaa.getGeneral().getIdentifiers().get(0).getEntry());
        }

        // Preenchimento dos metametadados
        Metametadata meta = new Metametadata();

        meta.setLanguage("pt-BR");

        // logged user data
        User currentUser = UsersController.getCurrentUser();
        String userName = currentUser.getUsername();
        Contribute c = new Contribute();

        // Quando fizer o cadastro dos usuários do sistema cuidar para que possa por os dados do vcard        
        Entity e = new Entity();
        e.setName(userName, "");

        c.addEntity(e);
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

        //Parsing do duration
        d.setObaaEntry(obaa.getGeneral().getIdentifiers().get(0).getEntry());

        //Se o documento tem uma relação is_version_of, é testado se o outro 
        //documento tem o Has_version, se não tiver a relação é criada.
        for (Identifier id : obaa.getRelationsWithKind(Kind.IS_VERSION_OF)) {
            if (id.getCatalog().equalsIgnoreCase("URI")) {
                Document docVersion = docService.get(id.getEntry());
                if (docVersion.getMetadata() != null) {
                    //testa se o documento ja tem tem a versao cadastrada
                    if (!docVersion.getMetadata().hasRelationWith(Kind.HAS_VERSION, d.getObaaEntry())) {
                        //Cria relação de versão no orginial
                        Relation originalRelation = new Relation();
                        originalRelation.setKind(Kind.HAS_VERSION);
                        originalRelation.setResource(new Resource());
                        originalRelation.getResource().addIdentifier(new Identifier("URI", d.getObaaEntry()));
                        List<Relation> relationsList = new ArrayList<>();
                        relationsList.add(originalRelation);
                        docVersion.getMetadata().setRelations(relationsList);
                        //salva o documento com a nova relaçao
                        docService.save(docVersion);
                    }
                }
            }
        }

        d.setMetadata(obaa);
        d.setActive(true);
        docService.save(d);
    }

    private String createUri(Document d) {

        String port = config.getProperty("Repositorio.port", "8080");
        return ("http://"
                + config.getProperty("Repositorio.hostname")
                + (port.equals("80") ? "" : (":" + port))
                // if port 80, dont put anything
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
            LOG.debug("MIME Type: " + mime);

            if (!mime.startsWith(IMAGE)) {
                allImg = false;
            }
            if (!mime.equals(PDF_MIMETYPE)) {
                allPdf = false;
            }
            if (!mime.equals(DOC_MIMETYPE)) {
                allDoc = false;
            }

        }

        if (allImg && !empty) {
            //all image
            suggestions = this.allImg(mime);

        } else if (allPdf && !empty) {
            //all PDF
            suggestions = this.allPdf();

        } else if (allDoc && !empty) {
            //all DOC
            suggestions = this.allDoc();
        }

        //Title Suggestion
        if (files.size() != 1 || files.size() >= 2) {
            suggestions.setTitle("");
        } else {
            String fileName = files.get(0).getName().replaceAll("_", " ");

            // to remove the file extension
            if (fileName.contains(".")) {
                suggestions.setTitle(fileName.substring(0, fileName.lastIndexOf('.')));
            } else {
                suggestions.setTitle(fileName);
            }
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

    /**
     * Criado apenas para salvar em um diretório todos os objetos da base com o
     * seu respectivo metadado.
     *
     * @return
     * @throws IOException
     * @throws Exception
     */
//    @RequestMapping(value = "/recallfiles", method = RequestMethod.GET)
//    @ResponseBody
    public String recallFiles()
            throws IOException {
        String location = Config.FILE_PATH + "old/";

        List<Document> docs = docService.getAll();

        for (Document doc : docs) {
            LOG.trace("\n doc " + doc.getId());

            String destinationPath = Config.FILE_PATH + doc.getId();
            File destinationDocFiles = new File(destinationPath);
            destinationDocFiles.mkdir();

            List<Files> files = doc.getFiles();
            int numberFiles = 0;
            for (Files f : files) {
                if (f.getLocation().isEmpty()) {
                    throw new IOException("A localização do documento está em branco.");
                }
                File sourceFile = new File(location + f.getId());
                File destinationFile = new File(destinationPath + "/" + f.getName());

                copy(sourceFile, destinationFile);
                numberFiles++;
            }
            if (numberFiles == 0) {
                throw new IOException("O Documento " + doc.getId() + " não possui nenhum arquivo!");
            }
        }
        return "ok";
    }

    /**
     * Esses métodos foram feitos privado e aqui e não na classe ObaaDto, por
     * ela ser uma classe apenas para tranferência de dados.
     */
    private ObaaDto allImg(String mime) {
        ObaaDto imgObj = new ObaaDto();

        LOG.debug("All Image");

        //General
        imgObj.setStructure(Structure.ATOMIC);
        imgObj.setAggregationLevel("1");

        //Educational
        imgObj.setInteractivityType(InteractivityType.EXPOSITIVE);
        imgObj.setPerception(Perception.VISUAL);
        imgObj.setSynchronism("false");
        imgObj.setCopresense("false");
        imgObj.setReciprocity(Reciprocity.ONE_ONE);
        imgObj.setInteractivityLevel(InteractivityLevel.VERY_LOW);

        //Accessibility
        imgObj.setVisual("true");
        imgObj.setAuditory("false");
        imgObj.setTactil("false");
        imgObj.setTextual("false");

        //Technical
        imgObj.addSupportedPlatforms(SupportedPlatform.WEB);
        imgObj.addSupportedPlatforms(SupportedPlatform.MOBILE);
        imgObj.addSupportedPlatforms(SupportedPlatform.DTV);

        if (mime.endsWith("jpeg") || mime.endsWith("jpg") || mime.endsWith("png") || mime.endsWith("gif")) {

            imgObj.setRequirementsType(Type.OPERATING_SYSTEM);
            imgObj.setRequirementsName(Name.ANY);
        }
        return imgObj;
    }

    private ObaaDto allPdf() {
        ObaaDto pdfObj = new ObaaDto();

        LOG.debug("All PDF");

        //General
        pdfObj.setStructure(Structure.ATOMIC);
        pdfObj.setAggregationLevel("1");

        //Educational
        pdfObj.setInteractivityType(InteractivityType.EXPOSITIVE);
        pdfObj.setPerception(Perception.VISUAL);
        pdfObj.setSynchronism("false");
        pdfObj.setCopresense("false");
        pdfObj.setReciprocity(Reciprocity.ONE_ONE);
        pdfObj.setInteractivityLevel(InteractivityLevel.VERY_LOW);

        //Accessibility
        pdfObj.setVisual("true");
        pdfObj.setAuditory("false");
        pdfObj.setTactil("false");
        pdfObj.setTextual("true");

        //Technical
        pdfObj.addSupportedPlatforms(SupportedPlatform.WEB);
        pdfObj.addSupportedPlatforms(SupportedPlatform.MOBILE);

        pdfObj.setOtherPlatformRequirements("É necessário um programa como o Adobe Reader para ver esse arquivo.");
        pdfObj.setRequirementsType(Type.OPERATING_SYSTEM);
        pdfObj.setRequirementsName(Name.ANY);

        return pdfObj;
    }

    private ObaaDto allDoc() {
        ObaaDto docObj = new ObaaDto();

        LOG.debug("All Doc");

        //General
        docObj.setStructure(Structure.ATOMIC);
        docObj.setAggregationLevel("1");

        //Educational
        docObj.setInteractivityType(InteractivityType.EXPOSITIVE);
        docObj.setPerception(Perception.VISUAL);
        docObj.setSynchronism("false");
        docObj.setCopresense("false");
        docObj.setReciprocity(Reciprocity.ONE_ONE);
        docObj.setInteractivityLevel(InteractivityLevel.VERY_LOW);

        //Accessibility
        docObj.setVisual("true");
        docObj.setAuditory("false");
        docObj.setTactil("false");
        docObj.setTextual("true");

        //Technical
        docObj.addSupportedPlatforms(SupportedPlatform.WEB);
        docObj.addSupportedPlatforms(SupportedPlatform.MOBILE);

        docObj.setOtherPlatformRequirements("É necessário um programa como o Microsoft Word para ver esse arquivo.");
        docObj.setRequirementsType(Type.OPERATING_SYSTEM);
        docObj.setRequirementsName(Name.ANY);

        return docObj;
    }
}
