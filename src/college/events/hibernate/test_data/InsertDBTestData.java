package college.events.hibernate.test_data;

import college.events.hibernate.DbManager;
import college.events.hibernate.QueryResponse;
import college.events.hibernate.entities.UniversitiesEntity;
import java.util.List;

public class InsertDBTestData {
    public static void insertTestData(DbManager manager) {

        QueryResponse getUniversitiesByName = manager.getUniversities();
        List<UniversitiesEntity> unis = null;
        try {
            unis = (List<UniversitiesEntity>) getUniversitiesByName.getPayload();
            if(unis == null || unis.size() == 0) {
                manager.createUniversity("University of Central Florida", "", "", "preload");
                unis = (List<UniversitiesEntity>) manager.getUniversities().getPayload();
            }
        } catch (Exception e) {
            return;
        }

        if(unis == null) {
            return;
        }

        manager.createSuperAdmin("br109653", "password", "Brandon", "Gotay", "b@admin.com");

        manager.createAdmin("admin1", "password", "Brandon", "Gotay", "a1@admin.com", unis.get(0).getUniId());

        manager.createStudent("aaaaa", "password", "Brandon", "Gotay", "a@a.a", unis.get(0).getUniId());
        manager.createStudent("bbbbb", "password", "Brandon", "Gotay", "b@a.a", unis.get(0).getUniId());
        manager.createStudent("ccccc", "password", "Brandon", "Gotay", "c@a.a", unis.get(0).getUniId());
        manager.createStudent("ddddd", "password", "Brandon", "Gotay", "d@a.a", unis.get(0).getUniId());
        manager.createStudent("eeeee", "password", "Brandon", "Gotay", "e@a.a", unis.get(0).getUniId());
        manager.createStudent("fffff", "password", "Brandon", "Gotay", "f@a.a", unis.get(0).getUniId());
        manager.createStudent("ggggg", "password", "Brandon", "Gotay", "g@a.a", unis.get(0).getUniId());
        manager.createStudent("hhhhh", "password", "Brandon", "Gotay", "h@a.a", unis.get(0).getUniId());
        manager.createStudent("iiiii", "password", "Brandon", "Gotay", "i@a.a", unis.get(0).getUniId());
        manager.createStudent("jjjjj", "password", "Brandon", "Gotay", "j@a.a", unis.get(0).getUniId());
        manager.createStudent("kkkkk", "password", "Brandon", "Gotay", "k@a.a", unis.get(0).getUniId());
        manager.createStudent("lllll", "password", "Brandon", "Gotay", "l@a.a", unis.get(0).getUniId());
        manager.createStudent("mmmmm", "password", "Brandon", "Gotay", "m@a.a", unis.get(0).getUniId());
        manager.createStudent("nnnnn", "password", "Brandon", "Gotay", "n@a.a", unis.get(0).getUniId());
        manager.createStudent("ooooo", "password", "Brandon", "Gotay", "o@a.a", unis.get(0).getUniId());
        manager.createStudent("ppppp", "password", "Brandon", "Gotay", "p@a.a", unis.get(0).getUniId());
        manager.createStudent("qqqqq", "password", "Brandon", "Gotay", "q@a.a", unis.get(0).getUniId());
        manager.createStudent("rrrrr", "password", "Brandon", "Gotay", "r@a.a", unis.get(0).getUniId());
        manager.createStudent("sssss", "password", "Brandon", "Gotay", "s@a.a", unis.get(0).getUniId());
        manager.createStudent("ttttt", "password", "Brandon", "Gotay", "t@a.a", unis.get(0).getUniId());
        manager.createStudent("uuuuu", "password", "Brandon", "Gotay", "u@a.a", unis.get(0).getUniId());
        manager.createStudent("vvvvv", "password", "Brandon", "Gotay", "v@a.a", unis.get(0).getUniId());
        manager.createStudent("wwwww", "password", "Brandon", "Gotay", "w@a.a", unis.get(0).getUniId());
        manager.createStudent("xxxxx", "password", "Brandon", "Gotay", "x@a.a", unis.get(0).getUniId());
        manager.createStudent("yyyyy", "password", "Brandon", "Gotay", "y@a.a", unis.get(0).getUniId());
        manager.createStudent("zzzzz", "password", "Brandon", "Gotay", "z@a.a", unis.get(0).getUniId());
    }
}
