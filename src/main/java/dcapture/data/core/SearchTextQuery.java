package dcapture.data.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Like Query
 *
 * @author Ramesh
 * @since Oct, 2016
 */
public class SearchTextQuery {
    private final String searchText;
    private List<String> columnList;

    public SearchTextQuery(String searchText) {
        this.searchText = "%" + searchText.trim().toLowerCase() + "%";
        columnList = new ArrayList<>();
    }

    public void add(String... fieldArray) {
        Collections.addAll(columnList, fieldArray);
    }

    int getColumnSize() {
        return columnList.size();
    }

    String getSearchText() {
        return searchText;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (String column : columnList) {
            builder.append(" OR LOWER(").append(column).append(") LIKE ? ");
        }
        String query = builder.toString().replaceFirst(" OR", "(");
        return " " + query.concat(") ");
    }

    public static boolean isValid(String searchText) {
        return searchText != null && !searchText.trim().isEmpty();
    }
}
