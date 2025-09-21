package dwe.holding.generic.admin.model.type;


import dwe.holding.generic.admin.exception.ApplicationException;

import java.io.Serializable;
import java.util.*;

/**
 * Object representation of a Yes No, Maybe (Transition) object.
 * Can be overridden with he database information (language specific) for this domain.
 */

public final class YesNoTransition extends CodeAndDescription implements Serializable {

    public static final String CODE_YES = "Y";
    public static final String CODE_NO = "N";
    public static final String CODE_TRANSITION = "T";
    // Default waarden
    public static final String DESCRIPTION_YES = "label.yesno.yes";
    public static final String DESCRIPTION_NO = "label.yesno.no";
    public static final String DESCRIPTION_TRANSISTION = "label.yesno.transition";
    private static final long serialVersionUID = 4417846881103739273L;
    private static Map<String, String> codedescription = getDefaultOmschrijvingenStatic();
    private static Map<String, Long> volgorde = getDefaultVolgordeStatic();

    private YesNoTransition(String code) {
        this.setCode(code);
    }

    private static Map<String, String> getDefaultOmschrijvingenStatic() {
        Map<String, String> co = new TreeMap<String, String>();
        co.put(CODE_YES, DESCRIPTION_YES);
        co.put(CODE_NO, DESCRIPTION_NO);
        co.put(CODE_TRANSITION, DESCRIPTION_TRANSISTION);

        return co;
    }

    private static Map<String, Long> getDefaultVolgordeStatic() {
        Map<String, Long> vo = new TreeMap<String, Long>();
        vo.put(CODE_YES, 0L);
        vo.put(CODE_NO, 1L);
        vo.put(CODE_TRANSITION, 2L);
        return vo;
    }

    public static YesNoTransition getInstance(String code) throws ApplicationException {
        if (codedescription.containsKey(code)) {
            return new YesNoTransition(code);
        }
        throw new ApplicationException(ERROR_CODE_1, new String[]{NOTIFICATION_1});
    }

    private static void setCodeOmschrijving(Map<String, String> codedescription) {
        YesNoTransition.codedescription = codedescription;
    }

    private static void setVolgOrde(Map<String, Long> volgorde) {
        YesNoTransition.volgorde = volgorde;
    }

    public static List<YesNoTransition> getAllelTypes() throws ApplicationException {
        List<YesNoTransition> result = new ArrayList<YesNoTransition>();
        result.add(getInstance(CODE_YES));
        result.add(getInstance(CODE_NO));
        result.add(getInstance(CODE_TRANSITION));
        Collections.sort(result);
        return result;
    }

    @Override
    public void setMap(Map<String, String> newMap, Map<String, Long> volgordeParam) throws ApplicationException {
        for (String key : getDefaultOmschrijvingenStatic().keySet()) {
            if (!newMap.containsKey(key) || !volgordeParam.containsKey(key)) {
                throw new ApplicationException(ERROR_CODE_2, new String[]{NOTIFICATION_2});
            }
        }
        setCodeOmschrijving(newMap);
        setVolgOrde(volgordeParam);
    }

    @Override
    public Map<String, String> getDefaultDescriptions() {
        return getDefaultOmschrijvingenStatic();
    }

    @Override
    public Map<String, Long> getDefaultSequence() {
        return getDefaultVolgordeStatic();
    }

    @Override
    protected Map<String, String> getCodesAndDescription() {
        return codedescription;
    }

    @Override
    protected Map<String, Long> getSequence() {
        return volgorde;
    }
}