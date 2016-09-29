package de.mhus.lib.liferay;

import java.util.List;

import javax.portlet.PortletRequest;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

public class MLiferayUtil {

	public static final String ROLE_ADMINISTRATOR = "Administrator";
	public static final String ROLE_GUEST = "Guest";
	public static final String ROLE_OWNER = "Owner";
	public static final String ROLE_POWER_USER = "Power User";
	public static final String ROLE_SITE_MEMBER = "Site Member";
	public static final String ROLE_SITE_OWNER = "Site Owner";
	public static final String ROLE_SITE_ADMINISTRATOR = "Site Administrator";
	public static final String ROLE_SITE_CONTENT_REVIEWER = "Site Content Reviewer";
	public static final String ROLE_PORTAL_CONTENT_REVIEWER = "Portal Content Reviewer";
	public static final String ROLE_USER = "User";
	public static final String ROLE_ORGANIZATION_OWNER = "Organization Owner";
	public static final String ROLE_ORGANIZATION_USER = "Organization User";
	public static final String ROLE_ORGANIZATION_ADMINISTRATOR = "Organization Administrator";
	public static final String ROLE_ORGANIZATION_CONTENT_REVIEWER = "Organization Content Reviewer";

	/**
	 * Returns true if the use is member of the given role name.
	 * 
	 * @param user user or null. If null it will return false
	 * @param role Role name or null. If null it will return false'
	 * @return
	 * @throws SystemException
	 */
	public static boolean hasRole(User user, String role) throws SystemException {
		if (user == null || role == null) return false;
		for (Role r : user.getRoles()) {
			if (r.getName().equals(role))
				return true;
		}
		return false;
	}

	/**
	 * Returns true if the user is member in one of this roles.
	 * 
	 * @param user
	 * @param roles
	 * @return
	 * @throws SystemException
	 */
	public static boolean hasRole(User user, List<String> roles) throws SystemException {
		if (user == null || roles == null) return false;
		for (Role r : user.getRoles()) {
			for (String role : roles) {
				if (role != null && r.getName().equals(role))
					return true;
			}
		}
		return false;
	}
	
	
	public static String getWebUrlForFriendlyUrl(PortletRequest request, String friendly, String attributes) {
		String url = null;
		
		if (friendly == null)
			friendly = "/";
		else
		if (!friendly.startsWith("/"))
			friendly = "/" + friendly;
		
		if (request != null) {
			try {
				ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
				long groupId = getCompanyGroupId(request);
				Layout childLayout = LayoutLocalServiceUtil.getFriendlyURLLayout(groupId, false, friendly);
				url = PortalUtil.getLayoutURL(childLayout, themeDisplay);
			} catch (Exception e) {}
		}
		
		if (url == null) {
			url = "/web/guest" + friendly;
		}
		
		if (attributes != null)
			url =url + (url.indexOf('?') > 0 ? "&" : "?") + attributes;
			
		return url;
		
	}

	public static long getCompanyGroupId(PortletRequest request) {
		// TODO Auto-generated method stub
//		User user = PortalUtil.getUser(request);
//		long companyId = user.getCompanyId();
//		Company company = CompanyLocalServiceUtil.getCompany(companyId);
//		company.getGroupId();
		return 10181;
	}

}
