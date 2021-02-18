package com.lumileds.nttdata.otmm.priint;

import java.sql.Connection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lumileds.nttdata.otmm.priint.data.SessionResponse;
import com.lumileds.nttdata.otmm.priint.repository.SQLRepository;
import com.lumileds.nttdata.otmm.priint.util.OTMMRestClient;

public class PriintPostProcessor {

	private static final Logger logger = LoggerFactory.getLogger(PriintPostProcessor.class);
	
	public static void main(String[] args) {
		
		OTMMRestClient otmmRestClient = new OTMMRestClient();

		//Get the auth token from the Rest Client
		
		SessionResponse sessionResponse = otmmRestClient.getSessionToken();

		SQLRepository sqlRepository = new SQLRepository();

		//Get the SQL Connection
		Connection conn = sqlRepository.createConnection();

		//Get the list of assetId's to be deleted.
		List<String> assetIDList = sqlRepository.getAssetIDsToBeDeleted(conn);

		logger.debug("Number of assets to be deleted: {}", assetIDList.size());
		
		if (assetIDList.size() > 0) {

			otmmRestClient.deleteAsset(sessionResponse, assetIDList);
			
			sqlRepository.updateDeletedAssets(conn, assetIDList);
		}

	}

}
