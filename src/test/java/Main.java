import dev.kutuptilkisi.internal.database.DatabaseConnector;
import dev.kutuptilkisi.internal.database.DatabaseCredentials;


import java.util.List;

public class Main {
    public static void main(String[] args){
        DatabaseCredentials credentials = new DatabaseCredentials("localhost", 3306, "kutup", "root", "");
        DatabaseConnector connector = new DatabaseConnector(credentials);

        Runtime.getRuntime().addShutdownHook(new Thread(connector::disconnect));

        connector.connect();
        connector.executeRawSQL("drop table if exists users");
        connector.registerModal(UserModal.class);

        UserModal _A = connector.create(new UserModal("admin"));
        UserModal _K = connector.create(new UserModal("kutup", 5));

        List<UserModal> users = connector.findMany(new UserModal());
        for(UserModal user : users){
            System.out.println(user.getId());
            System.out.println(user.getNickname());
            System.out.println(user.getName());
        }

        while (true){

        }
    }
}
