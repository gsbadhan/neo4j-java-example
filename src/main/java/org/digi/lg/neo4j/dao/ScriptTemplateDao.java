/*******************************************************************************
 * Copyright (c) 2017  Wipro Digital. All rights reserved.
 * 
 *  Contributors:
 *      Wipro Digital - Looking Glass Team.
 *      
 *      
 *      May 5, 2017
 ******************************************************************************/
package org.digi.lg.neo4j.dao;

import java.util.List;
import java.util.Map;

import org.digi.lg.neo4j.core.Direction;
import org.digi.lg.neo4j.core.Edge;
import org.digi.lg.neo4j.core.Relationship;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.pojo.model.Script;
import org.digi.lg.neo4j.pojo.model.ScriptTemplate;

public interface ScriptTemplateDao {
	<TRX> ScriptTemplate save(TRX trx, Map<String, Object> params);

	<TRX> ScriptTemplate getByGuid(TRX trx, String guid);

	<TRX> Script getByName(TRX trx, String guid);

	<TRX> List<Vertex> getConfigItem(TRX trx, String scriptTemplateGuid);

	<TRX> List<Vertex> getEventType(TRX trx, String scriptTemplateGuid);

	<TRX> List<Vertex> getTriggerDataItem(TRX trx, String scriptTemplateGuid);

	<TRX> Edge scriptTemplateHasConfigItem(TRX trx, String scriptTemplateGuid, String configItemGuid);

	<TRX> Edge scriptTemplateGeneratesEventType(TRX trx, String scriptTemplateGuid, String eventTypeGuid);

	<TRX> Edge scriptTemplateTriggerDataItem(TRX trx, String scriptTemplateGuid, String dataItemGuid);

	<TRX> Edge scriptTemplateAppliesProductClass(TRX trx, String scriptTemplateGuid, String prdtClassGuid);

	<TRX> Edge scriptTemplateHasAdu(TRX trx, String scriptTemplateGuid, String aduGuid);

	<TRX> List<Vertex> getScriptTemplateInfoByAduGuid(TRX trx, String aduGuid);

	<TRX> List<Vertex> getScriptTemplateInfoByContractType(TRX trx, String contractTypeGuid);

	<TRX> ScriptTemplate getScriptTemplateByScript(TRX trx, String scriptGuid);

	<TRX> Edge scriptTemplateHasDS(TRX trx, String scriptTemplateGuid, String dsGuid);

	<TRX> Edge deleteLink(TRX trx, String srcLabel, String srcGuid, String destLabel, String destGuid,
			Relationship relType, Direction direction);

	<TRX> Edge createLink(TRX trx, String srcLabel, String destLabel, Relationship relType, Map<String, Object> params);

}
