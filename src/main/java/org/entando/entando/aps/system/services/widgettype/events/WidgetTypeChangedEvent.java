/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando software.
* Entando is a free software;
* You can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package org.entando.entando.aps.system.services.widgettype.events;

import com.agiletec.aps.system.common.IManager;
import com.agiletec.aps.system.common.notify.ApsEvent;

/**
 * @author E.Santoboni
 */
public class WidgetTypeChangedEvent extends ApsEvent {
	
	@Override
	public void notify(IManager srv) {
		((WidgetTypeChangedObserver) srv).updateFromShowletTypeChanged(this);
	}
	
	public Class getObserverInterface() {
		return WidgetTypeChangedObserver.class;
	}
	
	public String getShowletTypeCode() {
		return _showletTypeCode;
	}
	public void setShowletTypeCode(String showletTypeCode) {
		this._showletTypeCode = showletTypeCode;
	}
	
	private String _showletTypeCode;
	
}