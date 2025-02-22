package rendeleskezelo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rendeleskezelo.model.Lamp;
import rendeleskezelo.model.User;
import rendeleskezelo.repository.LampRepository;
import rendeleskezelo.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
public class LampServiceImpl implements LampService {
    private final LampRepository lampRepository;

    @Autowired
    public LampServiceImpl(LampRepository lampRepository) {
        this.lampRepository = lampRepository;
    }

    @Override
    public void AddLamp(Lamp lamp) {
        lampRepository.save(lamp);
    }

    @Override
    public void UpdateLamp(Lamp lamp) {
        Lamp lamp1 = lampRepository.findLampById(lamp.getId());
        lamp1.setName(lamp.getName());
        lamp1.setId(lamp.getId());
        lamp1.setSupply(lamp.getSupply());
        lamp1.setPrice(lamp.getPrice());
        lampRepository.save(lamp1);
    }

    @Override
    public void DeleteLamp(Lamp lamp) {
        lampRepository.delete(lamp);
    }

    @Override
    public Lamp GetLampByID(long id) {
        return lampRepository.findLampById(id);
    }

    @Override
    public List<Lamp> GetAllLamps() {
        List<Lamp> lamps = lampRepository.findAll();

            return lamps != null ? lamps : new ArrayList<>();


    }

    @Override
    public Lamp GetLampByName(String name) {
        return lampRepository.findLampByName(name);
    }

    @Override
    public List<Lamp> lampsWithSupplyZeroOrLess() {
        return lampRepository.findLampsWithSupplyLessThanOrEqual(0);
    }

    @Override
    public void UpdateLampSupply(Lamp lamp, int amount) {
        lamp.setSupply(lamp.getSupply() + amount);
        lampRepository.save(lamp);
    }


}
