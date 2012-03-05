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
package com.agiletec.aps.system.common.entity.event;

import com.agiletec.aps.system.common.notify.ObserverService;

/**
 * Interface for observers of events of Entity Types.
 * @author E.Santoboni
 */
public interface EntityTypesChangingObserver extends ObserverService {
	
	/**
	 * The method is invoked by the service observers.
	 * @param event The event of entity type changing.
	 */
	public void updateFromEntityTypesChanging(EntityTypesChangingEvent event);
	
}