package com.lumileds.nttdata.otmm.priint.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lumileds.nttdata.otmm.priint.config.ProcessorConstants;
import com.lumileds.nttdata.otmm.priint.data.SessionResponse;
import com.lumileds.nttdata.otmm.priint.repository.OTMMRestRepository;

public class OTMMRestClient {

	private static final Logger logger = LoggerFactory.getLogger(OTMMRestClient.class);

	public SessionResponse getSessionToken() {

		logger.debug("Getting the session token");

		SessionResponse sessionResponse = new SessionResponse();
		
		//Get both otmmauthtoken and session ID
		//String[] sessionDetails = new String[2];
		
		//String  messageDigest = null;
		
		try {

			String urlParameters  = ProcessorConstants.USERNAME
					+ ProcessorConstants.EQUALS
					+ ProcessorConstants.OTMM_USER_NAME
					+ ProcessorConstants.AMPERSAND
					+ ProcessorConstants.PASSWORD
					+ ProcessorConstants.EQUALS
					+ ProcessorConstants.OTMM_PASSWORD;

			byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );

			int postDataLength = postData.length;

			URL otmmSessionURL = new URL (
					ProcessorConstants.OTMM_API_URL 
					+ ProcessorConstants.FRONT_SLASH
					+ ProcessorConstants.SESSIONS);

			HttpURLConnection conn = (HttpURLConnection) 
					otmmSessionURL.openConnection();

			conn.setDoOutput(true);

			conn.setInstanceFollowRedirects(false);

			conn.setRequestMethod(ProcessorConstants.POST);

			conn.setRequestProperty(ProcessorConstants.CONTENT_TYPE, 
					ProcessorConstants.FORM_URL_ENCODE);

			conn.setRequestProperty(ProcessorConstants.CHARSET,
					ProcessorConstants.UTF_8); 

			conn.setRequestProperty(ProcessorConstants.CONTENT_LENGTH,
					Integer.toString(postDataLength ));

			//			conn.setUseCaches(false);

			conn.setConnectTimeout(ProcessorConstants.CONNECT_TIMEOUT);
			conn.setReadTimeout(ProcessorConstants.READ_TIMEOUT);

			try(DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
				wr.write( postData );
				wr.flush();
			}


			//			String inputJson = "{\"version\": "
			//					+ ProcessorConstants.OTMM_API_VERSION
			//					+ ", \"username\": "
			//					+ ProcessorConstants.OTMM_USER_NAME
			//					+ ", \"password\": "
			//					+ ProcessorConstants.OTMM_PASSWORD
			//					+ "}";
			//
			//			OutputStream os = conn.getOutputStream();
			//			os.write(inputJson.getBytes());
			//			os.flush();


			if (conn.getResponseCode() != 200) {

				logger.error("Failed : HTTP error code: {}",
						conn.getResponseCode());

				System.exit(1);

			}


			BufferedReader br = new BufferedReader(
					new InputStreamReader(
							(conn.getInputStream())
							)
					);

			String output;
			String sessionResponseOutput = new String();

			while ((output = br.readLine()) != null) {
				sessionResponseOutput += output ;

			}

			logger.debug("Full Json Session Response: {}", sessionResponseOutput);

			conn.disconnect();

			JSONObject jsonObject = new JSONObject(sessionResponseOutput);

			JSONObject sessionResource = jsonObject.getJSONObject(
					ProcessorConstants.SESSION_RESOURCE);
					

			JSONObject session = sessionResource.getJSONObject(
					ProcessorConstants.SESSION);

			//sessionDetails[0] = session.getString(ProcessorConstants.MESSAGE_DIGEST);
			
			sessionResponse.setId(
					session.getInt(ProcessorConstants.ID));
			
			sessionResponse.setMessageDigest(
					session.getString(ProcessorConstants.MESSAGE_DIGEST));
			
			//Get the Session ID
			//sessionDetails[1] = String.valueOf(session.getInt(ProcessorConstants.ID));

			logger.debug("Message Digest: {}", sessionResponse.getMessageDigest());
			
			logger.debug("Session Id: {}", sessionResponse.getId());
			
			
		} catch (SocketTimeoutException sockEx) {

			logger.error("Socket Timeout exception: {}", sockEx);			

		} catch (MalformedURLException malEx) {

			logger.error("Malformed URL Exception: {}", malEx);			

		}
		catch (IOException ioEx) {

			logger.error("Input Output Exception: {}", ioEx);

		}
		
		return sessionResponse ;

	}

	public void deleteAsset(SessionResponse sessionResponse, List<String> assetIDList) {

		try {

			for ( String assetID : assetIDList) {

				logger.debug("Asset IDs to be deleted: {}", assetID);

				String urlParameters = ProcessorConstants.ACTION
						+ ProcessorConstants.EQUALS
						+ ProcessorConstants.DELETE
						+ ProcessorConstants.AMPERSAND
						+ ProcessorConstants.ASSET_STATE_URL_PARAMS;


				URL otmmStateURL = new URL(
						ProcessorConstants.OTMM_API_URL
						+ ProcessorConstants.FRONT_SLASH
						+ ProcessorConstants.ASSETS_URL
						+ ProcessorConstants.FRONT_SLASH
						+ assetID
						+ ProcessorConstants.FRONT_SLASH
						+ ProcessorConstants.STATE_URL
						);

				HttpURLConnection httpCon = (HttpURLConnection) otmmStateURL.openConnection();

				httpCon.setRequestProperty(ProcessorConstants.CONTENT_TYPE, 
						ProcessorConstants.FORM_URL_ENCODE);

				byte[] putData = urlParameters.getBytes( StandardCharsets.UTF_8 );

				int putDataLength = putData.length;

				httpCon.setDoOutput(true);
				httpCon.setRequestMethod(ProcessorConstants.HTTP_PUT);

				httpCon.setRequestProperty("charset",
						ProcessorConstants.UTF_8); 

				//Changes for v5 api
				httpCon.setRequestProperty(
						ProcessorConstants.OTMM_AUTH_TOKEN,
						sessionResponse.getMessageDigest());
				
				httpCon.setRequestProperty(
						ProcessorConstants.X_REQUESTED_BY,
						String.valueOf(sessionResponse.getId()));

				httpCon.setRequestProperty(ProcessorConstants.CONTENT_LENGTH,
						Integer.toString(putDataLength ));

				DataOutputStream out = new DataOutputStream(
						httpCon.getOutputStream());
				out.write(putData);
				out.flush();
				out.close();

				if (httpCon.getResponseCode() != 200) {

					logger.error("Failed : HTTP error code: {}",
							httpCon.getResponseCode());

					System.exit(1);

				}


				BufferedReader br = new BufferedReader(
						new InputStreamReader(
								(httpCon.getInputStream())
								)
						);

				String output;
				String deleteResponse = new String();

				while ((output = br.readLine()) != null) {
					deleteResponse += output ;

				}

				logger.debug("Full Json Delete Response: {}", deleteResponse);

				httpCon.disconnect();

			}
		}
		catch (IOException ioEx) {

			logger.error("Input Output Exception while making"
					+ " Rest call for deleting asset: {}", ioEx);

		}

	}


	public void checkInAsset(
			String latestAssetVersionUoiID,
			SessionResponse sessionResponse,
			String checkInAssetAbsPath) {

		OTMMRestRepository otmmRestRepository = new OTMMRestRepository();

		//Checkout the asset
		otmmRestRepository.changeAssetState(latestAssetVersionUoiID, sessionResponse, ProcessorConstants.CHECK_OUT);

		//Lock the asset
		otmmRestRepository.changeAssetState(latestAssetVersionUoiID, sessionResponse, ProcessorConstants.LOCK);

		//Get the Import Job ID		
		String importJobId = otmmRestRepository.createImportJob(sessionResponse);


		//Create file from the absolute path
		File checkInAsset = new File(checkInAssetAbsPath);

		//Create the rendition with the job id
		otmmRestRepository.createRendition(importJobId, sessionResponse, checkInAsset);

		
		//Finally Check In the asset
		otmmRestRepository.checkInAssetWithJobId(importJobId, sessionResponse,
				checkInAsset.getName(), latestAssetVersionUoiID);
	}
}