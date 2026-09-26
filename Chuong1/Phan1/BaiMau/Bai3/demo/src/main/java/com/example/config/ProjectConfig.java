@Configuration
@ComponentScan(
    basePackages = {"com.example.implementation",
                    "com.example.services"})
@ComponentScan(
    basePackagesClasses = {com.example.beans.Vehicle.class,
                            com.example.beans.Person.class})
public class ProjectConfig {
    
}
