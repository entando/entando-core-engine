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
package com.agiletec.apsadmin.user;

/**
 * Classe action delegata alla gestione delle operazioni di associazione 
 * delle autorizzazioni agli utenti del sistema.
 * @author E.Mezzano - E.Santoboni
 * @deprecated From Entando 3.3.1, use org.entando.entando.apsadmin.user.AuthorityToUsersAction
 */
public class AuthorityToUsersAction extends org.entando.entando.apsadmin.user.AuthorityToUsersAction implements IAuthorityToUsersAction {
	/*
	@Override
	public String addUser() {
		IApsAuthority auth = this.getApsAuthority();
		try {
			if (SystemConstants.ADMIN_USER_NAME.equals(this.getUsername())) {
				this.addActionError(this.getText("error.user.cannotModifyAdminUser"));
				return INPUT;
			}
			UserDetails user = this.getUser();
			if (user != null && !this.hasUserAuthority()) {
				IApsAuthorityManager authorizatorManager = this.getAuthorizatorManager();
				authorizatorManager.setUserAuthorization(this.getUsername(), auth);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "addUser");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public String removeUser() {
		IApsAuthority auth = this.getApsAuthority();
		try {
			if (SystemConstants.ADMIN_USER_NAME.equals(this.getUsername())) {
				this.addActionError(this.getText("error.user.cannotModifyAdminUser"));
				return INPUT;
			}
			UserDetails user = this.getUser();
			if (user != null) {
				IApsAuthorityManager authorizatorManager = this.getAuthorizatorManager();
				authorizatorManager.removeUserAuthorization(this.getUsername(), auth);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "removeUser");
			return FAILURE;
		}
		return SUCCESS;
	}
	
	@Override
	public IApsAuthority getApsAuthority() {
		String authName = this.getAuthName();
		IApsAuthority authority = this.getAuthorizatorManager().getAuthority(authName);
		return authority;
	}
	
	public List<UserDetails> getAuthorizedUsers() {
		IApsAuthority auth = this.getApsAuthority();
		try {
			return this.getAuthorizatorManager().getUsersByAuthority(auth);
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, this, "getUserAuthorizated");
			throw new RuntimeException("Errore in ricerca utenti autorizzati", t);
		}
	}
	
	protected UserDetails getUser() throws ApsSystemException {
		String username = this.getUsername();
		UserDetails user = null;
		if (username!=null && username.trim().length()>=0) {
			user = this.getUserManager().getUser(username);
		}
		return user;
	}
	
	//TODO TROVARE IL MODO DI ELIMINARE QUESTO METODO
	protected boolean hasUserAuthority() throws ApsSystemException {
		String username = this.getUsername();
		List<UserDetails> users = this.getAuthorizatorManager().getUsersByAuthority(this.getApsAuthority());
		Iterator<UserDetails> usersIter = users.iterator();
		while (usersIter.hasNext()) {
			UserDetails currentUser = usersIter.next();
			if (currentUser.getUsername().equals(username)) {
				return true;
			}
		}
		return false;
	}
	
	public String getAuthName() {
		return _authName;
	}
	public void setAuthName(String authName) {
		this._authName = authName;
	}
	
	public String getUsername() {
		return _username;
	}
	public void setUsername(String username) {
		this._username = username;
	}
	
	protected IApsAuthorityManager getAuthorizatorManager() {
		return _authorizatorManager;
	}
	public void setAuthorizatorManager(IApsAuthorityManager authorizatorManager) {
		this._authorizatorManager = authorizatorManager;
	}
	
	private String _authName;
	private String _username;
	private IApsAuthorityManager _authorizatorManager;
	*/
}