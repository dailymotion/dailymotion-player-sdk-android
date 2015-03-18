package com.dailymotion.sdk.api.model;

import java.util.ArrayList;

public class PagedList<T> {
    public int page;
    public int limit;
    public boolean hasMore;
    public int total;
    public boolean explicit;
    public ArrayList<T> list = new ArrayList<T>();
}
