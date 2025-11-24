package dwe.holding.admin.security;

import ch.qos.logback.core.joran.spi.HttpUtil;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Component
@Slf4j
public class TenantAccessDecisionVoter {
    static final int ACCESS_GRANTED = 1;
    static final int ACCESS_DENIED = -1;

    protected static final String FILE_NAME_DELIMITER = "/";

    private static final String ATTRIBUTE_CREATE = "CREATE";
    private static final String ATTRIBUTE_READ = "READ";
    private static final String ATTRIBUTE_DELETE = "DELETE";

    private static final String LIST = "list";
    private final Map<String, String> autorisationKeys = new HashMap<>();

    /**
     * The vote determines if access is granted: using url and submit button. The
     * derived technical function name is created using the url and submit
     * button.. The following steps are performed in sequence: - Check if
     * authorisation is needed - Check if the url is valid - Check if it is a
     * report
     */

    @PreDestroy
    public void destroy() {
        autorisationKeys.forEach((key, value) -> System.out.printf("key: %s, value: %s%n", key, value));
    }

    public int vote(Authentication authentication, Object object) {
        // check for no security needed
        HttpServletRequest request = ((RequestAuthorizationContext) object).getRequest();
        String uri = request.getRequestURI();
        log.info("------------------>> vote called for uri=" + uri);
        if (uri.compareToIgnoreCase(request.getContextPath()) == 0) {
            return ACCESS_GRANTED;
        }

//******************************************
// *  Request methods mapping:
// *  GET   ->   /entity                  <- Retrieve the create screen for the entity
// *  GET   ->   /entity/{1}              <- Retrieve the create screen for the entity
// *  GET   ->   /entity/{id}/entity      <- Retrieve the create screen for the entity as child of a parent
// *  GET   ->   /entity/list             <- Retrieve the list screen filled with elements or empty
// *  GET   ->   /entity/{id}/entity/list <- Retrieve the list screen filled with elements or empty
// *
// *  POST  ->   /entity                  _save     <- Save the newly created entity
// *  POST  ->   /entity/{id}/entity      _save     <- Save the newly created entity as child of a parent
// *  POST  ->   /entity                  _delete   <- Delete the entity
// *  POST  ->   /entity/{id}/entity      _delete   <- Delete the entity as child of a parent
// *
// ******************************************

        HttpUtil.RequestMethod method = HttpUtil.RequestMethod.valueOf(request.getMethod());
        String last = getLastPart(uri);

        String authorizationAttribute = "";
        if (method.equals(HttpUtil.RequestMethod.GET)) {
            if (last.equalsIgnoreCase(LIST)) {
                authorizationAttribute = getPreviousBeforeLastPart(uri, "/list") + "_" + ATTRIBUTE_READ;
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


        int returnvalue = checkAuthorizationAttribute(authorizationAttribute, authentication.getAuthorities());

        // running with always ACCESS_GRANTED
//        if (returnvalue == -1) {
//            try {
//                autorisationKeys.put(uri, authorizationAttribute);
//            } catch (Exception e) {
//            }
//        }
//
//        Object principal = authentication.getDetails();
//        if (principal instanceof WebAuthenticationDetails) {
//            log.info("TenantAccesDecisionVoter:: Voting: ACCESS_GRANTED |user" +
//                    AutorisationUtils.getCurrentUserMid() + "|" +
//                    AutorisationUtils.getCurrentUserAccount() + " |prcssdUri=" + uri + "|authAttrd=" + authorizationAttribute);
//
//            return ACCESS_GRANTED;
//        }
//        return -1;

        if (returnvalue == -1) {
            log.debug("TenantAccesDecisionVoter::" + " ACCESS_DENIED for uri=" + uri + "  authorizationAttribute=" + authorizationAttribute);
            return returnvalue;
        } else {
            Object principal = authentication.getDetails();
            if (principal instanceof WebAuthenticationDetails) {
                log.debug("TenantAccesDecisionVoter:: Voting: ACCESS_GRANTED |user" +
                        AutorisationUtils.getCurrentUserMid() + "|" +
                        AutorisationUtils.getCurrentUserAccount() + " |prcssdUri=" + uri + "|authAttrd=" + authorizationAttribute);

                return returnvalue;
            }
            return ACCESS_DENIED;
        }
    }

    private String getPreviousBeforeLastPart(String uri, String endPart) {
        return getLastPart(uri.replace(endPart, ""));
    }

    private String getLastPart(String uri) {
        String lastPart;
        if (uri.charAt(uri.length() - 1) == '/') {
            String uriStripped = (uri.substring(0, (uri.length() - 1)));
            lastPart = uriStripped.substring(uriStripped.lastIndexOf(FILE_NAME_DELIMITER) + 1);
        } else {
            lastPart = uri.substring(uri.lastIndexOf(FILE_NAME_DELIMITER) + 1);
        }

        if (isLong(lastPart)) {
            return getPreviousBeforeLastPart(uri, lastPart);
        }
        return lastPart;

    }

    private int checkAuthorizationAttribute(String authorizationAttribute, Collection<? extends GrantedAuthority> collection) {
        String authorizationAttributeName = authorizationAttribute.toUpperCase();
        for (GrantedAuthority grantedAuthority : collection) {
            if (authorizationAttributeName.equals(grantedAuthority.getAuthority())) {
                return ACCESS_GRANTED;
            }
        }
        log.info("TenantAccesDecisionVoter:: ACCESS_DENIED for authorizationAttribute=" + authorizationAttributeName);
        return ACCESS_DENIED;
    }

    boolean isLong(String str) {
        if (str == null) {
            return false;
        }
        try {
            Long.parseLong(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}