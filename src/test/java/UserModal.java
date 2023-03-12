import dev.kutuptilkisi.internal.annotations.*;
import dev.kutuptilkisi.internal.database.structures.Modal;

import java.sql.Timestamp;

@Table(name = "Users")
public class UserModal extends Modal {

    // TODO: Modify table on column change
    // TODO: Converters
    // TODO: Referances

    @Id
    @NotNull
    @AutoIncrement
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Column(name = "username")
    private String name;

    @Default(defaultString = "newuser001")
    @Column(name = "nickname")
    private String nickname;

    @Column(name = "posts")
    @Default(defaultString = "5")
    private Integer posts;

    @Column(name = "registeredAt")
    @Default(defaultString = "NOW()")
    private Timestamp timestamp;

    public UserModal(){}

    public UserModal(String name){
        this.name = name;
    }

    public UserModal(String name, int posts){
        this.name = name;
        this.posts = posts;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNickname() {
        return nickname;
    }

    public int getPosts() {
        return posts;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
