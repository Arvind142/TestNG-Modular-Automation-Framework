package com.core.framework.testloggers;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;

/***
 * Test class holds few variables which are required while logging test status
 *
 * @author arvin
 *
 */
public class TableLog implements StepLogger {
    /**
     * holds step description
     */
    private final String stepDescription;
    /**
     * holds expected value
     */
    private final String expectedValue;
    /**
     * holds actual value
     */
    private final String actualValue;
    /**
     * log status holds status of log like PASS/FAIL/SKIP
     */
    private final Status logStatus;
    /**
     * attachment path is hold in attachment variable
     */
    private final String attachment;

    /***
     * constructor to create log with required values
     *
     * @param stepDescription test step name
     * @param expectedValue   expected value
     * @param actualValue     actual value
     * @param logStatus       log status i.e. PASS/FAIL/SKIP
     * @param attachment      url of attachment
     */
    private TableLog(String stepDescription, String expectedValue, String actualValue, Status logStatus,
                     String attachment) {
        super();
        this.stepDescription = stepDescription;
        this.expectedValue = expectedValue;
        this.actualValue = actualValue;
        this.logStatus = logStatus;
        if (attachment == null || attachment.trim().equalsIgnoreCase("")) {
            this.attachment = null;
        } else {
            this.attachment = "<a href=\"" + attachment + "\" target=\"_blank\"> Click Here!</a>";
        }
    }

    /**
     * @return Test class Object the logStatus
     */
    public Status getLogStatus() {
        return logStatus;
    }

    /***
     * method helps in logging INFO statement for particular test case based on need
     *
     * @param stepDescription test step description
     * @param expectedValue   additional information
     * @return Test class Object
     */
    public static synchronized TableLog logInfo(String stepDescription, String expectedValue) {
        return new TableLog(stepDescription, expectedValue, "", Status.INFO, null);
    }

    /***
     * method helps in logging SKIP statement for particular test case
     *
     * @param stepDescription step description
     * @param expectedValue   expected value
     * @return Test class Object
     */
    public static synchronized TableLog logSkip(String stepDescription, String expectedValue) {
        return new TableLog(stepDescription, expectedValue, "", Status.SKIP, null);
    }

    /***
     * to log PASS statement where expected and actuals are one and the same
     *
     * @param stepDescription step description
     * @param expectedValue   expected value
     * @return Test class Object
     */
    public static synchronized TableLog logPass(String stepDescription, String expectedValue) {
        return new TableLog(stepDescription, expectedValue, expectedValue, Status.PASS, null);
    }

    /***
     * method to log error as FAIL
     *
     * @param stepDescription step description
     * @param expectedValue   expected value which would be exception/error
     *                        statement
     * @return Test class Object
     */
    public static synchronized TableLog logError(String stepDescription, String expectedValue) {
        return new TableLog(stepDescription, expectedValue, "", Status.FAIL, null);
    }

    /**
     * method to deal with multiple data types and log results
     *
     * @param <T>             Data type
     * @param stepDescription steps to describe current log details
     * @param expectedValue   expected results
     * @param actualValue     actual results
     * @return Test class object
     */
    public static synchronized <T> TableLog log(String stepDescription, T expectedValue, T actualValue) {
        String exp;
        String act;

        try {
            // to work with null elements
            if (expectedValue == null || actualValue == null) {
                if (expectedValue == null) {
                    exp = "";
                }
                else{
                    exp = String.valueOf(expectedValue);
                }
                if (actualValue == null) {
                    act = "";
                }else{
                    act = String.valueOf(actualValue);
                }
                // if any of both of them or both are null
                return new TableLog(stepDescription, exp, act,
                        (exp.equals(act) ? Status.PASS : Status.FAIL), null);
            }

            // few conversions
            if ((expectedValue instanceof String && actualValue instanceof String)
                    || (expectedValue instanceof String[] && actualValue instanceof String[])) {

                // String comparison
                if (expectedValue instanceof String[] && actualValue instanceof String[]) {
                    String[] exps = (String[]) expectedValue;
                    String[] acts = (String[]) actualValue;
                    exp = TableLog.getPrintableStringOfArray(exps);
                    act = TableLog.getPrintableStringOfArray(acts);
                } else {
                    String exps = String.valueOf(expectedValue);
                    String acts = String.valueOf(actualValue);
                    exp = exps;
                    act = acts;
                }
            } else if ((expectedValue instanceof Integer && actualValue instanceof Integer)
                    || (expectedValue instanceof Integer[] && actualValue instanceof Integer[])) {

                // Integer comparison
                if (expectedValue instanceof Integer[] && actualValue instanceof Integer[]) {
                    Integer[] exps = (Integer[]) expectedValue;
                    Integer[] acts = (Integer[]) actualValue;
                    exp = TableLog.getPrintableStringOfArray(exps);
                    act = TableLog.getPrintableStringOfArray(acts);
                } else {
                    Integer exps = (Integer) expectedValue;
                    Integer acts = (Integer) actualValue;
                    exp = String.valueOf(exps);
                    act = String.valueOf(acts);
                }
            } else if ((expectedValue instanceof Long && actualValue instanceof Long)
                    || (expectedValue instanceof Long[] && actualValue instanceof Long[])) {

                // Long comparison
                if (expectedValue instanceof Long[] && actualValue instanceof Long[]) {
                    Long[] exps = (Long[]) expectedValue;
                    Long[] acts = (Long[]) actualValue;
                    exp = TableLog.getPrintableStringOfArray(exps);
                    act = TableLog.getPrintableStringOfArray(acts);
                } else {
                    Long exps = (Long) expectedValue;
                    Long acts = (Long) actualValue;
                    exp = String.valueOf(exps);
                    act = String.valueOf(acts);
                }
            } else if ((expectedValue instanceof Float && actualValue instanceof Float)
                    || (expectedValue instanceof Float[] && actualValue instanceof Float[])) {

                // Float comparison
                if (expectedValue instanceof Float[]  && actualValue instanceof Float[]) {
                    Float[] exps = (Float[]) expectedValue;
                    Float[] acts = (Float[]) actualValue;
                    exp = TableLog.getPrintableStringOfArray(exps);
                    act = TableLog.getPrintableStringOfArray(acts);
                } else {
                    Float exps = (Float) expectedValue;
                    Float acts = (Float) actualValue;
                    exp = String.valueOf(exps);
                    act = String.valueOf(acts);
                }
            } else if ((expectedValue instanceof Double && actualValue instanceof Double)
                    || (expectedValue instanceof Double[] && actualValue instanceof Double[])) {

                // Double comparison
                if (expectedValue instanceof Double[] && actualValue instanceof Double[]) {
                    Double[] exps = (Double[]) expectedValue;
                    Double[] acts = (Double[]) actualValue;
                    exp = TableLog.getPrintableStringOfArray(exps);
                    act = TableLog.getPrintableStringOfArray(acts);
                } else {
                    Double exps = (Double) expectedValue;
                    Double acts = (Double) actualValue;
                    exp = String.valueOf(exps);
                    act = String.valueOf(acts);
                }
            } else {
                exp = "invalid dataType";
                act = "invalid dataType";
            }

            return new TableLog(stepDescription, exp, act,
                    (exp.equals(act) ? Status.PASS : Status.FAIL), null);
        } catch (Exception e) {
            e.printStackTrace();
            return new TableLog(stepDescription, "logging issue", e.getMessage(), Status.FAIL, null);
        }
    }

    /**
     * method to deal with multiple data types and log results
     *
     * @param <T>             Data type
     * @param stepDescription steps to describe current log details
     * @param expectedValue   expected results
     * @param actualValue     actual results
     * @param attachment      attachment/evidence path
     * @return Test class object
     */
    public static synchronized <T> TableLog log(String stepDescription, T expectedValue, T actualValue,
                                                String attachment) {
        String exp = "";
        String act = "";

        try {
            // to work with null elements
            if (expectedValue == null || actualValue == null) {
                if (expectedValue == null) {
                    exp = "";
                }
                if (actualValue == null) {
                    act = "";
                }
                // if any of both of them or both are null
                return new TableLog(stepDescription, exp, act,
                        (exp.equals(act) ? Status.PASS : Status.FAIL), null);
            }

            // few conversions
            if ((expectedValue instanceof String && actualValue instanceof String)
                    || (expectedValue instanceof String[] && actualValue instanceof String[])) {

                // String comparison
                if (expectedValue instanceof String[] && actualValue instanceof String[]) {
                    String[] exps = (String[]) expectedValue;
                    String[] acts = (String[]) actualValue;
                    exp = TableLog.getPrintableStringOfArray(exps);
                    act = TableLog.getPrintableStringOfArray(acts);
                } else {
                    String exps = (String) expectedValue;
                    String acts = (String) actualValue;
                    exp = exps;
                    act = acts;
                }
            } else if ((expectedValue instanceof Integer && actualValue instanceof Integer)
                    || (expectedValue instanceof Integer[] && actualValue instanceof Integer[])) {

                // Integer comparison
                if (expectedValue instanceof Integer[] && actualValue instanceof Integer[]) {
                    Integer[] exps = (Integer[]) expectedValue;
                    Integer[] acts = (Integer[]) actualValue;
                    exp = TableLog.getPrintableStringOfArray(exps);
                    act = TableLog.getPrintableStringOfArray(acts);
                } else {
                    Integer exps = (Integer) expectedValue;
                    Integer acts = (Integer) actualValue;
                    exp = String.valueOf(exps);
                    act = String.valueOf(acts);
                }
            } else if ((expectedValue instanceof Long && actualValue instanceof Long)
                    || (expectedValue instanceof Long[] && actualValue instanceof Long[])) {

                // Long comparison
                if (expectedValue instanceof Long[] && actualValue instanceof Long[]) {
                    Long[] exps = (Long[]) expectedValue;
                    Long[] acts = (Long[]) actualValue;
                    exp = TableLog.getPrintableStringOfArray(exps);
                    act = TableLog.getPrintableStringOfArray(acts);
                } else {
                    Long exps = (Long) expectedValue;
                    Long acts = (Long) actualValue;
                    exp = String.valueOf(exps);
                    act = String.valueOf(acts);
                }
            } else if ((expectedValue instanceof Float && actualValue instanceof Float)
                    || (expectedValue instanceof Float[] && actualValue instanceof Float[])) {

                // Float comparison
                if (expectedValue instanceof Float[] && actualValue instanceof Float[]) {
                    Float[] exps = (Float[]) expectedValue;
                    Float[] acts = (Float[]) actualValue;
                    exp = TableLog.getPrintableStringOfArray(exps);
                    act = TableLog.getPrintableStringOfArray(acts);
                } else {
                    Float exps = (Float) expectedValue;
                    Float acts = (Float) actualValue;
                    exp = String.valueOf(exps);
                    act = String.valueOf(acts);
                }
            } else if ((expectedValue instanceof Double && actualValue instanceof Double)
                    || (expectedValue instanceof Double[] && actualValue instanceof Double[])) {

                // Double comparison
                if (expectedValue instanceof Double[] && actualValue instanceof Double[]) {
                    Double[] exps = (Double[]) expectedValue;
                    Double[] acts = (Double[]) actualValue;
                    exp = TableLog.getPrintableStringOfArray(exps);
                    act = TableLog.getPrintableStringOfArray(acts);
                } else {
                    Double exps = (Double) expectedValue;
                    Double acts = (Double) actualValue;
                    exp = String.valueOf(exps);
                    act = String.valueOf(acts);
                }
            } else {
                exp = "invalid dataType";
                act = "invalid dataType";
            }

            return new TableLog(stepDescription, exp, act,
                    (exp.equals(act) ? Status.PASS : Status.FAIL), attachment);
        } catch (Exception e) {
            e.printStackTrace();
            return new TableLog(stepDescription, "logging issue", e.getMessage(), Status.FAIL, attachment);
        }
    }

    /**
     * return printable value of any data type class
     *
     * @param t any element arrayClass
     * @return string equivalent of values present in array
     */
    public static <T> String getPrintableStringOfArray(T[] t) {
        String ret = "[";
        for (T element : t) {
            ret += "[ " + String.valueOf(element) + " ]";
        }
        ret += "]";
        return ret;
    }

    /***
     * toString() method give String value of object which shows values being holded
     * by each variable
     */
    @Override
    public String toString() {
        String stepLog="<details>"+
                                "<summary><b><i>"+stepDescription+"</i></b></summary>"+
                                "<table>" +
                                    "<thead>"+
                                        "<th>Expected Condition</th>"+
                                        "<th>Actual Condition</th>"+
                                        "<th>Evidence</th>"+
                                    "</thead>"+
                                    "<tbody>"+
                                        "<td>"+(expectedValue!=null?expectedValue:"NA")+"</td>"+
                                        "<td>"+(actualValue!=null?actualValue:"NA")+"</td>"+
                                        "<td>"+(attachment!=null?attachment:"NA")+"</td>"+
                                    "</tbody>"+
                                "</table>"+
                            "</details>";
        return stepLog;
    }


    @Override
    public Markup getEquivalent() {
        return MarkupHelper.createTable(new String[][]{
                {"Summary","Expected Condition","Actual Condition","Evidence"},
                {stepDescription,(expectedValue!=null?expectedValue:"NA"),(actualValue!=null?actualValue:"NA"),(attachment!=null?attachment:"NA")}
        });
    }




    public String toStringOlderOne() {
        if (isNullOrEmpty(expectedValue) && isNullOrEmpty(actualValue) && isNullOrEmpty(attachment)) {
            return " | Description: " + stepDescription + " | ";
        } else if (isNullOrEmpty(actualValue) && isNullOrEmpty(expectedValue) && !isNullOrEmpty(attachment)) {
            return " | Description: " + stepDescription + " | Evidence: " + attachment + " | ";
        } else if (isNullOrEmpty(actualValue) && isNullOrEmpty(attachment)) {
            return " | Description: " + stepDescription + " | Info: " + expectedValue + " | ";
        } else if (isNullOrEmpty(attachment)) {
            return " | Description: " + stepDescription + " | Expected: " + expectedValue + " | Actual: " + actualValue
                    + " | ";
        } else {
            return " | Description: " + stepDescription + " | Expected: " + expectedValue + " | Actual: " + actualValue
                    + " | Evidence: " + attachment + " | ";
        }
    }

    /**
     * to let use know if given string is empty/null
     *
     * @param element
     * @return
     */
    public boolean isNullOrEmpty(String element) {
        if (element != null) {
            if (element.trim().equals("")) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

}
