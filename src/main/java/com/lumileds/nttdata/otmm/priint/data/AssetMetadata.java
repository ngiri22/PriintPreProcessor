package com.lumileds.nttdata.otmm.priint.data;

import lombok.Data;

@Data
public class AssetMetadata {
	
	private String author;
	private String subject;
	private String modelID;
	private String name;
	
	private String folderName;
	private String destinationFolder;
	
	private String assetOwner;
	
	private String wcmsConfidentiality;
	private String regions;
	
	private String assetType;
	private String keywords;
	private String brand;
	
	private String securityPolicyID;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AssetMetadata other = (AssetMetadata) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	
}
