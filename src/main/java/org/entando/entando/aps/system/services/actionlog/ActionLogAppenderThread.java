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
package org.entando.entando.aps.system.services.actionlog;

import com.agiletec.aps.system.ApsSystemUtils;
import org.entando.entando.aps.system.services.actionlog.model.ActionLogRecord;

/**
 * @author E.Santoboni
 */
public class ActionLogAppenderThread extends Thread {
	
	public ActionLogAppenderThread(ActionLogRecord actionRecordToAdd, 
			ActionLogManager actionLogManager) {
		this._actionLogManager = actionLogManager;
		this._actionRecordToAdd = actionRecordToAdd;
	}
	
	@Override
	public void run() {
		try {
			this._actionLogManager.addActionRecordByThread(this._actionRecordToAdd);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "run");
		}
	}
	
	private ActionLogRecord _actionRecordToAdd;
	private ActionLogManager _actionLogManager;
	
}