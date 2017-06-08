package io.erfan.llogger.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Car {
    @Id(autoincrement = true)
    public Long id;
    public String name;
    public String plateNo;

    //region green dao stuff

    @Generated(hash = 1697099894)
    public Car(Long id, String name, String plateNo) {
        this.id = id;
        this.name = name;
        this.plateNo = plateNo;
    }
    @Generated(hash = 625572433)
    public Car() {
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
    public String getPlateNo() {
        return this.plateNo;
    }
    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    //endregion
}
