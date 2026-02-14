package dwe.holding.admin.security;

import dwe.holding.admin.sessionstorage.AutorisationUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
@Slf4j
public class TenantAccessDecisionVoter {

    private final Set<String> uriList = new HashSet<>();
    static final int ACCESS_GRANTED = 1;
    static final int ACCESS_DENIED = -1;

    protected static final String FILE_NAME_DELIMITER = "/";

    private static final String ATTRIBUTE_CREATE = "CREATE";
    private static final String ATTRIBUTE_READ = "READ";
    private static final String ATTRIBUTE_DELETE = "DELETE";
    private static final String LIST = "list";


    public Set<String> getList(){return uriList;}

    /**
     * The vote determines if access is granted: using url and submit button. The
     * derived technical function name is created using the url and submit
     * button.. The following steps are performed in sequence: - Check if
     * authorization is needed - Check if the url is valid - Check if it is a
     * report
     */
    public int vote(Authentication authentication, Object object) {
        // check for no security needed
        final HttpServletRequest request = ((RequestAuthorizationContext) object).getRequest();

        String uri = request.getRequestURI();
        log.info("------------------>> vote called for uri=" + uri);

        if (uri.compareToIgnoreCase(request.getContextPath()) == 0) {
            return ACCESS_GRANTED;
        }
        uri = uri.substring(request.getContextPath().length());

        final HttpMethod method = HttpMethod.valueOf(request.getMethod());


        String authorizationAttribute = "";
        if (uri.startsWith(request.getContextPath() + "/admin")) {
            authorizationAttribute = findAuthoirzationAttributeForCRUD(method, getLastPart(uri), uri, request.getParameterMap());
        } else {
            authorizationAttribute = findAuthoirzationAttributeForOther(method, getLastPart(uri), uri, request.getParameterMap());
        }

        int accessLevel = checkAuthorizationAttribute(authorizationAttribute, authentication.getAuthorities());

        if (accessLevel == -1) {
            log.info(
                    """
                            { "method": "%s", "uri": "%s", "attribute": "%s" }
                            """.formatted(method, uri, authorizationAttribute)
            );
            return accessLevel;
        } else {
            Object principal = authentication.getDetails();
            if (principal instanceof WebAuthenticationDetails) {
                if (AutorisationUtils.isLoggedIn()) {
                    log.debug("TenantAccesDecisionVoter:: Voting: ACCESS_GRANTED |user{}|{} |prcssdUri={}|authAttrd={}",
                            AutorisationUtils.getCurrentUserMid(), AutorisationUtils.getCurrentUserAccount(), uri, authorizationAttribute);
                } else {
                    log.debug("TenantAccesDecisionVoter:: Voting: ACCESS_GRANTED prcssdUri={}|authAttrd={}", uri, authorizationAttribute);
                }

                return accessLevel;
            }
            return ACCESS_DENIED;
        }
    }

    //******************************************
    // *  Request methods mapping for admin -> most action are real CRUD: List, edit, add, delete items. p
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
    // *  AJAX Calls:
    // *  DELETE  ->
    // ******************************************
    private String findAuthoirzationAttributeForCRUD(HttpMethod method, String last, String uri, Map<String, String[]> params) {

        if (HttpMethod.GET.equals(method)) {
            return last.equalsIgnoreCase(LIST)
                    ? getPreviousBeforeLastPart(uri, "/list") + "_" + ATTRIBUTE_READ
                    : last + "_" + ATTRIBUTE_READ;
        } else if (HttpMethod.POST.equals(method)) {
            if (params.containsKey(ButtonConstants.PARAM_SAVE_NEW) || params.containsKey(ButtonConstants.PARAM_SAVE)) {
                return last + "_" + ATTRIBUTE_CREATE;
            } else if (params.containsKey(ButtonConstants.PARAM_DELETE)) {
                return last + "_" + ATTRIBUTE_DELETE;
            } else {
                return "";
            }
        } else if (HttpMethod.DELETE.equals(method)) {
            return last + "_" + ATTRIBUTE_DELETE;
        } else {
            return "";
        }
    }

    //******************************************
    //    /customer/search/customer
    //    /customer/preferences/user
    //    /customer/customer

    //    /error

    //    /vmas/userpreferences

    //    /sales/price/sell
    //    /sales/otc/search/70823/sell/1242244/135378
    //    /sales/otc/search/70823
    //    /sales/otc/search
    //    /urilist
    //    /costing/search/costing


    // *  Request methods mapping for others: urls are build more in a resty feeling, where IDs are added in the url, to have links that can access direclty
    // *  GET   ->
    // *  GET   ->
    // *
    // *  POST  ->
    // *  POST  ->
    // *  AJAX Calls:
    // *  DELETE  ->
    // ******************************************
    private String findAuthoirzationAttributeForOther(HttpMethod method, String last, String uri, Map<String, String[]> params) {
        if (HttpMethod.GET.equals(method)) {
            if (last.endsWith("index"))  return last+"_"+ATTRIBUTE_READ;
        }
        uriList.add(method.name() + " " + uri);
        return "index_"+ATTRIBUTE_READ;
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