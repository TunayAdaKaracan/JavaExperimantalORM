package dev.kutuptilkisi.util.filter;

public class Filter {

    // TODO: Add more filter types
    public enum FilterType{
        EQ("=");

        private String op;
        FilterType(String operator){
            this.op = operator;
        }

        public String getOperator(){
            return this.op;
        }
    }

    private final String column;
    private final Object o;
    private final FilterType filterType;

    private Filter(String column, Object o, FilterType filterType){
        this.column = column;
        this.o = o;
        this.filterType = filterType;
    }

    public String getColumn() {
        return column;
    }

    public Object getObject() {
        return o;
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public static Filter eq(String column, Object o){
        return new Filter(column, o, FilterType.EQ);
    }
}
