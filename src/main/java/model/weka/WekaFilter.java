package model.weka;


import weka.filters.Filter;

public class WekaFilter {

    private Filter filter ;
    private String filterName ;
    private String searchMethod ;
    private Boolean backward ;


    public WekaFilter(Filter filter, String filterName, String searchMethod, Boolean backward) {
        this.filter = filter ;
        this.filterName = filterName ;
        this.searchMethod = searchMethod ;
        this.backward = backward ;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public String getSearchMethod() {
        return searchMethod;
    }

    public void setSearchMethod(String searchMethod) {
        this.searchMethod = searchMethod;
    }

    public Boolean getBackward() {
        return backward;
    }

    public void setBackward(Boolean backward) {
        this.backward = backward;
    }
}