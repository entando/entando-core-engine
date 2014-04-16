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
package com.agiletec.apsadmin.portal;

import com.agiletec.aps.system.common.FieldSearchFilter;
import com.agiletec.aps.system.exception.ApsSystemException;
import java.util.List;

import org.entando.entando.aps.system.services.widgettype.WidgetType;
import org.entando.entando.aps.system.services.widgettype.WidgetTypeParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.agiletec.aps.system.services.group.Group;
import com.agiletec.aps.system.services.page.IPage;
import com.agiletec.aps.system.services.page.Widget;
import com.agiletec.aps.system.services.role.Permission;
import com.agiletec.aps.util.ApsProperties;
import com.agiletec.apsadmin.system.ApsAdminSystemConstants;

import org.apache.commons.lang.StringUtils;
import org.entando.entando.aps.system.services.guifragment.GuiFragment;
import org.entando.entando.aps.system.services.guifragment.IGuiFragmentManager;

/**
 * @author E.Santoboni
 */
public class WidgetTypeAction extends AbstractPortalAction implements IWidgetTypeAction {

	private static final Logger _logger = LoggerFactory.getLogger(WidgetTypeAction.class);
	
	@Override
	public void validate() {
		super.validate();
		if (this.getStrutsAction() == ApsAdminSystemConstants.EDIT) return;
		try {
			if (this.getStrutsAction() == ApsAdminSystemConstants.PASTE) {
				this.checkWidgetToCopy();
			} else if (this.getStrutsAction() == ApsAdminSystemConstants.ADD) {
				this.checkNewWidget();
			}
		} catch (Throwable t) {
			_logger.error("error in validate", t);
			//ApsSystemUtils.logThrowable(t, this, "validate");
			throw new RuntimeException(t);
		}
	}
	
	/**
	 * @deprecated Use {@link #newUserWidget()} instead
	 */
	@Override
	public String newUserShowlet() {
		return newUserWidget();
	}

	@Override
	public String newUserWidget() {
		try {
			String check = this.checkNewWidget();
			if (null != check) return check;
			this.setStrutsAction(ApsAdminSystemConstants.ADD);
			this.setMainGroup(Group.FREE_GROUP_NAME);
		} catch (Throwable t) {
			_logger.error("error in newUserWidget", t);
			//ApsSystemUtils.logThrowable(t, this, "newUserWidget");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String copy() {
		try {
			String check = this.checkWidgetToCopy();
			if (null != check) return check;
			this.setStrutsAction(ApsAdminSystemConstants.PASTE);
			this.setMainGroup(Group.FREE_GROUP_NAME);
		} catch (Throwable t) {
			_logger.error("error in copy", t);
			//ApsSystemUtils.logThrowable(t, this, "copy");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String save() {
		try {
			if (this.getStrutsAction() != ApsAdminSystemConstants.EDIT) {
				if (!this.hasCurrentUserPermission(Permission.SUPERUSER)) {
					return USER_NOT_ALLOWED;
				}
				return this.saveUserWidget();
			}
			String check = this.checkWidgetType();
			if (null != check) return check;
			ApsProperties titles = new ApsProperties();
			titles.put("it", this.getItalianTitle());
			titles.put("en", this.getEnglishTitle());
			WidgetType type = this.getWidgetTypeManager().getWidgetType(this.getWidgetTypeCode());
			String mainGroupToSet = (this.hasCurrentUserPermission(Permission.SUPERUSER)) ? this.getMainGroup() : type.getMainGroup();
			ApsProperties configToSet = type.getConfig();
			if (type.isLogic() && type.isUserType() && !type.isLocked() && this.hasCurrentUserPermission(Permission.SUPERUSER)) {
				configToSet = this.extractWidgetTypeConfig(type.getParentType().getTypeParameters());
			}
			this.getWidgetTypeManager().updateWidgetType(this.getWidgetTypeCode(), titles, configToSet, mainGroupToSet);
			if (!type.isLogic()) {
				GuiFragment guiFragment = this.extractGuiFragment(this.getWidgetTypeCode());
				if (StringUtils.isNotBlank(this.getGui())) {
					if (null == guiFragment) {
						guiFragment = new GuiFragment();
						guiFragment.setCode(this.getWidgetTypeCode());
						guiFragment.setPluginCode(type.getPluginCode());
						guiFragment.setGui(this.getGui());
						guiFragment.setWidgetCode(this.getWidgetTypeCode());
						this.getGuiFragmentManager().addGuiFragment(guiFragment);
					} else {
						guiFragment.setGui(this.getGui());
						this.getGuiFragmentManager().updateGuiFragment(guiFragment);
					}
				} else {
					if (null != guiFragment) {
						this.getGuiFragmentManager().deleteGuiFragment(guiFragment.getId());
					}
				}
			}
		} catch (Throwable t) {
			_logger.error("error in save", t);
			//ApsSystemUtils.logThrowable(t, this, "save");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Deprecated
	protected String saveUserShowlet() {
		return this.saveUserWidget();
	}
	
	protected String saveUserWidget() {
		try {
			boolean isCopy = (null != this.getPageCode() && this.getPageCode().trim().length() > 0);
			String check = (isCopy) ? this.checkWidgetToCopy() : this.checkNewWidget();
			if (null != check) return check;
			WidgetType newType = null;
			Widget widgetToCopy = this.extractWidgetToCopy();
			if (null == widgetToCopy) {
				this.setReplaceOnPage(false);
				newType = this.createNewWidgetType();
				WidgetType parentType = this.getWidgetTypeManager().getWidgetType(this.getParentWidgetTypeCode());
				newType.setParentType(parentType);
				ApsProperties config = this.extractWidgetTypeConfig(parentType.getTypeParameters());
				newType.setConfig(config);
			} else {
				newType = this.createCopiedWidget(widgetToCopy);
			}
			
			//TODO CHECK MainGroup
			newType.setMainGroup(this.getMainGroup());
			
			this.getWidgetTypeManager().addWidgetType(newType);
			if (this.isReplaceOnPage()) {
				WidgetType type = this.getWidgetType(this.getWidgetTypeCode());
				Widget widget = new Widget();
				widget.setType(type);
				IPage page = this.getPageManager().getPage(this.getPageCode());
				page.getWidgets()[this.getFramePos()] = widget;
				this.getPageManager().updatePage(page);
				return "replaceOnPage";
			}
		} catch (Throwable t) {
			_logger.error("error in saveUserWidget", t);
			//ApsSystemUtils.logThrowable(t, this, "saveUserWidget");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	private Widget extractWidgetToCopy() throws Throwable {
		IPage page = this.getPageManager().getPage(this.getPageCode());
		if (null == page) return null;
		Widget[] widgets = page.getWidgets();
		Widget widget = widgets[this.getFramePos()];
		return widget;
	}
	
	private String checkNewWidget() throws Throwable {
		WidgetType parentType = this.getWidgetTypeManager().getWidgetType(this.getParentWidgetTypeCode());
		if (null == parentType) {
			this.addActionError(this.getText("error.widgetType.invalid.null", new String[]{this.getParentWidgetTypeCode()}));
			return "inputShowletTypes";
		}
		if (null == parentType.getTypeParameters() || parentType.getTypeParameters().isEmpty()) {
			this.addActionError(this.getText("error.widgetType.invalid.typeWithNoParameters", new String[]{this.getParentWidgetTypeCode()}));
			return "inputShowletTypes";
		}
		return null;
	}
	
	private String checkWidgetToCopy() throws Throwable {
		IPage page = this.getPageManager().getPage(this.getPageCode());
		if (null == page) {
			this.addActionError(this.getText("error.page.invalidPageCode.adv", 
					new String[]{this.getPageCode()}));
			return "inputShowletTypes";
		}
		if (!this.getAuthorizationManager().isAuth(this.getCurrentUser(), page)) {
			this.addActionError(this.getText("error.page.userNotAllowed.adv", 
					new String[]{this.getPageCode()}));
			return "inputShowletTypes";
		}
		Widget[] widgets = page.getWidgets();
		if (null == this.getFramePos() || widgets.length <= this.getFramePos()) {
			String framePos = (null != this.getFramePos()) ? this.getFramePos().toString() : null;
			this.addActionError(this.getText("error.page.invalidPageFrame.adv", 
					new String[]{this.getPageCode(), framePos}));
			return "inputShowletTypes";
		}
		Widget widget = widgets[this.getFramePos()];
		if (null == widget) {
			this.addActionError(this.getText("error.page.nullWidgetOnFrame", 
					new String[]{this.getPageCode(), this.getFramePos().toString()}));
			return "inputShowletTypes";
		}
		this.setShowletToCopy(widget);
		return null;
	}
	
	private WidgetType createNewWidgetType() {
		WidgetType type = new WidgetType();
		type.setCode(this.getWidgetTypeCode());
		ApsProperties titles = new ApsProperties();
		titles.setProperty("it", this.getItalianTitle());
		titles.setProperty("en", this.getEnglishTitle());
		type.setTitles(titles);
		type.setLocked(false);
		return type;
	}
	
	private WidgetType createCopiedWidget(Widget widgetToCopy) {
		WidgetType type = this.createNewWidgetType();
		WidgetType parentType = widgetToCopy.getType();
		type.setParentType(parentType);
		type.setConfig(widgetToCopy.getConfig());
		return type;
	}
	
	private ApsProperties extractWidgetTypeConfig(List<WidgetTypeParameter> parameters) throws Exception {
		ApsProperties config = new ApsProperties();
		for (int i=0; i<parameters.size(); i++) {
			WidgetTypeParameter param = parameters.get(i);
			String paramName = param.getName();
			String value = this.getRequest().getParameter(paramName);
			if (value != null && value.trim().length()>0) {
				config.setProperty(paramName, value);
			}
		}
		return config;
	}
	
	@Override
	public String edit() {
		try {
			String check = this.checkWidgetType();
			if (null != check) return check;
			this.setStrutsAction(ApsAdminSystemConstants.EDIT);
			WidgetType type = this.getWidgetTypeManager().getWidgetType(this.getWidgetTypeCode());
			ApsProperties titles = type.getTitles();
			this.setItalianTitle(titles.getProperty("it"));
			this.setEnglishTitle(titles.getProperty("en"));
			this.setMainGroup(type.getMainGroup());
			GuiFragment guiFragment = this.extractGuiFragment(this.getWidgetTypeCode());
			if (null != guiFragment) {
				this.setGui(guiFragment.getGui());
			}
		} catch (Throwable t) {
			_logger.error("error in editWidgetTitles", t);
			//ApsSystemUtils.logThrowable(t, this, "editWidgetTitles");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	protected GuiFragment extractGuiFragment(String widgetTypeCode) throws ApsSystemException {
		FieldSearchFilter filter = new FieldSearchFilter("widgetcode", widgetTypeCode, false);
		FieldSearchFilter[] filters = {filter};
		List<Integer> ids = this.getGuiFragmentManager().searchGuiFragments(filters);
		if (null != ids && !ids.isEmpty()) {
			Object idObject = ids.get(0);
			Integer id = (idObject instanceof Integer)? (Integer) idObject : Integer.parseInt(idObject.toString());
			GuiFragment guiFragment = this.getGuiFragmentManager().getGuiFragment(id);
			return guiFragment;
		}
		return null;
	}
	
	private String checkWidgetType() {
		WidgetType type = this.getWidgetTypeManager().getWidgetType(this.getWidgetTypeCode());
		if (null == type) {
			this.addActionError(this.getText("error.widgetType.invalid.null", new String[]{this.getWidgetTypeCode()}));
			return "inputShowletTypes";
		}
		return null;
	}
	
	@Override
	public String trash() {
		try {
			String check = this.checkDeleteWidgetType();
			if (null != check) return check;
		} catch (Throwable t) {
			_logger.error("error in trash", t);
			//ApsSystemUtils.logThrowable(t, this, "trash");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String delete() {
		try {
			String check = this.checkDeleteWidgetType();
			if (null != check) return check;
			this.getWidgetTypeManager().deleteWidgetType(this.getWidgetTypeCode());
		} catch (Throwable t) {
			_logger.error("error in delete", t);
			//ApsSystemUtils.logThrowable(t, this, "delete");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	private String checkDeleteWidgetType() {
		try {
			String check = this.checkWidgetType();
			if (null != check) return check;
			WidgetType type = this.getWidgetTypeManager().getWidgetType(this.getWidgetTypeCode());
			if (type.isLocked()) {
				this.addActionError(this.getText("error.widgetType.locked.undeletable", new String[]{this.getWidgetTypeCode()}));
				return "inputShowletTypes";
			}
			List<IPage> utilizers = this.getPageManager().getWidgetUtilizers(this.getWidgetTypeCode());
			if (null != utilizers && utilizers.size() > 0) {
				this.addActionError(this.getText("error.widgetType.used.undeletable", new String[]{this.getWidgetTypeCode()}));
				return "inputShowletTypes";
			}
		} catch (Throwable t) {
			_logger.error("Error on checking delete operatione : widget type code {}",this.getWidgetTypeCode(), t);
			//ApsSystemUtils.logThrowable(t, this, "checkDeleteWidgetType");
			throw new RuntimeException("Error on checking delete operatione : widget type code " + this.getWidgetTypeCode(), t);
		}
		return null;
	}
	
	@Deprecated
	public WidgetType getShowletType(String code) {
		return this.getWidgetType(code);
	}
	
	public WidgetType getWidgetType(String code) {
		return this.getWidgetTypeManager().getWidgetType(code);
	}
	
	public Group getGroup(String groupCode) {
		Group group = super.getGroupManager().getGroup(groupCode);
		if (null == group) {
			group = super.getGroupManager().getGroup(Group.FREE_GROUP_NAME);
		}
		return group;
	}
	
	public List<Group> getGroups() {
		return this.getGroupManager().getGroups();
	}
	
	public int getStrutsAction() {
		return _strutsAction;
	}
	public void setStrutsAction(int strutsAction) {
		this._strutsAction = strutsAction;
	}
	
	@Deprecated
	public String getShowletTypeCode() {
		return this.getWidgetTypeCode();
	}
	@Deprecated
	public void setShowletTypeCode(String widgetTypeCode) {
		this.setWidgetTypeCode(widgetTypeCode);
	}
	
	public String getEnglishTitle() {
		return _englishTitle;
	}
	public void setEnglishTitle(String englishTitle) {
		this._englishTitle = englishTitle;
	}
	
	public String getItalianTitle() {
		return _italianTitle;
	}
	public void setItalianTitle(String italianTitle) {
		this._italianTitle = italianTitle;
	}
	
	public String getMainGroup() {
		return _mainGroup;
	}
	public void setMainGroup(String mainGroup) {
		this._mainGroup = mainGroup;
	}
	
	@Deprecated
	public String getParentShowletTypeCode() {
		return this.getParentWidgetTypeCode();
	}
	@Deprecated
	public void setParentShowletTypeCode(String parentWidgetTypeCode) {
		this.setParentWidgetTypeCode(parentWidgetTypeCode);
	}
	
	public String getParentWidgetTypeCode() {
		return _parentWidgetTypeCode;
	}
	public void setParentWidgetTypeCode(String parentWidgetTypeCode) {
		this._parentWidgetTypeCode = parentWidgetTypeCode;
	}
	
	public String getGui() {
		return _gui;
	}
	public void setGui(String gui) {
		this._gui = gui;
	}
	
	public String getPageCode() {
		return _pageCode;
	}
	public void setPageCode(String pageCode) {
		this._pageCode = pageCode;
	}
	
	public Integer getFramePos() {
		return _framePos;
	}
	public void setFramePos(Integer framePos) {
		this._framePos = framePos;
	}
	
	@Deprecated
	public Widget getShowletToCopy() {
		return this.getWidgetToCopy();
	}
	@Deprecated
	public void setShowletToCopy(Widget showletToCopy) {
		this.setWidgetToCopy(showletToCopy);
	}
	
	public Widget getWidgetToCopy() {
		return _widgetToCopy;
	}
	public void setWidgetToCopy(Widget widgetToCopy) {
		this._widgetToCopy = widgetToCopy;
	}
	
	public boolean isReplaceOnPage() {
		return _replaceOnPage;
	}
	public void setReplaceOnPage(boolean replaceOnPage) {
		this._replaceOnPage = replaceOnPage;
	}
	
	public String getWidgetTypeCode() {
		return _widgetTypeCode;
	}
	public void setWidgetTypeCode(String widgetTypeCode) {
		this._widgetTypeCode = widgetTypeCode;
	}
	
	protected IGuiFragmentManager getGuiFragmentManager() {
		return _guiFragmentManager;
	}
	public void setGuiFragmentManager(IGuiFragmentManager guiFragmentManager) {
		this._guiFragmentManager = guiFragmentManager;
	}
	
	private int _strutsAction;
	
	private String _widgetTypeCode;
	
	private String _englishTitle;
	private String _italianTitle;
	
	private String _mainGroup;
	
	private String _parentWidgetTypeCode;
	
	private String _gui;
	
	private String _pageCode;
	private Integer _framePos;
	//private Widget _showletToCopy;
	private Widget _widgetToCopy;
	
	private boolean _replaceOnPage;
	
	private IGuiFragmentManager _guiFragmentManager;
	
}