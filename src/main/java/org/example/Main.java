package org.example;

public class Main {
    public static void main(String[] args) throws Exception {
        UserService service = new UserService();

        var rep = service.getClass().getDeclaredField("repository");

        rep.setAccessible(true);
        rep.set(service, new FakeDBRepository());

        var method = service.getClass().getDeclaredMethod("getById", Long.class);


        service.create(new User(1L, "Gustavo"));


        System.out.println(method.invoke(service,1L));

        System.out.println(service.getById(1L).toString());
    }
}
