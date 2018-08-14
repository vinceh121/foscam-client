package me.vinceh121.foscamclient;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class CGIManager {
	private static CGIManager instance = new CGIManager();
	private String host, username, password;
	private int port;
	private CloseableHttpClient client;

	private CGIManager() {
	}

	public static CGIManager getInstance() {
		return instance;
	}

	public void init() {
		client = HttpClients.createDefault();
	}

	public Element execCmd(String cmd, NameValuePair... nvps)
			throws ClientProtocolException, IOException, URISyntaxException, CGIException {
		URIBuilder ub = new URIBuilder("http://" + host + ":" + port + "/cgi-bin/CGIProxy.fcgi?usr=" + username
				+ "&pwd=" + password + "&cmd=" + cmd);
		if (nvps != null) {
			for (NameValuePair p : nvps) {
				ub.addParameter(p.getName(), p.getValue());
			}
		}

		HttpGet get = new HttpGet(ub.build());

		ResponseHandler<Element> responseHandler = new ResponseHandler<Element>() {

			@Override
			public Element handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
				if (response.getStatusLine().getStatusCode() == 200) {
					try {
						Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
								.parse(response.getEntity().getContent());
						return doc.getDocumentElement();
					} catch (UnsupportedOperationException | SAXException | ParserConfigurationException e) {
						e.printStackTrace();
					}
				} else {
					throw new ClientProtocolException(
							"Unexpected response status: " + response.getStatusLine().getStatusCode());
				}
				return null;
			}
		};
		Element e = client.execute(get, responseHandler);

		try {
			int result = Integer.parseInt(e.getElementsByTagName("result").item(0).getTextContent());
			if (result != 0) {
				throw new CGIException(result);
			}
		} catch (NumberFormatException nfe) {
			System.err.println("Could not parse result code. Result: "
					+ e.getElementsByTagName("result").item(0).getTextContent());
		}
		return e;
	}

	public String getRtspMrl() {
		return "rtsp://" + username + ":" + password + "@" + host + ":" + port + "/videoMain";
	}

	/**
	 * @param host
	 *            the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}
}
