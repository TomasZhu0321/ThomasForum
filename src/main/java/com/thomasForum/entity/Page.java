package com.thomasForum.entity;

public class Page {

    // current page
    private int current = 1;
    //post limit for one page
    private int limit = 10;

    //total posts
    private int rows = 0;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private String path;
    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current){
        if(current >= 1) {
            this.current = current;
        }
    }
    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if(rows >= 0){
            this.rows = rows;
        }
    }

    /**
     * get current page starting row
     * @return
     */
    public int getOffset(){
        // current * limit - limit
        return current * limit - limit;
    }
    /**
     * get number of total pages
     */
    public int getTotal(){
        //rows/limit + 1;
        if(rows%limit == 0){
            return rows/limit;
        }else {
            return rows/limit + 1;
        }
    }
    /**
     * get starting page
     */
    public int getFrom(){
        if(current - 2 > 0){
            return current - 2;
        }
        else {
            return 1;
        }
    }
    /**
     * get ending page
     */
    public int getTo(){
        if(current + 2 <= getTotal()){
            return current + 2;
        }
        else {
            return getTotal();
        }
    }
}
