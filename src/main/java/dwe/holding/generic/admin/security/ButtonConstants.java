package dwe.holding.generic.admin.security;

import jakarta.servlet.http.HttpServletRequest;

/**
 * <p>
 * List of all buttons used within the application. All butons need to be defined here.
 * </p>
 */
public final class ButtonConstants {

    // generic buttons
    public static final String PARAM_SEARCH = "_search";
    public static final String PARAM_SAVE = "_save";
    public static final String PARAM_SAVE_NEW = "_saveandnew";
    public static final String PARAM_DELETE = "_delete";
    private ButtonConstants() {
        // no default constructor
    }

    public static String getRedirectFor(HttpServletRequest request, Long id, String url) {
        if (request.getParameter(ButtonConstants.PARAM_SAVE) != null) {
            return url + "/" + id;
        }
        if (request.getParameter(ButtonConstants.PARAM_SAVE_NEW) != null) {
            return url;
        }
        throw new RuntimeException("ButtonConstants::getRedirectForSave");
    }

}