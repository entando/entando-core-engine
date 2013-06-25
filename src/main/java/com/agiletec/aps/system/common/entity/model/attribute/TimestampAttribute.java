/*
*
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
* This file is part of Entando Enterprise Edition software.
* You can redistribute it and/or modify it
* under the terms of the Entando's EULA
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2013 Entando S.r.l. (http://www.entando.com) All rights reserved.
*
*/
package com.agiletec.aps.system.common.entity.model.attribute;

import com.agiletec.aps.system.SystemConstants;
import com.agiletec.aps.system.common.entity.model.AttributeFieldError;
import com.agiletec.aps.system.common.entity.model.AttributeTracer;
import com.agiletec.aps.system.common.entity.model.FieldError;
import java.util.List;
import org.jdom.Element;

/**
 * @author E.Santoboni
 */
public class TimestampAttribute extends DateAttribute {
	
    @Override
    public Element getJDOMElement() {
        Element attributeElement = new Element("attribute");
        attributeElement.setAttribute("name", this.getName());
        attributeElement.setAttribute("attributetype", this.getType());
        if (null != this.getDate()) {
            Element dateElement = new Element("timestamp");
            dateElement.setText(this.getFormattedDate(SystemConstants.SYSTEM_TIMESTAMP_FORMAT));
            attributeElement.addContent(dateElement);
        }
        return attributeElement;
    }
	
    @Override
    public List<AttributeFieldError> validate(AttributeTracer tracer) {
        List<AttributeFieldError> errors = super.validate(tracer);
        if (null == this.getDate() || (
        		null != this.getFailedDateString() || 
        		null != this.getFailedHourString() || 
        		null != this.getFailedMinuteString() || 
        		null != this.getFailedSecondString())
        		) {
            errors.add(new AttributeFieldError(this, FieldError.INVALID_FORMAT, tracer));
        }
        return errors;
    }
	
	public String getFailedHourString() {
		return _failedHourString;
	}
	public void setFailedHourString(String failedHourString) {
		this._failedHourString = failedHourString;
	}
	
	public String getFailedMinuteString() {
		return _failedMinuteString;
	}
	public void setFailedMinuteString(String failedMinuteString) {
		this._failedMinuteString = failedMinuteString;
	}
	
	public String getFailedSecondString() {
		return _failedSecondString;
	}
	public void setFailedSecondString(String failedSecondString) {
		this._failedSecondString = failedSecondString;
	}

	private String _failedHourString;
    private String _failedMinuteString;
    private String _failedSecondString;
    
}