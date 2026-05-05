package dwe.holding.admin.security;

import dwe.holding.admin.authorisation.notenant.function.FunctionRepository;
import dwe.holding.admin.authorisation.notenant.function_role.FunctionRoleRepository;
import dwe.holding.admin.authorisation.notenant.projection.IdName;
import dwe.holding.admin.sessionstorage.AutorisationUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.PathContainer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Component
@Slf4j
public class TenantAccessDecisionVoter {

    private final Set<String> errorUriList = new HashSet<>();
    private static final PathPatternParser parser = new PathPatternParser();

    static final int ACCESS_GRANTED = 1;
    static final int ACCESS_DENIED = -1;

    protected static final String FILE_NAME_DELIMITER = "/";

    private static final String ATTRIBUTE_CREATE = "CREATE";
    private static final String ATTRIBUTE_READ = "READ";
    private static final String ATTRIBUTE_DELETE = "DELETE";
    private static final String LIST = "list";

    @Autowired
    private FunctionRoleRepository functionRoleRepository;
    @Autowired
    FunctionRepository functionRepository;

    public Set<String> getErrorList() {
        return errorUriList;
    }

    public int vote(Authentication authentication, Object object) {
        // check for no security needed
        final HttpServletRequest request = ((RequestAuthorizationContext) object).getRequest();
        final String uri = request.getRequestURI();

        final String contextPath = request.getContextPath();
        log.info("------------------>> vote called for uri=" + uri);

        if (uri.compareToIgnoreCase(contextPath) == 0 || uri.compareToIgnoreCase(contextPath + '/') == 0) {
            return ACCESS_GRANTED;
        }

        String uriWithoutContext = uri.substring(request.getContextPath().length());
        final HttpMethod method = HttpMethod.valueOf(request.getMethod());

        List<Long> roleIdsConnectedToFunction = findRoles(method, uriWithoutContext);
        int accessLevel = checkAuthorizationAttribute(roleIdsConnectedToFunction, authentication.getAuthorities());

        if (accessLevel == -1) {
            log.info(
                    """
                            { "method": "%s", "uri": "%s", "attribute": "%s" }
                            """.formatted(method, uriWithoutContext, roleIdsConnectedToFunction.toString())
            );
            return accessLevel;
        } else {
            Object principal = authentication.getDetails();
            if (principal instanceof WebAuthenticationDetails) {
                if (AutorisationUtils.isLoggedIn()) {
                    log.debug("TenantAccesDecisionVoter:: Voting: ACCESS_GRANTED |user{}|{} |prcssdUri={}|roles={}",
                            AutorisationUtils.getCurrentUserMid(), AutorisationUtils.getCurrentUserAccount(), uri, roleIdsConnectedToFunction);
                } else {
                    log.debug("TenantAccesDecisionVoter:: Voting: ACCESS_GRANTED prcssdUri={}|roles={}", uri, roleIdsConnectedToFunction);
                }

                return accessLevel;
            }
            return ACCESS_DENIED;
        }
    }

    private List<Long> findRoles(HttpMethod method, String uri) {
        Long functionId = findFuctionId(method, uri);
        if (functionId.equals(0L)) {
            return List.of();
        }
        return functionRoleRepository.findRolesByFunctionId(functionId);
    }

    private Long findFuctionId(HttpMethod method, String uri) {
        PathContainer path = PathContainer.parsePath(method.name().toLowerCase() + "_" + uri.toLowerCase());

        for (IdName pattern : functionRepository.findAllCachedNames()) {
            PathPattern compiled = parser.parse(pattern.name());
            if (compiled.matches(path)) {
                return pattern.id();
            }
        }
        return 0L;
    }


    private int checkAuthorizationAttribute(List<Long> roleList, Collection<? extends GrantedAuthority> collection) {
        for (Long roleId : roleList) {
            for (GrantedAuthority grantedAuthority : collection) {
                if (roleId.toString().equals(grantedAuthority.getAuthority())) {
                    return ACCESS_GRANTED;
                }
            }
        }
        log.info("granting access checkAutorizationAttribute");
        return ACCESS_DENIED;
    }
}