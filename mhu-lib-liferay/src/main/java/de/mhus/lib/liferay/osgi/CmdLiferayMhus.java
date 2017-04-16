package de.mhus.lib.liferay.osgi;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import com.liferay.counter.kernel.service.CounterLocalServiceUtil;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManagerUtil;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserGroupRoleServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.service.UserServiceUtil;
import com.liferay.portal.kernel.service.VirtualHostLocalServiceUtil;
import com.liferay.portal.kernel.service.persistence.UserGroupRoleUtil;

import de.mhus.lib.core.MCast;
import de.mhus.lib.core.console.ConsoleTable;

public class CmdLiferayMhus implements CommandProvider {

	@Override
	public String getHelp() {
		return "clc - Clear Liferay Cache\n"
				+ "setpassword - Set New Password <virtual host> <user> <new password> [<need user reset>]";
	}

//	Role role = RoleLocalServiceUtil.getRole(company.getCompanyId(), company.getAdminName());
//	User admin = company.getDefaultUser();
//	PermissionChecker pc = PermissionCheckerFactoryUtil.getPermissionCheckerFactory().create(admin);
//	PermissionThreadLocal.setPermissionChecker(pc);
	
	public Object _clc(CommandInterpreter intp) {
		intp.println("Clear Liferay Caches");
        CacheRegistryUtil.clear();
	    EntityCacheUtil.clearCache();
	    FinderCacheUtil.clearCache();
		return null;
	}
	
	public Object _virtualhosts(CommandInterpreter ci) throws PortalException {
		ConsoleTable out = new ConsoleTable();
		out.setHeaderValues("Id","Hostname","Company Id", "Company Name", "Company Admin");
		for (VirtualHost vh : VirtualHostLocalServiceUtil.getVirtualHosts(0, VirtualHostLocalServiceUtil.getVirtualHostsCount() ) ) {
			long companyId = vh.getCompanyId();
			Company company = CompanyServiceUtil.getCompanyById(companyId);
			out.addRowValues(vh.getVirtualHostId(), vh.getHostname(), companyId, company.getName(), company.getAdminName());
		}
		ci.println(out);
		return null;
		
	}
	
	public Object _users(CommandInterpreter ci) throws Exception {

		String virtualHost = ci.nextArgument();
		Company company = CompanyServiceUtil.getCompanyByVirtualHost(virtualHost);
		long companyId = company.getCompanyId();
		ci.println("6");
		
		ConsoleTable out = new ConsoleTable();
		out.setHeaderValues("Id","Screen Name","EMail", "Full Name");
		for (User user : UserLocalServiceUtil.getCompanyUsers(companyId, 0, UserLocalServiceUtil.getCompanyUsersCount(companyId))) {
			out.addRowValues(user.getUserId(), user.getScreenName(), user.getEmailAddress(), user.getFullName());
		}
		
		ci.println(out);
		return null;
		
	}
	
	public Object _setpassword(CommandInterpreter ci) throws PortalException {
		try {
			String virtualHost = ci.nextArgument();
			String screenName = ci.nextArgument();
			String pw = ci.nextArgument();
			String reset = ci.nextArgument();
			
			Company company = CompanyServiceUtil.getCompanyByVirtualHost(virtualHost);
			User user = UserLocalServiceUtil.getUserByScreenName(company.getCompanyId(), screenName);
			UserLocalServiceUtil.updatePassword(user.getUserId(), pw, pw, MCast.toboolean(reset, false), true);
			return null;
		} catch (Throwable t) {
			ci.printStackTrace(t);
			throw t;
		}
	}
	
	public Object _roles(CommandInterpreter ci) throws PortalException {
		String virtualHost = ci.nextArgument();
		Company company = CompanyServiceUtil.getCompanyByVirtualHost(virtualHost);
		
		ConsoleTable out = new ConsoleTable();
		out.setHeaderValues("Id","Name","Type","Title", "User Id", "User Name");
		for (Role role : RoleLocalServiceUtil.getRoles(company.getCompanyId())) {
			out.addRowValues(role.getRoleId(), role.getName(), role.getTypeLabel(),role.getTitle(), role.getUserId(), role.getUserName());
		}
		ci.println(out);
		return null;
	}
	
	public Object _role_delete(CommandInterpreter ci) throws PortalException {
		long id = MCast.tolong(ci.nextArgument(), -1);
		if (id < 0) return null;
		Role role = RoleLocalServiceUtil.deleteRole(id);
		ci.println("Deleted Role " + role.getRoleId() + " " + role.getName());
		return role;
	}
	
	public Object _user_delete(CommandInterpreter ci) throws PortalException {
		long id = MCast.tolong(ci.nextArgument(), -1);
		if (id < 0) return null;
		User user = UserLocalServiceUtil.deleteUser(id);
		ci.println("Deleted User " + user.getUserId() + " " + user.getScreenName());
		return user;
	}
	
	public Object _user_add(CommandInterpreter ci) throws PortalException {
		try {
			String virtualHost = ci.nextArgument();
			Company company = CompanyServiceUtil.getCompanyByVirtualHost(virtualHost);
			String screenName = ci.nextArgument();
			String email = ci.nextArgument();
			String firstName = ci.nextArgument();
			String lastName = ci.nextArgument();
	
			
			User user = UserLocalServiceUtil.addUser(
					0,
					company.getCompanyId(),
					false,
					"asd", "asd", 
					false,
					screenName, 
					email,
					0,
					null,
					Locale.GERMAN, 
					firstName,
					"",
					lastName,
					0,
					0,
					true,
					1, 1, 1970, 
					"", 
					new long[0], 
					new long[0], 
					new long[0], 
					new long[0], 
					false, 
					null
					);
			
			return user;
		} catch (Throwable t) {
			ci.printStackTrace(t);
			throw t;
		}
	}

	public Object _role_add(CommandInterpreter ci) throws PortalException {
		String virtualHost = ci.nextArgument();
		Company company = CompanyServiceUtil.getCompanyByVirtualHost(virtualHost);
		String userName = ci.nextArgument();
		
		User user = UserLocalServiceUtil.getUserByScreenName(company.getCompanyId(), userName);
		
		String name = ci.nextArgument();
		Role role = RoleLocalServiceUtil.addRole(user.getUserId(), null, 0, name, null, null, 0, null, null);
		return role;
	}
	
	public Object _db_table_list(CommandInterpreter ci) throws SQLException {
		Connection con = DataAccess.getConnection();
		DatabaseMetaData meta = con.getMetaData();
		ResultSet res = meta.getTables(null, null, null, new String[] {"TABLE"});
		ConsoleTable out = ConsoleTable.fromJdbcResult(res);
		ci.println(out);
		return null;
	}
	
	public Object _db_index_list(CommandInterpreter ci) throws SQLException {
		String idxName = ci.nextArgument();
		Connection con = DataAccess.getConnection();
		DatabaseMetaData meta = con.getMetaData();
		ResultSet tres = meta.getTables(null, null, null, new String[] {"TABLE"});
		ConsoleTable out = null;
		while (tres.next()) {
			ResultSet res = meta.getIndexInfo(con.getCatalog(), null, tres.getString("TABLE_NAME"), true, false);
			ResultSetMetaData resMeta = res.getMetaData();
			if (out == null) {
				out = new ConsoleTable();
				String[] h = new String[resMeta.getColumnCount()];
				for (int i = 0; i < resMeta.getColumnCount(); i++)
					h[i] = resMeta.getColumnName(i+1);
				out.setHeaderValues(h);
			}
			while (res.next()) {
				if (idxName == null || idxName.equals(res.getString("INDEX_NAME"))) {
					List<String> r = out.addRow();
					for (int i = 0; i < resMeta.getColumnCount(); i++)
							r.add(String.valueOf(res.getObject(i+1)));
				}
			}
		}
		ci.println(out);
		return null;
		
	}
	
}