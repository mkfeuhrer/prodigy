package com.gcc;

import com.gcc.common.Model;

public class BrandModel extends Model {
    public String title, url;

    public BrandModel() {
    }

    public BrandModel(String title, String url) {
        this.title = title;
        this.url = url;
    }
}
