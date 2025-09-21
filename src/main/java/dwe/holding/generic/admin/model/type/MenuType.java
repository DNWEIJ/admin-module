package dwe.holding.generic.admin.model.type;


import dwe.holding.generic.admin.exception.ApplicationException;
import dwe.holding.generic.admin.exception.SystemException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class MenuType extends CodeAndDescription implements Serializable {
    public static final String CODE_LEFT_MENU = "1";
    public static final String CODE_TOP_MENU = "2";
    public static final String CODE_SUPERUSER = "7";
    // Default waarden
    public static final String DESCRIPTION_LEFT_MENU = "Left Menu";
    public static final String DESCRIPTION_TOP_MENU = "Top Menu";
    public static final String DESCRIPTION_SUPERUSER = "Superuser menu";
    private static final long serialVersionUID = 1L;
    private static Map<String, String> codeDescription = getDefaultDescriptionStatic();
    private static Map<String, Long> sequence = getDefaultSequenceStatic();

    private MenuType(String code) {
        this.setCode(code);
    }

    private static Map<String, String> getDefaultDescriptionStatic() {
        Map<String, String> co = new TreeMap<String, String>();
        co.put(CODE_LEFT_MENU, DESCRIPTION_LEFT_MENU);
        co.put(CODE_TOP_MENU, DESCRIPTION_TOP_MENU);
        co.put(CODE_SUPERUSER, DESCRIPTION_SUPERUSER);
        return co;
    }

    private static Map<String, Long> getDefaultSequenceStatic() {
        Map<String, Long> vo = new TreeMap<String, Long>();

        vo.put(CODE_LEFT_MENU, 0L);
        vo.put(CODE_TOP_MENU, 1L);
        vo.put(CODE_SUPERUSER, 2L);
        return vo;
    }

    public static MenuType getInstance(String code) throws ApplicationException {
        if (codeDescription.containsKey(code)) {
            return new MenuType(code);
        }
        throw new ApplicationException(ERROR_CODE_1, new String[]{NOTIFICATION_1});
    }

    private static void setCodeOmschrijving(Map<String, String> codeOmschrijving) {
        MenuType.codeDescription = codeDescription;
    }

    private static void setVolgOrde(Map<String, Long> volgOrde) {
        MenuType.sequence = sequence;
    }

    public static List<MenuType> getAlleMenuTypes() {
        List<MenuType> result = new ArrayList<MenuType>();
        for (String string : codeDescription.keySet()) {
            try {
                result.add(getInstance(string));
            } catch (Exception e) {
                throw new SystemException("SYS-00001", e);
            }
        }
        return result;
    }

    public static List<MenuType> getAlleMenuTypesZonderSuperMenu() {
        List<MenuType> result = getAlleMenuTypes();
        try {
            result.remove(MenuType.getInstance(CODE_SUPERUSER));
        } catch (ApplicationException e) {
            throw new SystemException("SYS-00001", e);
        }
        return result;
    }

    @Override
    public void setMap(Map<String, String> newMap, Map<String, Long> volgOrdeParam) throws ApplicationException {
        for (String key : getDefaultDescriptionStatic().keySet()) {
            if (!newMap.containsKey(key) || !volgOrdeParam.containsKey(key)) {
                throw new ApplicationException(ERROR_CODE_2, new String[]{NOTIFICATION_2});
            }
        }
        setCodeOmschrijving(newMap);
        setVolgOrde(volgOrdeParam);
    }

    @Override
    public Map<String, String> getDefaultDescriptions() {
        return getDefaultDescriptionStatic();
    }

    @Override
    public Map<String, Long> getDefaultSequence() {
        return getDefaultSequenceStatic();
    }

    @Override
    protected Map<String, String> getCodesAndDescription() {
        return codeDescription;
    }

    @Override
    protected Map<String, Long> getSequence() {
        return sequence;
    }
}