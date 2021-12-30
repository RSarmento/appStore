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

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/apps")
public class AppController {

    @Autowired
    private AppRepository appRepository;

    @PostMapping
    public ResponseEntity<App> createApp(@Valid @RequestBody App app) throws DataValidationException {
        appRepository.save(app);
        return ResponseEntity.ok().body(appRepository.save(app));
    }

    @GetMapping
    public ResponseEntity<List<App>> getAllApps(){
        return ResponseEntity.ok().body(appRepository.findAll());
    }

    @GetMapping("/byNameAndType")
    public ResponseEntity<Page<App>> getAppsByNameAndType(@RequestParam(value = "name", required = false, defaultValue = "") String name,
                                                          @RequestParam(value = "type", required = false, defaultValue = "") String type,
                                                          Pageable pageable){
        return ResponseEntity.ok(appRepository.findAllByNameAndType(name, type, pageable));
    }

    @GetMapping("/byTypeAndLowerPrice")
    public ResponseEntity<Page<App>> getAppsByTypeAndLowerPrice(@RequestParam("type") String type,
                                                                Pageable pageable){
        return ResponseEntity.ok(appRepository.findAllByTypeOrderByLowerPrice(type, pageable));
    }

}
