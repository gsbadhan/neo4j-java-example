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
import org.digi.lg.neo4j.core.ScriptTemplateScope;
import org.digi.lg.neo4j.core.Vertex;
import org.digi.lg.neo4j.pojo.model.Script;

public interface ScriptDao {
	<TRX> Script save(TRX trx, Map<String, Object> params);

	<TRX> Script getByGuid(TRX trx, String guid);

	<TRX> Script getByName(TRX trx, String guid);

	<TRX> Edge scriptToScriptTemplate(TRX trx, String scriptGuid, String scriptTemplateGuid);

	<TRX> Edge scriptHasEvents(TRX trx, String scriptGuid, String eventsGuid);

	<TRX> Edge scriptHasConfigItem(TRX trx, String scriptGuid, String configItemGuid);

	<TRX> Edge scriptHasContract(TRX trx, String scriptGuid, String contractGuid);

	<TRX> Edge scriptBelongsAdu(TRX trx, String scriptGuid, String aduGuid);

	<TRX> Edge scriptTriggerDataItem(TRX trx, String scriptGuid, String dataItemGuid);

	<TRX> Edge scriptHasDS(TRX trx, String scriptGuid, String dsGuid);

	<TRX> List<Vertex> getScriptsByAduGuid(TRX trx, String aduGuid);

	<TRX> List<Vertex> getScriptConfigItems(TRX trx, String scriptGuid);

	<TRX> List<Vertex> getScriptAssets(TRX trx, String scriptGuid, ScriptTemplateScope scope);

	<TRX> Edge scriptHasCongigItem(TRX trx, String scriptGuid, String configItemLabel, String configItemGuid,
			String configValue);

	<TRX> Edge scriptAppliestoAsset(TRX trx, String scriptGuid, String assetOrClassLabel, String assetOrClassGuid);

	<TRX> List<Vertex> getScriptDataItems(TRX trx, String scriptGuid);

	<TRX> List<Vertex> getScriptEvents(TRX trx, String scriptGuid);

	<TRX> long countScriptTemplateInstance(TRX trx, String scriptTemplateGuid, String assetGuid, String astLabel);

	<TRX> List<Vertex> getScriptAdminUnits(TRX trx, String scriptGuid);

	<TRX> List<Vertex> getOtherScripts(TRX trx, String scriptGuid);

	<TRX> List<Vertex> getScriptsByScriptTemplate(TRX trx, String scriptTemplateGuid);

	<TRX> List<Vertex> getScriptAppliesToAssets(TRX trx, String scriptGuid);

	<TRX> List<Vertex> getScriptTemplateInstance(TRX trx, String scriptTemplateGuid, String assetGuid, String astLabel);

	<TRX> Edge deleteLink(TRX trx, String srcLabel, String srcGuid, String destLabel, String destGuid,
			Relationship relType, Direction direction);

	<TRX> Edge createLink(TRX trx, String srcLabel, String destLabel, Relationship relType, Map<String, Object> params);

}
