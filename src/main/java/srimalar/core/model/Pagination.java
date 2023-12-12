package srimalar.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("pagination")
public class Pagination extends Paging {
    @JsonProperty("name")
    private String name;
    @JsonProperty("search_text")
    private String searchText;

    @JsonProperty("sorting_text")
    private String sortingText;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getSortingText() {
        return sortingText;
    }

    public void setSortingText(String sortingText) {
        this.sortingText = sortingText;
    }

    @Override
    public String toString() {
        return new ToStringBuilder()
                .intValue("limit", getLimit())
                .longValue("offset", getOffset())
                .longValue("total_records", getTotalRecords())
                .intValue("size", getSize())
                .string("name", name)
                .string("search_text", searchText).toString();
    }
}
