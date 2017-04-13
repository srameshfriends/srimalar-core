package dcapture.data.core;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Ramesh
 * SQL Object To String
 */
public class SqlParameter {
    private String datePattern, timePattern, dateTimePattern, decimalPattern, quantityPattern;
    private DateFormat dateFormat, dateTimeFormat, timeFormat;
    private DecimalFormat decimalFormat, quantityFormat;
    private List<String> parameterList;
    private String fileName, queryName, query, joinQuery;
    private WhereQuery whereQuery;
    private int limit, offset;

    public SqlParameter() {
        parameterList = new ArrayList<>();
        setJoinQuery("");
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileQueryNames(String fileName, String queryName) {
        this.fileName = fileName;
        this.queryName = queryName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getQueryName() {
        return queryName;
    }

    public String getQuery() {
        return query;
    }

    public String[] getParameters() {
        if (parameterList.size() == 0) {
            return null;
        }
        return parameterList.toArray(new String[parameterList.size()]);
    }

    public void setDateFormat(String datePattern) {
        this.datePattern = datePattern;
    }

    public void setDateTimeFormat(String dateTimePattern) {
        this.dateTimePattern = dateTimePattern;
    }

    public void setTimeFormat(String timePattern) {
        this.timePattern = timePattern;
    }

    public void setDecimalFormat(String decimalPattern) {
        this.decimalPattern = decimalPattern;
    }

    public void setQuantityFormat(String quantityPattern) {
        this.quantityPattern = quantityPattern;
    }

    public void setLimitOffset(int limit, int offset) {
        this.limit = limit;
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }

    public SqlParameter add(int value) {
        parameterList.add(String.valueOf(value));
        return SqlParameter.this;
    }

    public SqlParameter add(long value) {
        parameterList.add(String.valueOf(value));
        return SqlParameter.this;
    }

    public SqlParameter add(double value) {
        parameterList.add(String.valueOf(value));
        return SqlParameter.this;
    }

    public SqlParameter add(boolean value) {
        parameterList.add(decimalFormat.format(value ? "true" : "false"));
        return SqlParameter.this;
    }

    public SqlParameter add(String value) {
        parameterList.add(value);
        return SqlParameter.this;
    }

    public SqlParameter addDate(Date date) {
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat(datePattern == null ? "yyyy-MM-dd" : datePattern);
        }
        parameterList.add(dateFormat.format(date));
        return SqlParameter.this;
    }

    public SqlParameter addTime(Date date) {
        if (timeFormat == null) {
            timeFormat = new SimpleDateFormat(timePattern == null ? "hh:mm:ss" : timePattern);
        }
        parameterList.add(timeFormat.format(date));
        return SqlParameter.this;
    }

    public SqlParameter addDateTime(Date date) {
        if (dateTimeFormat == null) {
            dateTimeFormat = new SimpleDateFormat(dateTimePattern == null ? "yyyy-MM-dd hh:mm:ss" : dateTimePattern);
        }
        parameterList.add(dateTimeFormat.format(date));
        return SqlParameter.this;
    }

    public SqlParameter addDecimal(BigDecimal value) {
        if (decimalFormat == null) {
            decimalFormat = new DecimalFormat(decimalPattern == null ? "#0.0#" : decimalPattern);
        }
        parameterList.add(decimalFormat.format(value));
        return SqlParameter.this;
    }

    public SqlParameter addQuantity(BigDecimal value) {
        if (quantityFormat == null) {
            quantityFormat = new DecimalFormat(quantityPattern == null ? "#0" : quantityPattern);
        }
        parameterList.add(quantityFormat.format(value));
        return SqlParameter.this;
    }

    public SqlParameter addDecimal(double value) {
        if (decimalFormat == null) {
            decimalFormat = new DecimalFormat(decimalPattern == null ? "#0.0#" : decimalPattern);
        }
        parameterList.add(decimalFormat.format(value));
        return SqlParameter.this;
    }

    public SqlParameter addObject(Object value) {
        parameterList.add(value.toString());
        return SqlParameter.this;
    }

    public WhereQuery addWhereQuery(String query) {
        if (whereQuery == null) {
            whereQuery = new WhereQuery();
        }
        //whereQuery.whereAndIn(query);
        return whereQuery;
    }

    public String toWhereQuery() {
        if (whereQuery == null) {
            return "";
        }
        return whereQuery.toString();
    }

    public String toJoinQuery() {
        return joinQuery;
    }

    public void setJoinQuery(String joinQuery) {
        this.joinQuery = joinQuery;
    }

    @Override
    public String toString() {
        if (query != null) {
            return query;
        }
        return fileName + "-" + queryName;
    }
}
