import java.sql.*;
import java.lang.UnsupportedOperationException;
import gov.nist.sip.accounting.BlockManager;

class UserNotFoundException extends Exception {
    public UserNotFound(String message) {
        super(message);
    }
}

class UserBlockedException extends Exception {
    public UserBlocked(String message) {
        super(message);
    }
}

class User {
    private int id;
    private String password;

    User(int id, String password) {
        this.id = id;
        this.password = password;
    }
    static public User fromURI(String uri) throws UserNotFound {
        String selectQuery = "SELECT\n"
                           + "    id, password\n"
                           + "FROM\n"
                           + "    user\n"
                           + "WHERE\n"
                           + "    name = ?\n"
        try {
        	PreparedStatement preparedStatement = Database.getConnection().prepareStatement(selectQuery);
        	preparedStatement.setInt(1, uri);
	        ResultSet rs = preparedStatement.executeQuery();
	      
	        row = rs.next();
            if (row == false) {
                throw UserNotFoundException();
            }
            userid = rs.getInt("id");
            password = rs.getString("password");
            return new User(userid, password);
        }
        catch (SQLException e) {
        	e.printStackTrace();
        	return null;
        }
    }
    public boolean hasBlocked(User target) {
        return BlockManager.getInstance().isBlocked(this.id, target.id);
    }
    public void invite(User target) {
        if (this.hasBlocked(target)) {
            throw new UserBlocked();
        }
    }
    public void cancel(User target) {
    }
    public void ack(User target, int callId) {
        BillingManager.getInstance().beginCall(callId);
    }
    public void bye(User target, int callId) {
        BillingManager.getInstance().endCall(callId);
    }
    public hash(String password) {
        // TODO
        return password;
    }
    public boolean authenticate(String password) {
        return this.hash(password) == password;
    }
}
