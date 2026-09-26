@Component
@Primary
public class SonySpeakers implements Speakers{
    public String makeSound(){
        return "Playing music with Sony speakers";
    }
}
