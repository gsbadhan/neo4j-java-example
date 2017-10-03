package org.digi.lg.neo4j.core;

import org.apache.commons.lang3.StringUtils;

public class DBRowUUID {
	private static final String SEPARATOR = "#";

	private DBRowUUID() {
	}

	private static String buildUID(Object... args) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			if (StringUtils.isBlank(args[i].toString())) {
				throw new IllegalArgumentException("invalid DBUID value:" + args[i]);
			}
			if (i == 0)
				builder.append(args[i]);
			else
				builder.append(SEPARATOR).append(args[i]);
		}
		return builder.toString();
	}

	public static class ClassUID implements DBUUID {
		private final String uuid;

		public ClassUID(final String domainClassGuid, final String className) {
			super();
			this.uuid = DBRowUUID.buildUID(className.toLowerCase(), domainClassGuid);
		}

		@Override
		public String get() {
			return uuid;
		}
	}

	public static class ProductClassUID implements DBUUID {
		private final String domainClassGuid;
		private final String pcName;

		public ProductClassUID(final String domainClassGuid, final String pcName) {
			super();
			this.domainClassGuid = domainClassGuid;
			this.pcName = pcName;
		}

		@Override
		public String get() {
			return DBRowUUID.buildUID(pcName.toLowerCase(), domainClassGuid);
		}
	}

	public static class AssetUID implements DBUUID {
		private final String uuid;

		public AssetUID(final String domainClassGuid, final String serialNumber) {
			super();
			this.uuid = DBRowUUID.buildUID(serialNumber.toLowerCase(), domainClassGuid);
		}

		@Override
		public String get() {
			return uuid;
		}
	}

	public static class EventsUID implements DBUUID {
		private final String uuid;

		public EventsUID(final String domainClassGuid, final String eventName) {
			super();
			this.uuid = DBRowUUID.buildUID(eventName.toLowerCase(), domainClassGuid);
		}

		@Override
		public String get() {
			return uuid;
		}
	}

	public static class DataItemUID implements DBUUID {
		private final String uuid;

		public DataItemUID(final String domainClassGuid, final String dataItemName) {
			super();
			this.uuid = DBRowUUID.buildUID(dataItemName.toLowerCase(), domainClassGuid);
		}

		@Override
		public String get() {
			return uuid;
		}
	}

	public static class ScriptTemplateUID implements DBUUID {
		private final String uuid;

		public ScriptTemplateUID(final String domainClassGuid, final String scriptTemplateName) {
			super();
			this.uuid = DBRowUUID.buildUID(scriptTemplateName.toLowerCase(), domainClassGuid);
		}

		@Override
		public String get() {
			return uuid;
		}

		public static String getDomainGuid(String dbUuid) {
			return StringUtils.isEmpty(dbUuid) ? null : dbUuid.split(SEPARATOR)[1];
		}
	}

	public static class TermDataUID implements DBUUID {
		private final String uuid;

		public TermDataUID(final String contractTypeGuid, final String tdName) {
			super();
			this.uuid = DBRowUUID.buildUID(tdName.toLowerCase(), contractTypeGuid);
		}

		@Override
		public String get() {
			return uuid;
		}
	}

	public static class TermEventUID implements DBUUID {
		private final String uuid;

		public TermEventUID(final String contractTypeGuid, final String teName) {
			super();
			this.uuid = DBRowUUID.buildUID(teName.toLowerCase(), contractTypeGuid);
		}

		@Override
		public String get() {
			return uuid;
		}
	}

	public static class TermEventTypeUID implements DBUUID {
		private final String uuid;

		public TermEventTypeUID(final String contractTypeGuid, final String tetName) {
			super();
			this.uuid = DBRowUUID.buildUID(tetName.toLowerCase(), contractTypeGuid);
		}

		@Override
		public String get() {
			return uuid;
		}
	}

	public static class TermActionUID implements DBUUID {
		private final String uuid;

		public TermActionUID(final String contractTypeGuid, final String taName) {
			super();
			this.uuid = DBRowUUID.buildUID(taName.toLowerCase(), contractTypeGuid);
		}

		@Override
		public String get() {
			return uuid;
		}
	}

	public static class TermActionTypeUID implements DBUUID {
		private final String uuid;

		public TermActionTypeUID(final String contractTypeGuid, final String tatName) {
			super();
			this.uuid = DBRowUUID.buildUID(tatName.toLowerCase(), contractTypeGuid);
		}

		@Override
		public String get() {
			return uuid;
		}
	}

	public static class TermServiceUID implements DBUUID {
		private final String uuid;

		public TermServiceUID(final String contractTypeGuid, final String tsName) {
			super();
			this.uuid = DBRowUUID.buildUID(tsName.toLowerCase(), contractTypeGuid);
		}

		@Override
		public String get() {
			return uuid;
		}
	}

	public static class TermMashupUID implements DBUUID {
		private final String uuid;

		public TermMashupUID(final String contractTypeGuid, final String tmuName) {
			super();
			this.uuid = DBRowUUID.buildUID(tmuName.toLowerCase(), contractTypeGuid);
		}

		@Override
		public String get() {
			return uuid;
		}
	}

	public static class ContractTypeUID implements DBUUID {
		private final String uuid;

		public ContractTypeUID(final String domainClassGuid, final String contractTypeName) {
			super();
			this.uuid = DBRowUUID.buildUID(contractTypeName.toLowerCase(), domainClassGuid);
		}

		@Override
		public String get() {
			return uuid;
		}
	}

	public static class ContractUID implements DBUUID {
		private final String uuid;

		public ContractUID(final String domainClassGuid, final String contractName) {
			super();
			this.uuid = DBRowUUID.buildUID(contractName.toLowerCase(), domainClassGuid);
		}

		@Override
		public String get() {
			return uuid;
		}

		public static String getDomainGuid(String dbUuid) {
			return StringUtils.isEmpty(dbUuid) ? null : dbUuid.split(SEPARATOR)[1];
		}
	}

	public static class AdminUnitUID implements DBUUID {
		private final String uuid;

		public AdminUnitUID(final String domainClassGuid, final String aduName) {
			super();
			this.uuid = DBRowUUID.buildUID(aduName.toLowerCase(), domainClassGuid);
		}

		@Override
		public String get() {
			return uuid;
		}
	}

	public static class ConfigItemUID implements DBUUID {
		private final String uuid;

		public ConfigItemUID(final String domainClassGuid, final String configItemName) {
			super();
			this.uuid = DBRowUUID.buildUID(configItemName.toLowerCase(), domainClassGuid);
		}

		@Override
		public String get() {
			return uuid;
		}
	}

	public static class EventTypeUID implements DBUUID {
		private final String uuid;

		public EventTypeUID(final String domainClassGuid, final String eventTypeName) {
			super();
			this.uuid = DBRowUUID.buildUID(eventTypeName.toLowerCase(), domainClassGuid);
		}

		@Override
		public String get() {
			return uuid;
		}
	}
}
