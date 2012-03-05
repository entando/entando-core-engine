/*
*
* Copyright 2012 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software.
* Entando is a free software; 
* you can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2012 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package com.agiletec.plugins.jacms.apsadmin.content.attribute.action.resource;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.services.group.Group;
import com.agiletec.plugins.jacms.aps.system.services.content.model.Content;
import com.agiletec.plugins.jacms.aps.system.services.resource.model.ResourceInterface;
import com.agiletec.plugins.jacms.apsadmin.content.ContentActionConstants;
import com.agiletec.plugins.jacms.apsadmin.resource.ResourceFinderAction;

/**
 * Classe action a servizio della gestione attributi risorsa, 
 * estensione della action gestrice delle operazioni di ricerca risorse.
 * @author E.Santoboni
 */
public class ExtendedResourceFinderAction extends ResourceFinderAction {
	
	public String entryFindResource() {
		this.setCategoryCode(null);
		return SUCCESS;
	}
	
	@Override
	public List<String> getResources() throws Throwable {
		List<String> resourcesId = null;
		try {
			List<String> groupCodes = new ArrayList<String>();
			groupCodes.add(Group.FREE_GROUP_NAME);
			if (null != this.getContent().getMainGroup()) {
				groupCodes.add(this.getContent().getMainGroup());
			}
			resourcesId = this.getResourceManager().searchResourcesId(this.getResourceTypeCode(), 
					this.getText(), this.getCategoryCode(), groupCodes);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getResources");
			throw t;
		}
		return resourcesId;
	}
	
	/**
	 * Restituisce il contenuto in sesione.
	 * @return Il contenuto in sesione.
	 */
	public Content getContent() {
		return (Content) this.getRequest().getSession().getAttribute(ContentActionConstants.SESSION_PARAM_NAME_CURRENT_CONTENT);
	}
	
	/**
	 * Aggiunge una risorsa ad un Attributo.
	 * @return SUCCESS se è andato a buon fine, FAILURE in caso contrario
	 */
	public String joinResource() {
		try {
			String resourceId = this.getResourceId();
			ResourceInterface resource = this.getResourceManager().loadResource(resourceId);
			this.buildEntryContentAnchorDest();
			ResourceAttributeActionHelper.joinResource(resource, this.getRequest());
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "joinResource");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	private void buildEntryContentAnchorDest() {
		HttpSession session = this.getRequest().getSession();
		String anchorDest = ResourceAttributeActionHelper.buildEntryContentAnchorDest(session);
		this.setEntryContentAnchorDest(anchorDest);
	}
	
	public boolean isOnEditContent() {
		return true;
	}
	
	public String getResourceId() {
		return _resourceId;
	}
	public void setResourceId(String resourceId) {
		this._resourceId = resourceId;
	}
	
	public String getEntryContentAnchorDest() {
		if (null == this._entryContentAnchorDest) {
			this.buildEntryContentAnchorDest();
		}
		return _entryContentAnchorDest;
	}
	protected void setEntryContentAnchorDest(String entryContentAnchorDest) {
		this._entryContentAnchorDest = entryContentAnchorDest;
	}
	
	private String _resourceId;
	private String _entryContentAnchorDest;
	
}