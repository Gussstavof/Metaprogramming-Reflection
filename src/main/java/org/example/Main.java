package org.example;

public class Main {
    public static void main(String[] args) throws Exception {
        UserService service = new UserService();

        var rep = service.getClass().getDeclaredField("repository");

        if (rep.isAnnotationPresent(Injection.class)){
            rep.setAccessible(true);
            rep.set(service, new FakeDBRepository());
        }

        var method = service.getClass().getDeclaredMethod("getById", Long.class);
        User user = new User(1L, "Gustavo");

        service.create(user);

        System.out.println(method.invoke(service,1L));

        var fieldName = method.getReturnType().getDeclaredField("name");

        fieldName.setAccessible(true);
        fieldName.set(user, "Ferreira");

        System.out.println(service.getById(1L).toString());
    }
}
