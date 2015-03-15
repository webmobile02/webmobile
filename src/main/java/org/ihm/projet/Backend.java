package org.ihm.projet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.GistFile;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.GistService;
import org.eclipse.egit.github.core.service.UserService;

@Path("backend")
public class Backend {

	@GET
	@Path("test")
	public String version() {
		return "The current version is 0.0.1-SNAPSHOT";
	}

	@GET
	@Path("recipes/{user}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getRecipes(@PathParam("user") String user) {

		List<Gist> allGist = null;

		GistService gistService = new GistService();
		StringBuilder result = new StringBuilder();

		Collection<GistFile> gistFile = new ArrayList<GistFile>();
		Collection<String> urls = new ArrayList<String>();

		try {

			allGist = gistService.getGists(user);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (Gist gist : allGist) {

			gistFile.addAll(gist.getFiles().values());
		}

		for (GistFile gistFile2 : gistFile) {

			urls.add(gistFile2.getRawUrl());
		}

		for (String url : urls) {

			result.append(getHTML(url) + ",");
		}

		return "[" + result.toString() + "]";
	}

	@POST
	@Path("signIn")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public User signIn(@FormParam("login") String login,
			@FormParam("mdp") String mdp) {

		User user = null;

		GitHubClient client = new GitHubClient().setCredentials(login, mdp);
		UserService service = new UserService(client);

		try {

			user = service.getUser();
		} catch (IOException e) {

			user = null;
		}
		return user;
	}

	@POST
	@Path("connect")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Boolean connect(@FormParam("login") String login,
			@FormParam("mdp") String mdp) {

		Boolean result = false;

		GitHubClient client = new GitHubClient().setCredentials(login, mdp);
		UserService service = new UserService(client);

		try {

			service.getUser();
			result = true;
		} catch (IOException e) {

			result = false;
		}
		return result;
	}
	
	@POST
	@Path("createRecipe")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void createRecipe(@FormParam("recipe") String recipe){
		
		GitHubClient client = new GitHubClient().setCredentials("webmobile01",
				"azerty1234");
		GistService gistService = new GistService(client);
		
		Gist gist = new Gist().setDescription("Recette");;
		GistFile file = new GistFile().setContent(recipe);
		gist.setFiles(Collections.singletonMap("recette.json", file));
		
		try {
			
			gistService.createGist(gist);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@GET
	@Path("recipe/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String recipe(@PathParam("id") String id) {

		Gist gist = null;
		StringBuilder result = new StringBuilder();
		GistService gistService = new GistService();
		Collection<String> urls = new ArrayList<String>();

		try {

			gist = gistService.getGist(id);

			for (GistFile gistFile : gist.getFiles().values()) {
				urls.add(gistFile.getRawUrl());
			}

			for (String url : urls) {

				result.append(getHTML(url));
			}
		} catch (IOException e) {

			System.out.println("PB avec l'id du gist");
		}

		return result.toString();
	}

	@DELETE
	@Path("recipe/{id}")
	public void deleteRecipe(@PathParam("id") String id) {

		GitHubClient client = new GitHubClient().setCredentials("webmobile01",
				"azerty1234");
		GistService gistService = new GistService(client);

		try {

			gistService.deleteGist(id);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@PUT
	@Path("recipe/{id}/{key}/{modif}")
	public void updateRecipe(@PathParam("id") String id, @PathParam("key") String key, @PathParam("modif") String modif) throws JSONException {

		Gist gist = null;
		JSONObject jsonObj = null;
		GitHubClient client = new GitHubClient().setCredentials("webmobile01",
				"azerty1234");
		GistService gistService = new GistService(client);

		try {

			gist = gistService.getGist(id);

			for (GistFile gistFile : gist.getFiles().values()) {

				jsonObj = new JSONObject(gistFile.getContent());
				
				jsonObj.put(key, modif);
				
				gistFile.setContent(jsonObj.toString());
			}

			gistService.updateGist(gist);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@PUT
	@Path("recipe/{id}/star")
	public void starRecipe(@PathParam("id") String id){
		
		GitHubClient client = new GitHubClient().setCredentials("webmobile01",
				"azerty1234");
		GistService gistService = new GistService(client);
		
		try {
			
			gistService.starGist(id);
		} catch (IOException e) {
			
			System.out.println("PB lors du star du gist!");
		}
		
	}
	
	@DELETE
	@Path("recipe/{id}/star")
	public void unstarRecipe(@PathParam("id") String id){
		
		GitHubClient client = new GitHubClient().setCredentials("webmobile01",
				"azerty1234");
		GistService gistService = new GistService(client);
		
		try {
			
			gistService.unstarGist(id);
		} catch (IOException e) {
			
			System.out.println("PB lors du unStar du gist!");
		}
		
	}
	
	@PUT
	@Path("recipe/{id}/fork")
	public void forkRecipe(@PathParam("id") String id){
		
		GitHubClient client = new GitHubClient().setCredentials("webmobile01",
				"azerty1234");
		GistService gistService = new GistService(client);
		
		try {
			
			gistService.forkGist(id);
		} catch (IOException e) {
			
			System.out.println("PB lors du fork du gists !");
		}
		
	}
	

	public String getHTML(String url) {
		String result = "";
		try {
			HttpClient client = new DefaultHttpClient();
			HttpParams httpParameters = client.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
			HttpConnectionParams.setSoTimeout(httpParameters, 5000);
			HttpConnectionParams.setTcpNoDelay(httpParameters, true);
			HttpGet request = new HttpGet();
			request.setURI(new URI(url));
			HttpResponse response = client.execute(request);
			InputStream ips = response.getEntity().getContent();
			BufferedReader buf = new BufferedReader(new InputStreamReader(ips,
					"UTF-8"));
			StringBuilder sb = new StringBuilder();
			String s;
			while (true) {
				s = buf.readLine();
				if (s == null)
					break;
				sb.append(s);
			}
			buf.close();
			ips.close();
			result = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
