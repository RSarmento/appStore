package br.roga.appStore.factory;

import br.roga.appStore.repository.AppRepository;
import br.roga.appStore.domain.App;

public class AppFactory {

    private AppRepository appRepository;

    public AppFactory(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    public App createApp(String name, String type, Double price){
        App app = new App();
        app.setName(name);
        app.setPrice(price);
        app.setType(type);
        return appRepository.save(app);
    }
}
