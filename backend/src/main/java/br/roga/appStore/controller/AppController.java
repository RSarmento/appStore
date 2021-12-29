package br.roga.appStore.controller;

import br.roga.appStore.expections.DataValidationException;
import br.roga.appStore.repository.AppRepository;
import br.roga.appStore.domain.App;
import liquibase.pro.packaged.D;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apps")
public class AppController {

    @Autowired
    private AppRepository appRepository;

    @PostMapping
    public ResponseEntity<App> createApp(@RequestParam("name") String name,
                                         @RequestParam("type") String type,
                                         @RequestParam("price") Double price) throws DataValidationException {
        App newApp = new App();
        newApp.setName(name);
        newApp.setType(type);
        newApp.setPrice(price);

        validateNewApp(newApp);

        appRepository.save(newApp);
        return ResponseEntity.ok().body(newApp);
    }

    @GetMapping
    public ResponseEntity<List<App>> getAllApps(){
        return ResponseEntity.ok().body(appRepository.findAll());
    }

    @GetMapping("/byNameAndType")
    public ResponseEntity<Page<App>> getAppsByNameAndType(@RequestParam("name") String name,
                                                          @RequestParam("type") String type,
                                                          Pageable pageable){
        return ResponseEntity.ok(appRepository.findAllByNameAndType(name, type, pageable));
    }

    @GetMapping("/lowerPrice")
    public ResponseEntity<Page<App>> getAppsByTypeAndLowerPrice(@RequestParam("type") String type,
                                                                Pageable pageable){
        return ResponseEntity.ok(appRepository.findAllByTypeOrderByLowerPrice(type, pageable));
    }

    private void validateNewApp(App newApp) throws DataValidationException {
        if (newApp.getName() == null || newApp.getName().isBlank() || newApp.getName().isEmpty())
            throw new DataValidationException("App name cannot be empty. ");
        if (newApp.getType() == null || newApp.getType().isBlank() || newApp.getType().isEmpty())
            throw new DataValidationException("App type cannot be empty. ");
        if (newApp.getPrice() == null || newApp.getPrice() < 0.0)
            throw new DataValidationException("App price cannot be negative. ");
    }
}
