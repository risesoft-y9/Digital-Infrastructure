package net.risesoft.model;

import java.util.List;

public class CategoryResult extends Result{

    private List<Category> base_idcode_list;

    public List<Category> getBase_idcode_list() {
        return base_idcode_list;
    }

    public void setBase_idcode_list(List<Category> base_idcode_list) {
        this.base_idcode_list = base_idcode_list;
    }
}
