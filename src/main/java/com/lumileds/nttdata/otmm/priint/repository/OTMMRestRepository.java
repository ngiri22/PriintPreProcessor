package com.lumileds.nttdata.otmm.priint.repository;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lumileds.nttdata.otmm.priint.config.ProcessorConstants;
import com.lumileds.nttdata.otmm.priint.data.SessionResponse;

public class OTMMRestRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(OTMMRestRepository.class);
	
	public String getLatestAssetUoiID(
			String otmmAuthToken,
			String assetFolderID,
			String fileName			
			) {
		//TODO
		return null ;
	}

	public void checkInAssetWithJobId(
			String importJobID,
			SessionResponse sessionResponse,
			String fileName,
			String latestAssetVersionUoiID
			) {

		logger.debug("Checking in the Asset with JobID: {}", importJobID);

		try {

			URL otmmcheckinsURL = new URL (
					ProcessorConstants.OTMM_API_URL 
					+ ProcessorConstants.FRONT_SLASH
					+ ProcessorConstants.JOBS
					+ ProcessorConstants.FRONT_SLASH
					+ ProcessorConstants.CHECKINS
					+ ProcessorConstants.FRONT_SLASH
					+ importJobID);


			//Generate a unique random boundary
			String boundary = Long.toHexString(System.currentTimeMillis());

			HttpURLConnection httpURLConnection = (HttpURLConnection) 
					otmmcheckinsURL.openConnection();

			httpURLConnection.setDoOutput(true);

			httpURLConnection.setInstanceFollowRedirects(false);

			httpURLConnection.setRequestMethod(ProcessorConstants.HTTP_PUT);

			httpURLConnection.setRequestProperty(
					ProcessorConstants.CONTENT_TYPE, 
					ProcessorConstants.MULTIPART_FORM_WITH_BOUNDARY
					+ boundary);

			//Set otmmauthtoken property
			httpURLConnection.setRequestProperty(
					ProcessorConstants.OTMM_AUTH_TOKEN,
					sessionResponse.getMessageDigest());

			//Set X-requested-By property
			httpURLConnection.setRequestProperty(
					ProcessorConstants.X_REQUESTED_BY,
					String.valueOf(sessionResponse.getId()));

			httpURLConnection.setRequestProperty(
					ProcessorConstants.CONTENT_LENGTH,
					Integer.toString(1024) );
			
			httpURLConnection.setRequestProperty(
					ProcessorConstants.CHARSET,
					ProcessorConstants.UTF_8); 

			httpURLConnection.setConnectTimeout(ProcessorConstants.CONNECT_TIMEOUT);
			httpURLConnection.setReadTimeout(ProcessorConstants.READ_TIMEOUT);



			OutputStream output = httpURLConnection.getOutputStream();
			PrintWriter writer = new PrintWriter(
					new OutputStreamWriter(
							output, 
							ProcessorConstants.UTF_8), true);

			// Send import_job_id
			writer.append("--" + boundary).append(ProcessorConstants.CRLF);

			writer
			.append(
					"Content-Disposition: form-data; "
							+ "name=\""
							+ ProcessorConstants.IMPORT_JOB_ID
							+ "\""
					)
			.append(ProcessorConstants.CRLF);

			writer
			.append(
					"Content-Type: text/plain; "
							+ ProcessorConstants.CHARSET
							+ ProcessorConstants.EQUALS
							+ ProcessorConstants.UTF_8
					)
			.append(ProcessorConstants.CRLF);

			writer
			.append(ProcessorConstants.CRLF)
			.append(importJobID)
			.append(ProcessorConstants.CRLF)
			.flush();

			// Send upload manifest.
			writer
			.append("--" + boundary)
			.append(ProcessorConstants.CRLF);

			writer
			.append(
					"Content-Disposition: form-data; "
							+ "name=\"manifest\"; "
							+ "filename=\"blob\"")
			.append(ProcessorConstants.CRLF);

			writer
			.append(
					"Content-Type: "
							+ ProcessorConstants.APPLICATION_JSON
							+ "; "
							+ ProcessorConstants.CHARSET
							+ ProcessorConstants.EQUALS
							+ ProcessorConstants.UTF_8
					)
			.append(ProcessorConstants.CRLF);

			writer
			.append(ProcessorConstants.CRLF)
			.flush();

			//Files.copy(checkInAsset.toPath(), output);

			writer.append(
					"{\"upload_manifest\":"
							+ "{\"master_files\":"
							+ "[{\"file\":"
							+ "{\"file_name\":\""
							+ fileName
							+ "\"},"
							+ "\"uoi_id\":\""
							+ latestAssetVersionUoiID
							+ "\"}"
							+ "]}}");


			output.flush(); // Important before continuing with writer!
			writer
			.append(ProcessorConstants.CRLF)
			.flush(); // CRLF is important! It indicates end of boundary.


			// Send file name
			writer
			.append("--" + boundary)
			.append(ProcessorConstants.CRLF);

			writer
			.append(
					"Content-Disposition: form-data; "
							+ "name=\"file_name\"")
			.append(ProcessorConstants.CRLF);

			writer
			.append(ProcessorConstants.CRLF)
			.flush();

			writer.append(fileName);

			output.flush(); // Important before continuing with writer!
			writer
			.append(ProcessorConstants.CRLF)
			.flush(); // CRLF is important! It indicates end of boundary.



			// End of multipart/form-data.
			writer
			.append("--" + boundary + "--")
			.append(ProcessorConstants.CRLF)
			.flush();

			TimeUnit.SECONDS.sleep(1);
			
			
			if (
					(httpURLConnection.getResponseCode() != 200) &&
					(httpURLConnection.getResponseCode() != 202)
					){

				logger.error("Failed while checking in the asset, "
						+ "http error code is : {}",
						httpURLConnection.getResponseCode());

				System.exit(1);
			}


			BufferedReader br = new BufferedReader(
					new InputStreamReader(
							(httpURLConnection.getInputStream())
							)
					);

			String outputResponse;
			String checkInAssetResponse = new String();

			while ((outputResponse = br.readLine()) != null) {
				checkInAssetResponse += outputResponse ;

			}

			logger.debug("Full Json response of Checkin Job : {}", 
					checkInAssetResponse);

			httpURLConnection.disconnect();

			

		} catch (SocketTimeoutException sockEx) {

			logger.error("Socket Timeout exception while "
					+ "checking in asset: {}", sockEx);			

		} catch (MalformedURLException malEx) {

			logger.error("Malformed URL Exception while "
					+ "checking in asset: {}", malEx);			

		}
		catch (IOException ioEx) {

			logger.error("Input Output Exception while "
					+ "checking in asset: {}", ioEx);

		} catch (InterruptedException intEx) {
			logger.error("Interrupted exception while "
					+ "checking in asset: {}", intEx);
		}
		
	}

	public void createRendition (
			String importJobID,
			SessionResponse sessionResponse,
			File checkInAsset
			) {

		try {

			URL otmmRenditionsURL = new URL (
					ProcessorConstants.OTMM_API_URL 
					+ ProcessorConstants.FRONT_SLASH
					+ ProcessorConstants.RENDITIONS);


			//Generate a unique random boundary
			String boundary = Long.toHexString(System.currentTimeMillis());

			HttpURLConnection httpURLConnection = (HttpURLConnection) 
					otmmRenditionsURL.openConnection();

			httpURLConnection.setDoOutput(true);

			httpURLConnection.setInstanceFollowRedirects(false);

			httpURLConnection.setRequestMethod(ProcessorConstants.POST);

			httpURLConnection.setRequestProperty(
					ProcessorConstants.CONTENT_TYPE, 
					ProcessorConstants.MULTIPART_FORM_WITH_BOUNDARY
					+ boundary);

			//Set otmmauthtoken property
			httpURLConnection.setRequestProperty(
					ProcessorConstants.OTMM_AUTH_TOKEN,
					sessionResponse.getMessageDigest());

			//Set X-requested-By property
			httpURLConnection.setRequestProperty(
					ProcessorConstants.X_REQUESTED_BY,
					String.valueOf(sessionResponse.getId()));

			httpURLConnection.setRequestProperty(
					ProcessorConstants.CONTENT_LENGTH,
					Integer.toString(0) );
			
			httpURLConnection.setRequestProperty(
					ProcessorConstants.CHARSET,
					ProcessorConstants.UTF_8); 

			httpURLConnection.setConnectTimeout(ProcessorConstants.CONNECT_TIMEOUT);
			httpURLConnection.setReadTimeout(ProcessorConstants.READ_TIMEOUT);



			OutputStream output = httpURLConnection.getOutputStream();
			
					
			PrintWriter writer = new PrintWriter(
					new OutputStreamWriter(
							output, 
							ProcessorConstants.UTF_8), true);

			
			// Send import_job_id
			writer.append("--" + boundary).append(ProcessorConstants.CRLF);

			writer
			.append(
					"Content-Disposition: form-data; "
							+ "name=\""
							+ ProcessorConstants.IMPORT_JOB_ID
							+ "\""
					)
			.append(ProcessorConstants.CRLF);

//			writer
//			.append(
//					"Content-Type: text/plain; "
//							+ ProcessorConstants.CHARSET
//							+ ProcessorConstants.EQUALS
//							+ ProcessorConstants.UTF_8
//					)
//			.append(ProcessorConstants.CRLF);

			writer
			.append(ProcessorConstants.CRLF)
			.append(importJobID)
			.append(ProcessorConstants.CRLF)
			.flush();

			// Send new version for the file.
			writer
			.append("--" + boundary)
			.append(ProcessorConstants.CRLF);

			writer
			.append(
					"Content-Disposition: form-data; "
							+ "name=\"file\"; "
							+ "filename=\"" 
							+ checkInAsset.getName() 
							+ "\"")
			.append(ProcessorConstants.CRLF);

			writer
			.append(
					"Content-Type: "
							+ URLConnection.guessContentTypeFromName(
									checkInAsset.getName()
									)
					)
			.append(ProcessorConstants.CRLF);

			logger.debug("Asset name: {}", checkInAsset.getName());
			
			writer
			.append(ProcessorConstants.CRLF)
			.flush();

			Files.copy(checkInAsset.toPath(), output);
			
			output.flush(); // Important before continuing with writer!
			writer
			.append(ProcessorConstants.CRLF)
			.flush(); // CRLF is important! It indicates end of boundary.

			// End of multipart/form-data.
			writer
			.append("--" + boundary + "--")
			.append(ProcessorConstants.CRLF)
			.flush();

			if (httpURLConnection.getResponseCode() != 200) {

				logger.error("Failed while creating rendition, "
						+ "http error code is : {}",
						httpURLConnection.getResponseCode());

				System.exit(1);
			}


			BufferedReader br = new BufferedReader(
					new InputStreamReader(
							(httpURLConnection.getInputStream())
							)
					);

			String outputResponse;
			String createRenditionResponse = new String();

			while ((outputResponse = br.readLine()) != null) {
				createRenditionResponse += outputResponse ;

			}

			logger.debug("Full Json of Create Rendition Response: {}", 
					createRenditionResponse);

			httpURLConnection.disconnect();

			//Delete the asset after the rendition is created
			//We no longer need it.
			Files.delete(checkInAsset.toPath());

		} catch (SocketTimeoutException sockEx) {

			logger.error("Socket Timeout exception while "
					+ "creating rendition: {}", sockEx);			

		} catch (MalformedURLException malEx) {

			logger.error("Malformed URL Exception while "
					+ "creating rendition: {}", malEx);			

		}
		catch (IOException ioEx) {

			logger.error("Input Output Exception while "
					+ "creating rendition: {}", ioEx);

		}

	}

	//Creates the Import Job and returns the job id
	public String createImportJob(SessionResponse sessionResponse) {

		String jobID = null ;

		try {

			//Changes for v5
			String urlParameters = "version=v5";
			
			byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );

			//int postDataLength = postData.length;
			
			URL otmmJobsImportsURL = new URL (
					ProcessorConstants.OTMM_API_URL 
					+ ProcessorConstants.FRONT_SLASH
					+ ProcessorConstants.JOBS
					+ ProcessorConstants.FRONT_SLASH
					+ ProcessorConstants.IMPORTS);

			logger.debug("Imports URL: {}", otmmJobsImportsURL.toString());
			
			HttpURLConnection httpURLConnection = (HttpURLConnection) 
					otmmJobsImportsURL.openConnection();

			httpURLConnection.setDoOutput(true);

			httpURLConnection.setInstanceFollowRedirects(false);

			httpURLConnection.setRequestMethod(ProcessorConstants.POST);

			httpURLConnection.setRequestProperty(
					ProcessorConstants.CONTENT_TYPE, 
					ProcessorConstants.FORM_URL_ENCODE);

			//Set otmmauthtoken property
			httpURLConnection.setRequestProperty(
					ProcessorConstants.OTMM_AUTH_TOKEN,
					sessionResponse.getMessageDigest());

			//Set X-requested-By property
			httpURLConnection.setRequestProperty(
					ProcessorConstants.X_REQUESTED_BY,
					String.valueOf(sessionResponse.getId()));
			
			httpURLConnection.setRequestProperty(
					ProcessorConstants.CONTENT_LENGTH,
					Integer.toString(0) );
			
			httpURLConnection.setRequestProperty(
					ProcessorConstants.CHARSET,
					ProcessorConstants.UTF_8); 

			httpURLConnection.setConnectTimeout(ProcessorConstants.CONNECT_TIMEOUT);
			httpURLConnection.setReadTimeout(ProcessorConstants.READ_TIMEOUT);
			
			try(DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream())) {
				wr.write( postData );
				wr.flush();
			}

			if (httpURLConnection.getResponseCode() != 200) {

				logger.error("Failed while creating Import Job, "
						+ "http error code is : {}",
						httpURLConnection.getResponseCode());

				System.exit(1);
			}

			
			BufferedReader br = new BufferedReader(
					new InputStreamReader(
							(httpURLConnection.getInputStream())
							)
					);

			String output;
			String createImportJobResponse = new String();

			while ((output = br.readLine()) != null) {
				createImportJobResponse += output ;

			}

			logger.debug("Full Json of Create Import Job Response: {}", 
					createImportJobResponse);

			httpURLConnection.disconnect();

			JSONObject jsonObject = new JSONObject(createImportJobResponse);

			JSONObject jobHandle = jsonObject.getJSONObject(ProcessorConstants.JOB_HANDLE);

			jobID = jobHandle.getString(ProcessorConstants.JOB_ID);

			logger.debug("JOB ID : {}", jobID);


		} catch (SocketTimeoutException sockEx) {

			logger.error("Socket Timeout exception: {}", sockEx);			

		} catch (MalformedURLException malEx) {

			logger.error("Malformed URL Exception: {}", malEx);			

		}
		catch (IOException ioEx) {

			logger.error("Input Output Exception: {}", ioEx);

		}

		return jobID;
	}


	//Changes the asset state based on the state parameter passed
	public void changeAssetState(String latestAssetVersionUoiID, 
			SessionResponse sessionResponse, String state) {

		logger.debug("Action being executed for assetId : {} "
				+ "is {}", latestAssetVersionUoiID, state);

		try {

			URL otmmStateURL = new URL(
					ProcessorConstants.OTMM_API_URL
					+ ProcessorConstants.FRONT_SLASH
					+ ProcessorConstants.ASSETS_URL
					+ ProcessorConstants.FRONT_SLASH
					+ latestAssetVersionUoiID
					+ ProcessorConstants.FRONT_SLASH
					+ ProcessorConstants.STATE_URL
					);

			HttpURLConnection httpCon = (HttpURLConnection) 
					otmmStateURL.openConnection();

			httpCon.setRequestProperty(ProcessorConstants.CONTENT_TYPE, 
					ProcessorConstants.FORM_URL_ENCODE);

			String urlParameters = ProcessorConstants.ACTION 
					+ ProcessorConstants.EQUALS
					+ state
					+ ProcessorConstants.AMPERSAND
					+ ProcessorConstants.ASSET_STATE_URL_PARAMS;

			byte[] putData = urlParameters.getBytes(StandardCharsets.UTF_8);

			int putDataLength = putData.length;

			httpCon.setDoOutput(true);
			httpCon.setRequestMethod(ProcessorConstants.HTTP_PUT);

			httpCon.setRequestProperty(
					ProcessorConstants.CHARSET,
					ProcessorConstants.UTF_8);

			//Set otmmauthtoken property
			httpCon.setRequestProperty(
					ProcessorConstants.OTMM_AUTH_TOKEN,
					sessionResponse.getMessageDigest());

			//Set X-requested-By property
			httpCon.setRequestProperty(
					ProcessorConstants.X_REQUESTED_BY,
					String.valueOf(sessionResponse.getId()));
			
			
			httpCon.setRequestProperty(
					ProcessorConstants.CONTENT_LENGTH,
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

			String assetStateResponse = new String();

			while ((output = br.readLine()) != null) {
				assetStateResponse += output ;

			}

			logger.debug("Full Json Response for state change: {}",
					assetStateResponse);

			httpCon.disconnect();

		}
		catch (IOException ioEx) {

			logger.error("Input Output Exception while making rest api "
					+ "call for changing the state of asset: {}", ioEx);

		}

	}

	
}
