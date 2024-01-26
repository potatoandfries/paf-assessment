package vttp2023.batch4.paf.assessment.repositories;

public class Queries {
    
    public static final String SQL_INSERT_BOOKING="""
        INSERT INTO orders(listing_id, email, duration)
        VALUES 
            (?,?,?)

    """;


    public static final String SQL_FIND_BY_EMAIL =  """
        select * from users
           where email = ?
           
     """;

    public static final String SQL_INSERT_NEW_USER = """
        INSERT INTO users(email, name)
        VALUES 
            (?,?)
    """;
}
