package by.veremei.api.models.users;

import lombok.Data;

@Data
public class UserData {
    Integer id;
    String email;
    String first_name;
    String last_name;
    String avatar;
}
