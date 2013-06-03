import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.omg.CORBA.NameValuePair;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequestSettings;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * 
 */

/**
 * @author paulo
 * 
 */
public class CreationTest {

	static WebClient webClient;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		webClient = new WebClient(BrowserVersion.FIREFOX_3_6);

		// delete all documents
		login();

		WebRequestSettings requestSettings = new WebRequestSettings(new URL(
				"http://localhost:9090/repositorio/documents/"),
				HttpMethod.DELETE);

		// Then we set the request parameters
		requestSettings.setRequestParameters(new ArrayList());

		// Finally, we can get the page
		HtmlPage page = webClient.getPage(requestSettings);

		assertThat(page.asText(), containsString("Nenhum documento cadastrado"));

	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	private static void login() throws FailingHttpStatusCodeException,
			MalformedURLException, IOException {
		// Get the first page
		final HtmlPage page1 = webClient
				.getPage("http://localhost:9090/repositorio/login.html");

		// Get the form that we are dealing with and within that form,
		// find the submit button and the field that we want to change.
		final HtmlForm form = page1.getFormByName("login");
		final HtmlTextInput username = form.getInputByName("j_username");
		final HtmlPasswordInput password = form.getInputByName("j_password");

		username.setText("admin");
		password.setText("teste");

		final HtmlSubmitInput button = form.getInputByName("submeter");

		// Now submit the form by clicking the button and get back the second
		// page.
		final HtmlPage page2 = button.click();
		assertThat(page2.asText(), containsString("Lista de documentos"));
	}

	@Test
	public void testLoginFail() throws Exception {

		// Get the first page
		final HtmlPage page1 = webClient
				.getPage("http://localhost:9090/repositorio/login.html");

		// Get the form that we are dealing with and within that form,
		// find the submit button and the field that we want to change.
		final HtmlForm form = page1.getFormByName("login");
		final HtmlTextInput username = form.getInputByName("j_username");
		final HtmlPasswordInput password = form.getInputByName("j_password");

		username.setText("admin");
		password.setText("errado");

		final HtmlSubmitInput button = form.getInputByName("submeter");

		// Now submit the form by clicking the button and get back the second
		// page.
		final HtmlPage page2 = button.click();
		assertThat(page2.asText(), containsString("Usuário ou senha incorreto"));

		webClient.closeAllWindows();
	}

	@Test
	public void testLoginSuccess() throws Exception {
		login();

		webClient.closeAllWindows();
	}

	@Test
	public void newDocumentSimple() throws FailingHttpStatusCodeException,
			MalformedURLException, IOException {
		login();
		final HtmlPage newDoc = webClient
				.getPage("http://localhost:9090/repositorio/documents/new");

		final HtmlForm form = (HtmlForm) newDoc.getElementById("newobject");

		final HtmlTextInput title0 = form
				.getInputByName("obaa.general.titles[0]");

		title0.setText("Título de teste");

		final HtmlTextInput language0 = form
				.getInputByName("obaa.general.languages[0]");
		language0.setText("pt_BR");

		HtmlSubmitInput button = (HtmlSubmitInput) form
				.getElementById("submitButton");

		final HtmlPage page2 = button.click();
		assertThat(page2.asText(), containsString("Título de teste"));

		final HtmlPage page3 = page2.getAnchorByText("Título de teste").click();

		assertThat(page3.asText(), containsString("Título: Título de teste"));

	}
	
	@Test
	public void splitKeywords() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		login();
		
		final HtmlPage newDoc = webClient
				.getPage("http://localhost:9090/repositorio/documents/new");

		final HtmlForm form = (HtmlForm) newDoc.getElementById("newobject");

		final HtmlTextInput title0 = form
				.getInputByName("obaa.general.titles[0]");

		title0.setText("testSplitKeywords");
		
		((HtmlTextInput)form.getInputByName("obaa.general.keywords[0]")).setText("Objetos de aprendizagem, Sociologia, Informática");
		
		HtmlSubmitInput button = (HtmlSubmitInput) form
				.getElementById("submitButton");

		final HtmlPage page2 = button.click();
		assertThat(page2.asText(), containsString("testSplitKeywords"));

		final HtmlPage page3 = page2.getAnchorByText("testSplitKeywords").click();

		assertThat(page3.asText(), containsString("Título: testSplitKeywords"));
		assertThat(page3.asText(), containsString("Palavra-chave: Objetos de aprendizagem"));
		assertThat(page3.asText(), containsString("Palavra-chave: Sociologia"));
		assertThat(page3.asText(), containsString("Palavra-chave: Informática"));
	
	}
	
	@Test
	public void newDocumentComplete() throws FailingHttpStatusCodeException,
			MalformedURLException, IOException {
		login();
		final HtmlPage newDoc = webClient
				.getPage("http://localhost:9090/repositorio/documents/new");

		final HtmlForm form = (HtmlForm) newDoc.getElementById("newobject");

		final HtmlTextInput title0 = form
				.getInputByName("obaa.general.titles[0]");

		title0.setText("newDocumentComplete");
		
		((HtmlSelect)form.getSelectByName("obaa.general.structure")).setSelectedAttribute("atomic", true);
		((HtmlTextInput)form.getInputByName("obaa.educational.language[0]")).setText("en");



		final HtmlTextInput language0 = form
				.getInputByName("obaa.general.languages[0]");
		language0.setText("pt_BR");
		
		((HtmlTextInput)form.getInputByName("obaa.lifeCycle.version")).setText("1.0");
		((HtmlSelect)form.getSelectByName("obaa.lifeCycle.status")).setSelectedAttribute("draft", true);
		((HtmlSelect)form.getSelectByName("obaa.lifeCycle.contribute[0].role")).setSelectedAttribute("author", true);
		((HtmlTextInput)form.getInputByName("obaa.lifeCycle.contribute[0].entity[0]")).setText("Julia");
		
		newDoc.getAnchorByName("addEntity").click();
		((HtmlTextInput)form.getInputByName("obaa.lifeCycle.contribute[0].entity[1]")).setText("Ana Paula Scariot");
		((HtmlTextInput)form.getInputByName("obaa.lifeCycle.contribute[0].date")).setText("30-04-2012");
		
		((HtmlRadioButtonInput)form.getInputsByName("obaa.rights.cost").get(0)).click();
		((HtmlRadioButtonInput)form.getInputsByName("obaa.rights.copyright").get(1)).click();




		HtmlSubmitInput button = (HtmlSubmitInput) form
				.getElementById("submitButton");

		final HtmlPage page2 = button.click();
		assertThat(page2.asText(), containsString("newDocumentComplete"));

		final HtmlPage page3 = page2.getAnchorByText("newDocumentComplete").click();

		assertThat(page3.asText(), containsString("Título: newDocumentComplete"));
		assertThat(page3.asText(), containsString("Versão: 1.0"));
		assertThat(page3.asText(), containsString("Estrutura: Atômico"));
		assertThat(page3.asText(), containsString("Papel: Autor"));
		assertThat(page3.asText(), containsString("Custo: Não"));
		assertThat(page3.asText(), containsString("Direito autoral: Sim"));
		assertThat(page3.asText(), containsString("Estado: Rascunho"));




	}

}
