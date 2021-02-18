package com.lumileds.nttdata.otmm.priint.repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lumileds.nttdata.otmm.priint.config.ProcessorConstants;

public class SQLRepository {

	private static final Logger logger = LoggerFactory.getLogger(SQLRepository.class);

	private static Connection conn = null;

	public Connection createConnection() {

		try {
			
			logger.debug("DB URL is : {}", ProcessorConstants.DB_DRIVER);

			Class.forName(ProcessorConstants.DB_DRIVER).newInstance();

			conn = DriverManager.getConnection
					(
							ProcessorConstants.DB_URL,
							ProcessorConstants.DB_USER,
							ProcessorConstants.DB_PASSWORD
							);

			if (null != conn) {

				DatabaseMetaData dm = (DatabaseMetaData) conn.getMetaData();
				logger.debug("Driver name : " + dm.getDriverName());
				logger.debug("Driver version : " + dm.getDriverVersion());
				logger.debug("Product name : " + dm.getDatabaseProductName());
				logger.debug("Product version : " + dm.getDatabaseProductVersion());

			}

			return conn;

		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		return conn;

	}

	public List<String> getAssetIDsToBeDeleted(Connection conn) {

		List<String> assetIDList = new ArrayList<String>();
		
		String deleteAssets = "SELECT ASSET_ID FROM "
				+ " PRIINT_DUPLICATES "
				+ " WHERE DEL_FLAG=?";
		
		try {

			PreparedStatement deleteAssetsStatement = 
					conn.prepareStatement(deleteAssets);

			deleteAssetsStatement.setString(1, ProcessorConstants.DELETE_FLAG);
			
			ResultSet rs = deleteAssetsStatement.executeQuery();

			while (rs.next()) {
				assetIDList.add(rs.getString(1));
			}
			
		} catch (SQLException sqlEx) {
			logger.error("Exception while fetchg asset IDs to be delted: {}", sqlEx);
		}


		return assetIDList;
	}

	// Update deleted assets back to DB
	public void updateDeletedAssets (
			Connection conn,
			List<String> assetIDList
			)
	{
		try
		{

			for (String assetID : assetIDList) {
			
			String updateDeletedAssets = " UPDATE "
					+ " PRIINT_DUPLICATES"
					+ " SET "
					+ " DEL_FLAG = ? "
					+ " WHERE ASSET_ID = ? ";

			// create our java prepared statement using a sql update query
			PreparedStatement updateDeletedAssetsStatement = 
					conn.prepareStatement(updateDeletedAssets);

			// set the prepared statement parameters
			updateDeletedAssetsStatement.setString(1,ProcessorConstants.PURGE_FLAG);
			updateDeletedAssetsStatement.setString(2,assetID);
						// call executeUpdate to execute our sql update statement
			updateDeletedAssetsStatement.executeUpdate();
			updateDeletedAssetsStatement.close();
			}
		}
		catch (SQLException sqlEx)
		{
			logger.error("Exception while updating deleted asset IDs: {}", sqlEx);
		}
	}

	public String getLatestOTMMVersionUoiID(Connection conn, 
			String assetName,
			String otmmFolderId) {
		
		logger.debug("Folder ID to check for latest version is {} ",
				otmmFolderId);
		
		String latestVersionUoiID = "select top 1 a.uoi_id from "
		 + "uois a, "
		 + "link_matrixes b "
		 + "where "
		 + "a.logical_uoi_id = b.CHILD_ID and "
		 + "a.IS_LATEST_VERSION=? and "
		 + "b.parent_id=? and "
		 + "a.name=? "
		 + "order by a.IMPORT_DT desc";
		
		try {

			PreparedStatement latestVersionUoiIDStatement = 
					conn.prepareStatement(latestVersionUoiID);

			latestVersionUoiIDStatement.setString(1, ProcessorConstants.Y_FLAG);
			latestVersionUoiIDStatement.setString(2, otmmFolderId);
			latestVersionUoiIDStatement.setString(3, assetName);
			
			ResultSet rs = latestVersionUoiIDStatement.executeQuery();

			while (rs.next()) {
				return (rs.getString(1));
			}
			
		} catch (SQLException sqlEx) {
			logger.error("Exception while fetching latest "
					+ "asset version uoiID {}", sqlEx);
		}
		
		return null;
	}
	
	public static void closeConnection() {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
		}
	}

	

	
}
