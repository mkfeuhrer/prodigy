package com.gcc;

import com.gcc.common.Model;

public class CatModel extends Model {
    public String title, url;

    public CatModel() {
    }

    public CatModel(String title, String url) {
        this.title = title;
        this.url = url;
    }
}
