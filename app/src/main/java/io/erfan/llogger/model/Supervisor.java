package io.erfan.llogger.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Supervisor {
    @Id(autoincrement = true)
    private Long id;
    private String name;
    private String licenceNo;

    //region greendao stuff

    @Generated(hash = 1601652680)
    public Supervisor(Long id, String name, String licenceNo) {
        this.id = id;
        this.name = name;
        this.licenceNo = licenceNo;
    }
    @Generated(hash = 218707262)
    public Supervisor() {
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
    public String getLicenceNo() {
        return this.licenceNo;
    }
    public void setLicenceNo(String licenceNo) {
        this.licenceNo = licenceNo;
    }

    //endregion
}
