package srimalar.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.List;

@JsonRootName("paging_result")
public class PagingResult<T> extends Paging  {

    @JsonProperty("data")
    private List<T> data;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public void setPaging(Paging paging, long totalRecords, List<T> dataList) {
        setSize(dataList == null ? 0 : dataList.size());
        setTotalRecords(totalRecords);
        setLimit(paging.getLimit());
        setOffset(paging.getOffset());
        this.data = dataList;
    }

    public void setPaging(Paging paging, List<T> dataList) {
        setSize(dataList == null ? 0 : dataList.size());
        setTotalRecords(getSize());
        setLimit(paging.getLimit());
        setOffset(paging.getOffset());
        this.data = dataList;
    }
}
