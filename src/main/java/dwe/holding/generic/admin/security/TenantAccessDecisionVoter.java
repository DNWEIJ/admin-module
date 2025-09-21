package dwe.holding.generic.admin.security;

import ch.qos.logback.core.joran.spi.HttpUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.lang.reflect.Field;
import java.util.Collection;

@Component
@Slf4j
public class TenantAccessDecisionVoter implements AccessDecisionVoter {


    protected static final String FILE_NAME_DELIMITER = "/";

    private static final String TENANT_ATTRIBUTE = "TENANT_AUTHORIZATION";
    private static final String NO_AUTHORIZATION_ATTRIBUTE = "NO_AUTHORIZATION_REQUIRED";
    private static final String FILE_NAME_CREATE = "create";
    private static final String FILE_NAME_READ = "search";
    private static final String FILE_NAME_EDIT = "edit";
    private static final String FILE_NAME_UPDATE = "update";
    private static final String FILE_NAME_DELETE = "delete";
    private static final String[] FILE_NAME_PREFIXES = new String[]{FILE_NAME_CREATE, FILE_NAME_READ, FILE_NAME_EDIT, FILE_NAME_UPDATE, FILE_NAME_DELETE};
    private static final String PARAM_EDIT_ID = "id";
    private static final String ATTRIBUTE_CREATE = "CREATE";
    private static final String ATTRIBUTE_READ = "READ";
    private static final String ATTRIBUTE_UPDATE = "UPDATE";
    private static final String ATTRIBUTE_DELETE = "DELETE";

    private static final String LIST = "list";
    private String[] useSubmitAttribute;

    /*
     * (non-Javadoc)
     *
     * @see
     * org.acegisecurity.vote.AccessDecisionVoter#supports(org.acegisecurity
     * .ConfigAttribute)
     */
    public boolean supports(ConfigAttribute attribute) {
        return attribute.getAttribute().equals(TENANT_ATTRIBUTE) || attribute.getAttribute().equals(NO_AUTHORIZATION_ATTRIBUTE);
    }

    public boolean supports(Class clazz) {
        return clazz.equals(FilterInvocation.class);
    }

    /**
     * The vote determines if access is granted: using url and submit button. The
     * derived technical function name is created using the url and submit
     * button.. The following steps are performed in sequence: - Check if
     * autorisation is needed - Check if the url is valid - Check if it is a
     * report
     */
    public int vote(Authentication authentication, Object object, Collection attributes) {
        // check for no security needed
        HttpServletRequest request = ((RequestAuthorizationContext) object).getRequest();
        String uri = request.getRequestURI();

        if (uri.compareToIgnoreCase(request.getContextPath()) == 0) {
            return ACCESS_GRANTED;
        }

/******************************************
 *  Request methods mapping:
 *  GET   ->   /entity                  <- Retrieve the create screen for the entity
 *  GET   ->   /entity/{1}              <- Retrieve the create screen for the entity
 *  GET   ->   /entity/{id}/entity      <- Retrieve the create screen for the entity as child of a parent
 *  GET   ->   /entity/list             <- Retrieve the list screen filled with elements or empty
 *  GET   ->   /entity/{id}/entity/list <- Retrieve the list screen filled with elements or empty
 *
 *  POST  ->   /entity                  _save     <- Save the newly created entity
 *  POST  ->   /entity/{id}/entity      _save     <- Save the newly created entity as child of a parent
 *  POST  ->   /entity                  _delete   <- Delete the entity
 *  POST  ->   /entity/{id}/entity      _delete   <- Delete the entity as child of a parent
 *
 ******************************************/

        HttpUtil.RequestMethod method = HttpUtil.RequestMethod.valueOf(request.getMethod());
        String last = getLastPart(uri);

        String authorizationAttribute = "";
        if (method.equals(HttpUtil.RequestMethod.GET)) {
            if (last.equalsIgnoreCase(LIST)) {
                authorizationAttribute = getNextToLastPart(uri, "/list") + "_" + ATTRIBUTE_READ;
            } else {
                authorizationAttribute = last + "_" + ATTRIBUTE_READ;
            }
        }
        if (method.equals(HttpUtil.RequestMethod.POST)) {
            if (request.getParameter(ButtonConstants.PARAM_SAVE_NEW) != null || request.getParameter(ButtonConstants.PARAM_SAVE) != null) {
                authorizationAttribute = last + "_" + ATTRIBUTE_CREATE;
            } else if (request.getParameter(ButtonConstants.PARAM_DELETE) != null) {
                authorizationAttribute = last + "_" + ATTRIBUTE_DELETE;
            }
        }

        return ACCESS_GRANTED;
//        String authorizationAttribute = "";
//        int fileNameStartIndex = uri.lastIndexOf(FILE_NAME_DELIMITER);
//        int fileNameEndIndex = uri.lastIndexOf(FILE_NAME_END_DELIMITER);
//        if (fileNameStartIndex != NOT_FOUND && fileNameEndIndex != NOT_FOUND && fileNameStartIndex < fileNameEndIndex && fileNameEndIndex < uri.length()) {
//            String fileName = uri.substring(fileNameStartIndex + 1, fileNameEndIndex);
//            // check if a report is called and if so validate if the user is granted for reports.
//            if (uri.contains(FILE_NAME_RAPPORTAGE)) {
//                authorizationAttribute = REPORT;
//            } else {
//                authorizationAttribute = getAuthorizationAttributePrefix(request, fileName) + getFileNameWithoutPrefixes(fileName);
//            }
//            int returnvalue = checkAuthorizationAttribute(authorizationAttribute, authentication.getAuthorities());
//            String returnvaluestring = " ACCESS_ABSTAIN ";
//            if (returnvalue == -1) {
//                returnvaluestring = " ACCESS_DENIED ";
//            }
//            if (returnvalue == 1) {
//                returnvaluestring = " ACCESS_GRANTED ";
//                WebAuthenticationDetails webdetails = null;
//                Object principal = authentication.getDetails();
//                if (principal instanceof WebAuthenticationDetails) {
//                    webdetails = (WebAuthenticationDetails) principal;
//                    try {
//                        LOG.info("TenantAccesDecisionVoter:: Voting:" + returnvaluestring + " |user" +
////                                AutorisationUtils.getCurrentUserMid() + "|" +
//                                 AutorisationUtils.getCurrentUserName() + " |prcssdUri=" + uri.toString() + "|authAttrd=" + authorizationAttribute.toString());
//                    } catch (ApplicationException e) {
//                        LOG.info("TenantAccesDecisionVoter:: SessionId:" + webdetails.getSessionId() + ":: Voting:" + returnvaluestring + " |prcssdUri=" + uri.toString() + "|authAttrd=" + authorizationAttribute.toString());
//                    }
//                }
//            }
//            if (returnvaluestring.compareTo(" ACCESS_DENIED ") == 0) {
//                OutputFunctionQuery(request, authorizationAttribute, returnvaluestring, uri, fileNameStartIndex, fileNameEndIndex);
//            }
//            return returnvalue;
//        }
//        LOG.info("TenantAccesDecisionVoter::" + " ACCESS_DENIED for uri=" + uri.toString() + "  authorizationAttribute=" + authorizationAttribute.toString());
//        OutputFunctionQuery(request, authorizationAttribute, " ACCESS_DENIED ", uri, fileNameStartIndex, fileNameEndIndex);
//        return ACCESS_DENIED;
    }

    private String getNextToLastPart(String uri, String endPart) {
        return getLastPart(uri.replace(endPart, ""));
    }

    private String getLastPart(String uri) {
        String lastPart = uri.substring(uri.lastIndexOf(FILE_NAME_DELIMITER) + 1);
        if (isInteger(lastPart)) {
            return getNextToLastPart(uri, lastPart);
        }
        return lastPart;
    }

    protected String getAuthorizationAttributePrefix(HttpServletRequest request, String fileName) {
        String submitButton = checkSubmitButtonSpecified(request);
        if (submitButton != null) {
            return submitButton;
        } else if ((fileName.startsWith(FILE_NAME_EDIT) || fileName.startsWith(FILE_NAME_CREATE)) && request.getParameter(PARAM_EDIT_ID) == null) {
            return ATTRIBUTE_CREATE;
        } else if (fileName.startsWith(FILE_NAME_UPDATE) || (fileName.startsWith(FILE_NAME_EDIT) && request.getParameter(PARAM_EDIT_ID) != null)) {
            return ATTRIBUTE_UPDATE;
            // indien het een gecombineerd scherm is: zoekscherm met
            // opslaan(save) button
        } else if (fileName.startsWith(FILE_NAME_READ)) {
            if (WebUtils.hasSubmitParameter(request, ButtonConstants.PARAM_SAVE) || WebUtils.hasSubmitParameter(request, ButtonConstants.PARAM_SAVE_NEW)) {
                return ATTRIBUTE_UPDATE;
            } else {
                return ATTRIBUTE_READ;
            }
        } else {
            return "";
        }
    }

    protected String getFileNameWithoutPrefixes(String fileName) {
        for (String prefix : FILE_NAME_PREFIXES) {
            if (fileName.startsWith(prefix)) {
                int startIndex = prefix.length();
                if (startIndex < fileName.length()) {
                    return fileName.substring(prefix.length());
                }
            }
        }
        return fileName;
    }

    private String checkSubmitButtonSpecified(HttpServletRequest request) {
        for (String submitAttribute : useSubmitAttribute) {
            if (WebUtils.hasSubmitParameter(request, submitAttribute)) {
                return ""; //StringUtils.substring(submitAttribute, 1);
            }
        }
        return null;
    }

    private int checkAuthorizationAttribute(String authorizationAttribute, Collection<? extends GrantedAuthority> collection) {
        for (GrantedAuthority grantedAuthority : collection) {
            if (authorizationAttribute.equalsIgnoreCase(grantedAuthority.getAuthority())) {
                return ACCESS_GRANTED;
            }
        }
        return ACCESS_DENIED;
    }

    boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    protected void OutputFunctionQuery(HttpServletRequest request, String authorizationAttribute, String returnvaluestring, String uri, int fileNameStartIndex, int fileNameEndIndex) {

        String fileName = (fileNameEndIndex < 0) ? uri : uri.substring(fileNameStartIndex + 1, fileNameEndIndex);
        String prefix = getAuthorizationAttributePrefix(request, fileName);
        String withoutprefix = getFileNameWithoutPrefixes(fileName);
        String tmp = (withoutprefix.equals("")) ? "" : withoutprefix.substring(0, 1).toUpperCase() + withoutprefix.substring(1).toLowerCase();
        if (prefix.length() > 1) {
            tmp += " - " + prefix.substring(0, 1).toUpperCase() + prefix.substring(1).toLowerCase();
        }
        log.info("TenantAccesDecisionVoter:: SQL_STATEMENT INSERT THAU_FUNCTON:\n " + "INSERT INTO thau_function (VERSION ,NAME, START_DATE, MODIFICATION_DATE, SEQUENCE_NUMBER,TECHNICAL_NAME) SELECT 0, '" + tmp.toString() + "' , " + "'2009-01-01', '2009-01-01', (select max(sequence_number) + 10 from thau_function), '" + authorizationAttribute.toString() + "');");
        log.info("TenantAccesDecisionVoter:: SQL_STATEMENT INSERT THAU_FUNCTION_ROLE:\n " + "insert into thau_function_role (version,  function_id, role_id) SELECT 0, " + " (select function_id from thau_function where name = '" + tmp.toString() + "') as function_id, " + " (select role_id from thau_role where name = 'SuperUserAdmin') as role_id from dual;\n");
        log.info("TenantAccesDecisionVoter:: SQL_STATEMENT INSERT THAU_FUNCTON_ROLE:\n" + "insert into thau_function_role (version,  function_id, role_id) SELECT 0, " + " (select function_id from thau_function where name = '" + tmp.toString() + "') as function_id, " + " (select role_id from thau_role where name = 'SuperUser') as role_id  from dual;\n\n");
    }
}