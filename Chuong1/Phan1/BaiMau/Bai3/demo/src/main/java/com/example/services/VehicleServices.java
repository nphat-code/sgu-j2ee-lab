@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class VehicleServices {
    @AutoWired
    private Speakers speakers;
    private Tyres tyres;
    public VehicleServices(){
        System.out.println("VehicleServices object is created");
    }
    public void playMusic(){
        String music = speakers.makeSound();
        System.out.println(music);
    }
    public void moveVehicle(){
        String status = tyres.rotate();
        System.out.println(status);
    }
    public Speakers getSpeakers(){
        return speakers;
    }
    public void setSpeakers(Speakers speakers){
        this.speakers = speakers;
    }
    public Tyres getTyres(){
        return tyres;
    }
    @AutoWired
    public void setTyres(Tyres tyres){
        this.tyres = tyres;
    }
}
