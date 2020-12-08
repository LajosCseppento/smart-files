package dev.lajoscseppento.smartfiles.service;

import dev.lajoscseppento.smartfiles.model.Model;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.file.Path;

@Service
public class ModelService {

    private Path persistenceDirectory;
    private Model model;

    @PostConstruct
    public void init() {
//        persistenceDirectory = Paths.get("persistence/").toAbsolutePath().normalize();
//        try {
//            model = readModel()
//        }catch ()
    }


}
