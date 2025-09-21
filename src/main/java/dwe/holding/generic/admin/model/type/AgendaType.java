package dwe.holding.generic.admin.model.type;


import dwe.holding.generic.admin.exception.ApplicationException;

import java.io.Serializable;
import java.util.*;

public final class AgendaType extends CodeAndDescription implements Serializable {
    public static final String CODE_ROOM = "R";
    public static final String CODE_VET = "V";
    public static final String CODE_WEEK = "W"; //week view for agenda
    public static final String CODE_AGENDA = "agenda";
    public static final String CODE_CUSTOMER = "customer";
    // Default waarden
    public static final String DESCRIPTION_VET = "label.agendatype.vetenarian";
    public static final String DESCRIPTION_ROOM = "label.agendatype.room";
    public static final String DESCRIPTION_WEEK = "label.agendatype.week";
    private static final long serialVersionUID = 1L;
    private static Map<String, String> codedescription = getDefaultOmschrijvingenStatic();
    private static Map<String, Long> volgorde = getDefaultVolgordeStatic();

    private AgendaType(String code) {
        this.setCode(code);
    }

    private static Map<String, String> getDefaultOmschrijvingenStatic() {
        Map<String, String> co = new TreeMap<String, String>();
        co.put(CODE_VET, DESCRIPTION_VET);
        co.put(CODE_ROOM, DESCRIPTION_ROOM);
        co.put(CODE_WEEK, DESCRIPTION_WEEK);
        return co;
    }

    private static Map<String, Long> getDefaultVolgordeStatic() {
        Map<String, Long> vo = new TreeMap<String, Long>();
        vo.put(CODE_VET, 0L);
        vo.put(CODE_ROOM, 1L);
        vo.put(CODE_WEEK, 1L);

        return vo;
    }

    public static AgendaType getInstance(String code) throws ApplicationException {
        if (codedescription.containsKey(code)) {
            return new AgendaType(code);
        }
        throw new ApplicationException(ERROR_CODE_1, new String[]{NOTIFICATION_1});
    }

    private static void setCodeOmschrijving(Map<String, String> codedescription) {
        AgendaType.codedescription = codedescription;
    }

    private static void setVolgOrde(Map<String, Long> volgorde) {
        AgendaType.volgorde = volgorde;
    }

    public static List<AgendaType> getAllelTypes() throws ApplicationException {
        List<AgendaType> result = new ArrayList<AgendaType>();
        result.add(getInstance(CODE_VET));
        result.add(getInstance(CODE_ROOM));
        result.add(getInstance(CODE_WEEK));
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