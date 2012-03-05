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
package com.agiletec.aps.system.common.entity.model.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

import com.agiletec.aps.system.common.entity.model.IApsEntity;
import com.agiletec.aps.system.common.searchengine.IndexableAttributeInterface;
import com.agiletec.aps.system.exception.ApsSystemException;

/**
 * This class describes the Entity of a Composed Attribute.
 * This attribute is build by one or more elementary attributes of different types.
 * These elementary attributes can support multiple languages or not (and are defined
 * multi or mono-language).
 * The Composite Attribute is only utilized in conjunction with the 'Monolist' attribute. Please
 * note that the composite attribute cannot be used as an element of the "List" attribute since
 * the items in the List can support multiple languages.
 * @author E.Santoboni
 */
public class CompositeAttribute extends AbstractComplexAttribute {
    
    /**
     * Attribute initialization.
     */
    public CompositeAttribute() {
        this._attributeList = new ArrayList<AttributeInterface>();
        this._attributeMap = new HashMap<String, AttributeInterface>();
    }
    
    /**
     * Return the attribute prototype, that is an empty attribute.
     */
    public Object getAttributePrototype() {
        CompositeAttribute clone = (CompositeAttribute) super.getAttributePrototype();
        Iterator<AttributeInterface> iter = this.getAttributes().iterator();
        while (iter.hasNext()) {
            AttributeInterface attribute = iter.next();
            attribute.setParentEntity(this.getParentEntity());
            clone.addAttribute((AttributeInterface) attribute.getAttributePrototype());
        }
        return clone;
    }
    
    public void setRenderingLang(String langCode) {
        super.setRenderingLang(langCode);
        Iterator<AttributeInterface> iter = this.getAttributes().iterator();
        while (iter.hasNext()) {
            AttributeInterface attribute = iter.next();
            attribute.setRenderingLang(langCode);
        }
    }
    
    public void setDefaultLangCode(String langCode) {
        super.setDefaultLangCode(langCode);
        Iterator<AttributeInterface> iter = this.getAttributes().iterator();
        while (iter.hasNext()) {
            AttributeInterface attribute = iter.next();
            attribute.setDefaultLangCode(langCode);
        }
    }
    
    public Element getJDOMElement() {
        Element attributeElement = new Element("composite");
        attributeElement.setAttribute("name", this.getName());
        attributeElement.setAttribute("attributetype", this.getType());
        Iterator<AttributeInterface> iter = this.getAttributes().iterator();
        while (iter.hasNext()) {
            AttributeInterface attribute = iter.next();
            attributeElement.addContent(attribute.getJDOMElement());
        }
        return attributeElement;
    }
    
    public Element getJDOMConfigElement() {
        Element configElement = super.getJDOMConfigElement();
        Element attributesElement = new Element("attributes");
        configElement.addContent(attributesElement);
        List<AttributeInterface> attributes = this.getAttributes();
        for (int i = 0; i < attributes.size(); i++) {
            AttributeInterface attributeElement = attributes.get(i);
            Element subConfigElement = attributeElement.getJDOMConfigElement();
            attributesElement.addContent(subConfigElement);
        }
        return configElement;
    }
    
    /**
     * Return the attribute with the given name.
     * @param name The name of the requested attribute
     * @return The requested attribute.
     */
    public AttributeInterface getAttribute(String name) {
        AttributeInterface attribute = (AttributeInterface) this.getAttributeMap().get(name);
        return attribute;
    }
    
    /**
     * Add an attribute to the current Composite Attribute.
     * @param attribute The attribute to add.
     */
    protected void addAttribute(AttributeInterface attribute) {
        this.getAttributes().add(attribute);
        this.getAttributeMap().put(attribute.getName(), attribute);
    }
    
    public List<AttributeInterface> getAttributes() {
        return this._attributeList;
    }
    
    /**
     * Return the map of the elementary attributes.
     * @return The requested map
     */
    public Map<String, AttributeInterface> getAttributeMap() {
        return this._attributeMap;
    }
    
    public void setComplexAttributeConfig(Element attributeElement, Map<String, AttributeInterface> attrTypes) throws ApsSystemException {
        Element compositeAttributesElement = attributeElement.getChild("attributes");
        if (null == compositeAttributesElement) {
            this.setOldComplexAttributeConfig(attributeElement, attrTypes);
        } else {
            List<Element> compositeAttributeElements = compositeAttributesElement.getChildren();
            for (int j = 0; j < compositeAttributeElements.size(); j++) {
                Element currentAttrJdomElem = compositeAttributeElements.get(j);
                this.extractAttributeCompositeElement(attrTypes, currentAttrJdomElem);
            }
        }
    }
    
    @Deprecated(/** INSERTED to guaranted compatibility with previsous version of jAPS 2.0.12 */)
    private void setOldComplexAttributeConfig(Element attributeElement, Map<String, AttributeInterface> attrTypes) throws ApsSystemException {
        List<Element> attributeElements = attributeElement.getChildren();
        for (int j = 0; j < attributeElements.size(); j++) {
            Element currentAttrJdomElem = attributeElements.get(j);
            extractAttributeCompositeElement(attrTypes, currentAttrJdomElem);
        }
    }
    
    private void extractAttributeCompositeElement(Map<String, AttributeInterface> attrTypes, Element currentAttrJdomElem) throws ApsSystemException {
        String typeCode = this.extractXmlAttribute(currentAttrJdomElem, "attributetype", true);
        AttributeInterface compositeAttrElem = (AttributeInterface) attrTypes.get(typeCode);
        if (compositeAttrElem == null) {
            throw new ApsSystemException("The type " + typeCode
                    + " of the attribute element found in the tag <" + currentAttrJdomElem.getName() + "> of the composite attribute is not a valid one");
        }
        compositeAttrElem = (AttributeInterface) compositeAttrElem.getAttributePrototype();
        compositeAttrElem.setAttributeConfig(currentAttrJdomElem);
        compositeAttrElem.setSearcheable(false);
        compositeAttrElem.setDefaultLangCode(this.getDefaultLangCode());
        this.addAttribute(compositeAttrElem);
    }
    
    /**
     * Since this kind of attribute can never be indexable this method, which overrides
     * the one of the abstract class, always returns the constant "INDEXING_TYPE_NONE" 
     * (defined in AttributeInterface) which explicitly declares it not indexable.
     * Declaring indexable a complex attribute will make the contained element indexable.
     * @see com.agiletec.aps.system.common.entity.model.attribute.AttributeInterface#getIndexingType()
     */
    public String getIndexingType() {
        return IndexableAttributeInterface.INDEXING_TYPE_NONE;
    }
    
    public Object getRenderingAttributes() {
        return this.getAttributeMap();
    }
    
    public void setParentEntity(IApsEntity parentEntity) {
        super.setParentEntity(parentEntity);
        for (int i = 0; i < this.getAttributes().size(); i++) {
            AttributeInterface attributeElement = this.getAttributes().get(i);
            attributeElement.setParentEntity(parentEntity);
        }
    }
    
    public Object getValue() {
        return this.getAttributeMap();
    }
    
    protected Object getJAXBValue(String langCode) {
        List<DefaultJAXBAttribute> jaxrAttributes = new ArrayList<DefaultJAXBAttribute>();
        List<AttributeInterface> attributes = this.getAttributes();
        for (int i = 0; i < attributes.size(); i++) {
            AttributeInterface attribute = attributes.get(i);
            jaxrAttributes.add(attribute.getJAXBAttribute(langCode));
        }
        return jaxrAttributes;
    }
    
    public void valueFrom(DefaultJAXBAttribute jaxbAttribute) {
        super.valueFrom(jaxbAttribute);
        List<DefaultJAXBAttribute> value = (List<DefaultJAXBAttribute>) jaxbAttribute.getValue();
        if (null != value) {
            for (int i = 0; i < value.size(); i++) {
                DefaultJAXBAttribute jaxbAttributeElement = value.get(i);
                AttributeInterface attributeElement = this.getAttributeMap().get(jaxbAttributeElement.getName());
                if (null != attributeElement && null != jaxbAttributeElement 
                        && attributeElement.getType().equals(jaxbAttributeElement.getType())) {
                    attributeElement.valueFrom(jaxbAttributeElement);
                }
            }
        }
    }
    
    public JAXBCompositeAttributeType getJAXBAttributeType() {
        JAXBCompositeAttributeType jaxbAttribute = (JAXBCompositeAttributeType) super.getJAXBAttributeType();
        List<AttributeInterface> elements = this.getAttributes();
        if (null != elements) {
            for (int i = 0; i < elements.size(); i++) {
                AttributeInterface attributeElement = elements.get(i);
                jaxbAttribute.getElementTypes().add(attributeElement.getJAXBAttributeType());
            }
        }
        return jaxbAttribute;
    }
    
    protected DefaultJAXBAttributeType getJAXBAttributeTypeInstance() {
        return new JAXBCompositeAttributeType();
    }
    
    private List<AttributeInterface> _attributeList;
    private Map<String, AttributeInterface> _attributeMap;
    
}
