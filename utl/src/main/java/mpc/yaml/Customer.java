package mpc.yaml;

import lombok.Data;
import mpu.X;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Data
public class Customer {
    private String firstName;
    private String lastName;
    private int age;
    // геттеры и сеттеры обязательно!

    public static void main(String[] args) throws FileNotFoundException {
        LoaderOptions loaderOptions = new LoaderOptions();
        Constructor constructor = new Constructor(Customer.class, loaderOptions);
        Yaml yaml = new Yaml(constructor);
        InputStream input = new FileInputStream("test.yaml");
        Customer customer = yaml.load(input);
        X.exit(customer);
    }
}