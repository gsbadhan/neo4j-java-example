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

public class UpdateAgentAsset {
	private List<Asset> asset;

	public List<Asset> getAsset() {
		return asset;
	}

	public void setAsset(List<Asset> asset) {
		this.asset = asset;
	}

	public static class Asset {
		private String serialNumber;
		private String guid;
		private List<Classes> classes;
		private ProductClass productClass;

		public Asset() {
		}

		public Asset(String serialNumber, List<Classes> classes, ProductClass productClass) {
			super();
			this.serialNumber = serialNumber;
			this.classes = classes;
			this.productClass = productClass;
		}

		public Asset(String guid, String serialNumber, List<Classes> classes, ProductClass productClass) {
			super();
			this.guid = guid;
			this.serialNumber = serialNumber;
			this.classes = classes;
			this.productClass = productClass;
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

		public List<Classes> getClasses() {
			return classes;
		}

		public void setClasses(List<Classes> classes) {
			this.classes = classes;
		}

		public ProductClass getProductClass() {
			return productClass;
		}

		public void setProductClass(ProductClass productClass) {
			this.productClass = productClass;
		}

		@Override
		public String toString() {
			return "Asset [serialNumber=" + serialNumber + ", productClass=" + productClass + ", classes=" + classes
					+ "]";
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

}
