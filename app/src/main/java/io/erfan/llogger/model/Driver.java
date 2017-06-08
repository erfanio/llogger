package io.erfan.llogger.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Driver {
    @Id(autoincrement = true)
    private Long id;
    private String name;

    //region greendao stuff

    @Generated(hash = 813833227)
    public Driver(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    @Generated(hash = 911393595)
    public Driver() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    //endregion
}
