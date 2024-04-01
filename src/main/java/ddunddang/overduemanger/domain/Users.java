package ddunddang.overduemanger.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Users {

    public Users() {
    }

    public Users(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    @Getter
    @Setter
    @Id
    private String userId;
    @Getter
    private String userName;

    @Getter
    @OneToMany
    List<CheckOut> checkOutList = new ArrayList<>();

    public void addCheckOut(CheckOut checkOut) {
        this.checkOutList.add(checkOut);
        if(checkOut.getUsers() != this) checkOut.setUsers(this);
    }
}
