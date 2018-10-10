package college.events.hibernate.test_data;

import college.events.hibernate.DbManager;
import college.events.hibernate.QueryResponse;

public class InsertDBTestData {
    public static void insertTestData(DbManager manager) {
        manager.createUniversity("University of Central Florida", "", "", 50000);

        QueryResponse getUniversitiesByName = manager.getUniversities();

        manager.createAccount("aaaaa", "password", "Brandon", "Gotay", "a@a.a");
        manager.createAccount("bbbbb", "password", "Brandon", "Gotay", "b@a.a");
        manager.createAccount("ccccc", "password", "Brandon", "Gotay", "c@a.a");
        manager.createAccount("ddddd", "password", "Brandon", "Gotay", "d@a.a");
        manager.createAccount("eeeee", "password", "Brandon", "Gotay", "e@a.a");
        manager.createAccount("fffff", "password", "Brandon", "Gotay", "f@a.a");
        manager.createAccount("ggggg", "password", "Brandon", "Gotay", "g@a.a");
        manager.createAccount("hhhhh", "password", "Brandon", "Gotay", "h@a.a");
        manager.createAccount("iiiii", "password", "Brandon", "Gotay", "i@a.a");
        manager.createAccount("jjjjj", "password", "Brandon", "Gotay", "j@a.a");
        manager.createAccount("kkkkk", "password", "Brandon", "Gotay", "k@a.a");
        manager.createAccount("lllll", "password", "Brandon", "Gotay", "l@a.a");
        manager.createAccount("mmmmm", "password", "Brandon", "Gotay", "m@a.a");
        manager.createAccount("nnnnn", "password", "Brandon", "Gotay", "n@a.a");
        manager.createAccount("ooooo", "password", "Brandon", "Gotay", "o@a.a");
        manager.createAccount("ppppp", "password", "Brandon", "Gotay", "p@a.a");
        manager.createAccount("qqqqq", "password", "Brandon", "Gotay", "q@a.a");
        manager.createAccount("rrrrr", "password", "Brandon", "Gotay", "r@a.a");
        manager.createAccount("sssss", "password", "Brandon", "Gotay", "s@a.a");
        manager.createAccount("ttttt", "password", "Brandon", "Gotay", "t@a.a");
        manager.createAccount("uuuuu", "password", "Brandon", "Gotay", "u@a.a");
        manager.createAccount("vvvvv", "password", "Brandon", "Gotay", "v@a.a");
        manager.createAccount("wwwww", "password", "Brandon", "Gotay", "w@a.a");
        manager.createAccount("xxxxx", "password", "Brandon", "Gotay", "x@a.a");
        manager.createAccount("yyyyy", "password", "Brandon", "Gotay", "y@a.a");
        manager.createAccount("zzzzz", "password", "Brandon", "Gotay", "z@a.a");
    }
}
