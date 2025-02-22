package rendeleskezelo.service;


import rendeleskezelo.model.Lamp;
import rendeleskezelo.repository.UserRepository;

import java.util.List;

public interface LampService {
    void AddLamp(Lamp lamp);
    void UpdateLamp(Lamp lamp);
    void DeleteLamp(Lamp lamp);
    Lamp GetLampByID(long id);
    List<Lamp> GetAllLamps();
    Lamp GetLampByName(String name);
    List<Lamp>lampsWithSupplyZeroOrLess();
    void UpdateLampSupply(Lamp lamp, int amount);





}
