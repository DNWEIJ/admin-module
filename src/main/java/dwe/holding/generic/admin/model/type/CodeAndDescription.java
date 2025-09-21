package dwe.holding.generic.admin.model.type;

import dwe.holding.generic.admin.exception.ApplicationException;

import java.util.Map;

/**
 * This class describes code and Description. It can be used for dropdowns to define the information of it.
 *
 */
public abstract class CodeAndDescription implements Comparable<CodeAndDescription> {
    protected static final String ERROR_CODE_1 = "SYS-00003";
    protected static final String NOTIFICATION_1 = "Delivered code value is invalid";
    protected static final String ERROR_CODE_2 = "SYS-00003";
    protected static final String NOTIFICATION_2 = "Several codes are missing";
    private static final int HASHCODE = 255;


    private String code;

    protected abstract Map<String, String> getCodesAndDescription();

    public abstract Map<String, String> getDefaultDescriptions();

    public abstract Map<String, Long> getDefaultSequence();

    /**
     * Set the codes and descriptions.
     *
     * @param newMap
     * @param volgOrdeParam
     * @throws ApplicationException
     */
    public abstract void setMap(Map<String, String> newMap, Map<String, Long> volgOrdeParam)
            throws ApplicationException;

    /**
     * a HashMap of code and number, used for sorting.
     *
     * @return de Map.
     */
    protected abstract Map<String, Long> getSequence();

    public String getCode() {
        return code;
    }

    protected void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return getCodesAndDescription().get(code);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof CodeAndDescription) {
            CodeAndDescription cmo = (CodeAndDescription) obj;
            if (cmo.getCode() == this.getCode() || (this.getCode() != null && this.getCode().equals(cmo.getCode()))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return HASHCODE;
    }

    @Override
    public String toString() {
        return getDescription();
    }

    public int compareTo(CodeAndDescription cmo) {
        Long waarde = getSequence().get(this.getCode()) - getSequence().get(cmo.getCode());
        return waarde < 0 ? -1 : waarde > 0 ? 1 : 0;
    }
}