/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.pojo.services;

import java.util.List;

public class RegisterAgentAsset {
	private List<Asset> assets;

	public RegisterAgentAsset() {
	}

	public RegisterAgentAsset(List<Asset> assets) {
		this.assets = assets;
	}

	public List<Asset> getAssets() {
		return assets;
	}

	public void setAsset(List<Asset> assets) {
		this.assets = assets;
	}

	public static class Asset {
		private String bootStrapKey;
		private String serialNumber;
		private String orgName;
		private String gatewayGuid;
		private String guid;
		private String name;
		private List<Classes> classes;
		private ProductClass productClass;
		private DataShard dataShardInfo;
		private Org connectedOrg;
		private Parent parent;
		private String accessKey;

		public Asset() {
		}

		public Asset(String orgName, String serialNumber, String guid, String name, List<Classes> classes,
				ProductClass productClass, DataShard dataShardInfo, Org connectedOrg) {
			super();
			this.orgName = orgName;
			this.serialNumber = serialNumber;
			this.guid = guid;
			this.name = name;
			this.classes = classes;
			this.productClass = productClass;
			this.dataShardInfo = dataShardInfo;
			this.connectedOrg = connectedOrg;
		}

		public String getSerialNumber() {
			return serialNumber;
		}

		public void setSerialNumber(String serialNumber) {
			this.serialNumber = serialNumber;
		}

		public String getGuid() {
			return guid;
		}

		public void setGuid(String guid) {
			this.guid = guid;
		}

		public ProductClass getProductClass() {
			return productClass;
		}

		public void setProductClass(ProductClass productClass) {
			this.productClass = productClass;
		}

		public List<Classes> getClasses() {
			return classes;
		}

		public void setClasses(List<Classes> classes) {
			this.classes = classes;
		}

		public String getBootStrapKey() {
			return bootStrapKey;
		}

		public void setBootStrapKey(String bootStrapKey) {
			this.bootStrapKey = bootStrapKey;
		}

		public String getOrgName() {
			return orgName;
		}

		public void setOrgName(String orgName) {
			this.orgName = orgName;
		}

		public String getGatewayGuid() {
			return gatewayGuid;
		}

		public void setGatewayGuid(String gatewayGuid) {
			this.gatewayGuid = gatewayGuid;
		}

		public DataShard getDataShardInfo() {
			return dataShardInfo;
		}

		public void setDataShardInfo(DataShard dataShardInfo) {
			this.dataShardInfo = dataShardInfo;
		}

		public Org getConnectedOrg() {
			return connectedOrg;
		}

		public void setConnectedOrg(Org connectedOrg) {
			this.connectedOrg = connectedOrg;
		}

		public Parent getParent() {
			return parent;
		}

		public void setParent(Parent parent) {
			this.parent = parent;
		}

		public String getAccessKey() {
			return accessKey;
		}

		public void setAccessKey(String accessKey) {
			this.accessKey = accessKey;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

	public static class Classes {
		private String name;
		private String guid;

		public Classes() {
		}

		public Classes(String name) {
			super();
			this.name = name;
		}

		public Classes(String name, String guid) {
			super();
			this.name = name;
			this.guid = guid;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getGuid() {
			return guid;
		}

		public void setGuid(String guid) {
			this.guid = guid;
		}

		@Override
		public String toString() {
			return "Classes [name=" + name + ", guid=" + guid + "]";
		}
	}

	public static class ProductClass {
		private String name;
		private String guid;

		public ProductClass() {
		}

		public ProductClass(String name) {
			super();
			this.name = name;
		}

		public ProductClass(String name, String guid) {
			super();
			this.name = name;
			this.guid = guid;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getGuid() {
			return guid;
		}

		public void setGuid(String guid) {
			this.guid = guid;
		}

		@Override
		public String toString() {
			return "ProductClass [name=" + name + ", guid=" + guid + "]";
		}
	}

	public static class DataShard {
		private String name;
		private String guid;
		private String ip;
		private String port;
		private String sourcePath;

		public DataShard() {
		}

		public DataShard(String name) {
			super();
			this.name = name;
		}

		public DataShard(String name, String guid, String ip, String port, String sourcePath) {
			super();
			this.name = name;
			this.guid = guid;
			this.ip = ip;
			this.port = port;
			this.sourcePath = sourcePath;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getGuid() {
			return guid;
		}

		public void setGuid(String guid) {
			this.guid = guid;
		}

		public String getSourcePath() {
			return sourcePath;
		}

		public void setSourcePath(String sourcePath) {
			this.sourcePath = sourcePath;
		}

		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

		public String getPort() {
			return port;
		}

		public void setPort(String port) {
			this.port = port;
		}

		@Override
		public String toString() {
			return "DataShard [name=" + name + ", guid=" + guid + "]";
		}
	}

	public static class Org {
		private String name;
		private String guid;

		public Org() {
		}

		public Org(String name) {
			super();
			this.name = name;
		}

		public Org(String name, String guid) {
			super();
			this.name = name;
			this.guid = guid;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getGuid() {
			return guid;
		}

		public void setGuid(String guid) {
			this.guid = guid;
		}

		@Override
		public String toString() {
			return "Org [name=" + name + ", guid=" + guid + "]";
		}
	}

	public static class Parent {
		private String name;
		private String serialNumber;
		private ProductClass productClass;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getSerialNumber() {
			return serialNumber;
		}

		public void setSerialNumber(String serialNumber) {
			this.serialNumber = serialNumber;
		}

		public ProductClass getProductClass() {
			return productClass;
		}

		public void setProductClass(ProductClass productClass) {
			this.productClass = productClass;
		}

	}
}
