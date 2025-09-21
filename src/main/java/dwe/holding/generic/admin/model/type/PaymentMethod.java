package dwe.holding.generic.admin.model.type;


import dwe.holding.generic.admin.exception.ApplicationException;

import java.io.Serializable;
import java.util.*;

public final class PaymentMethod extends CodeAndDescription implements Serializable {
    public static final String CODE_CASH = "0";
    public static final String CODE_CHECK = "1";
    public static final String CODE_CREDIT_CARD = "2";
    public static final String CODE_OTHER = "3";
    public static final String CODE_CHIP = "4";
    public static final String CODE_PIN = "5";
    public static final String CODE_BANK = "6";
    public static final String CODE_ONETIME = "7";
    // Default waarden
    public static final String DESCRIPTION_CASH = "label.paymentmethod.cash";
    public static final String DESCRIPTION_CHECK = "label.paymentmethod.check";
    public static final String DESCRIPTION_CREDIT_CARD = "label.paymentmethod.credit_card";
    public static final String DESCRIPTION_CHIP = "label.paymentmethod.chip";
    public static final String DESCRIPTION_PIN = "label.paymentmethod.pin";
    public static final String DESCRIPTION_OTHER = "label.paymentmethod.other";
    public static final String DESCRIPTION_BANK = "label.paymentmethod.bank";
    public static final String DESCRIPTION_ONETIME = "label.paymentmethod.onetime";
    private static final long serialVersionUID = 1L;
    private static Map<String, String> codedescription = getDefaultOmschrijvingenStatic();
    private static Map<String, Long> volgorde = getDefaultVolgordeStatic();

    private PaymentMethod(String code) {
        this.setCode(code);
    }

    private static Map<String, String> getDefaultOmschrijvingenStatic() {
        Map<String, String> co = new TreeMap<String, String>();
        co.put(CODE_CASH, DESCRIPTION_CASH);
        co.put(CODE_CHECK, DESCRIPTION_CHECK);
        co.put(CODE_CREDIT_CARD, DESCRIPTION_CREDIT_CARD);
        co.put(CODE_CHIP, DESCRIPTION_CHIP);
        co.put(CODE_PIN, DESCRIPTION_PIN);
        co.put(CODE_BANK, DESCRIPTION_BANK);
        co.put(CODE_ONETIME, DESCRIPTION_ONETIME);
        co.put(CODE_OTHER, DESCRIPTION_OTHER);
        return co;
    }

    private static Map<String, Long> getDefaultVolgordeStatic() {
        Map<String, Long> vo = new TreeMap<String, Long>();
        vo.put(CODE_CASH, 0L);
        vo.put(CODE_CHECK, 1L);
        vo.put(CODE_CREDIT_CARD, 2L);
        vo.put(CODE_CHIP, 3L);
        vo.put(CODE_PIN, 4L);
        vo.put(CODE_BANK, 6L);
        vo.put(CODE_ONETIME, 7L);
        vo.put(CODE_OTHER, 5L);
        return vo;
    }

    public static PaymentMethod getInstance(String code) throws ApplicationException {
        if (codedescription.containsKey(code)) {
            return new PaymentMethod(code);
        }
        throw new ApplicationException(ERROR_CODE_1, new String[]{NOTIFICATION_1});
    }

    private static void setCodeOmschrijving(Map<String, String> codedescription) {
        PaymentMethod.codedescription = codedescription;
    }

    private static void setVolgOrde(Map<String, Long> volgorde) {
        PaymentMethod.volgorde = volgorde;
    }

    public static List<PaymentMethod> getAllelTypes() throws ApplicationException {
        List<PaymentMethod> result = new ArrayList<PaymentMethod>();
        result.add(getInstance(CODE_CASH));
        result.add(getInstance(CODE_CHECK));
        result.add(getInstance(CODE_CREDIT_CARD));
        result.add(getInstance(CODE_CHIP));
        result.add(getInstance(CODE_PIN));
        result.add(getInstance(CODE_BANK));
        result.add(getInstance(CODE_ONETIME));
        result.add(getInstance(CODE_OTHER));
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