package examples;


public class Hello {
    private final String name;
    public static void main(String[] args) {
        Hello ds = new Hello("Data Science");

        System.out.println(String.format("Hello %s", ds.getName()));
    }

    public Hello(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
