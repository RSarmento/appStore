package br.roga.appStore.controller;

import br.roga.appStore.domain.App;
import br.roga.appStore.factory.AppFactory;
import br.roga.appStore.repository.AppRepository;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class AppControllerTest {

    private AppFactory appFactory;

    @Autowired
    private AppRepository appRepository;

    @Autowired
    private MockMvc mvc;

    @Before
    public void setUp() {
        appFactory = new AppFactory(appRepository);
    }

    @Test
    public void createApp_NewValidApp_ShouldReturnNewApp() throws Exception {
        App newApp = appFactory.createApp(
                "new name",
                "new type",
                1.0);

        mvc.perform(post("/apps")
                        .content(asJsonString(newApp))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(newApp.getId().intValue())))
                .andExpect(jsonPath("$.name", is(newApp.getName())))
                .andExpect(jsonPath("$.type", is(newApp.getType())))
                .andExpect(jsonPath("$.price", is(newApp.getPrice())));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void createApp_NewInvalidAppLackingName_ShouldThrowError() throws Exception {
        App newApp = appFactory.createApp(
                null,
                "type",
                1.0);

        mvc.perform(post("/apps")
                        .content(asJsonString(newApp))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)))
                .andExpect(jsonPath("$.pageable.pageNumber", is(0)));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void createApp_NewInvalidAppLackingType_ShouldThrowError() throws Exception {
        App newApp = appFactory.createApp(
                "name",
                null,
                1.0);

        mvc.perform(post("/apps")
                        .content(asJsonString(newApp))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)))
                .andExpect(jsonPath("$.pageable.pageNumber", is(0)));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void createApp_NewInvalidAppLackingPrice_ShouldThrowError() throws Exception {
        App newApp = appFactory.createApp(
                "name",
                "type",
                null);

        mvc.perform(post("/apps")
                        .content(asJsonString(newApp))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)))
                .andExpect(jsonPath("$.pageable.pageNumber", is(0)));
    }

    @Test(expected = NestedServletException.class)
    public void createApp_NewInvalidAppNegativePrice_ShouldThrowError() throws Exception {
        App newApp = appFactory.createApp(
                "name",
                "type",
                -1.0);

        mvc.perform(post("/apps")
                        .content(asJsonString(newApp))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)))
                .andExpect(jsonPath("$.pageable.pageNumber", is(0)));
    }

    @Test
    public void getAppsByNameAndType_OneAppRegistered_ShouldReturnOneApp() throws Exception {

        App newApp = appFactory.createApp(
                "new name 1",
                "new type 1",
                2.99);

        appRepository.save(newApp);

        mvc.perform(get("/apps/byNameAndType")
                        .param("name", newApp.getName())
                        .param("type", newApp.getType()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(newApp.getId().intValue())))
                .andExpect(jsonPath("$.content[0].name", is(newApp.getName())))
                .andExpect(jsonPath("$.content[0].type", is(newApp.getType())))
                .andExpect(jsonPath("$.content[0].price", is(newApp.getPrice())))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.pageable.pageNumber", is(0)));
    }

    @Test
    public void getAppsByNameAndType_MultipleAppsRegistered_ShouldReturnOneApp() throws Exception {

        App newApp1 = appFactory.createApp(
                "new name 1",
                "new type 1",
                2.99);

        appRepository.save(newApp1);

        App newApp2 = appFactory.createApp(
                "new name 2",
                "new type 2",
                2.99);

        appRepository.save(newApp2);

        mvc.perform(get("/apps/byNameAndType")
                        .param("name", newApp1.getName())
                        .param("type", newApp1.getType()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(newApp1.getId().intValue())))
                .andExpect(jsonPath("$.content[0].name", is(newApp1.getName())))
                .andExpect(jsonPath("$.content[0].type", is(newApp1.getType())))
                .andExpect(jsonPath("$.content[0].price", is(newApp1.getPrice())))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.pageable.pageNumber", is(0)));
    }

    @Test
    public void getAppsByNameAndType_MultipleAppsRegistered_ShouldReturnTwoAppsSameName() throws Exception {

        App newApp1 = appFactory.createApp(
                "new name 1",
                "new type 1",
                2.99);

        appRepository.save(newApp1);

        App newApp2 = appFactory.createApp(
                newApp1.getName(),
                "new type 2",
                2.99);

        appRepository.save(newApp2);

        mvc.perform(get("/apps/byNameAndType")
                        .param("name", newApp1.getName()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(newApp1.getId().intValue())))
                .andExpect(jsonPath("$.content[0].name", is(newApp1.getName())))
                .andExpect(jsonPath("$.content[0].type", is(newApp1.getType())))
                .andExpect(jsonPath("$.content[0].price", is(newApp1.getPrice())))
                .andExpect(jsonPath("$.content[1].id", is(newApp2.getId().intValue())))
                .andExpect(jsonPath("$.content[1].name", is(newApp2.getName())))
                .andExpect(jsonPath("$.content[1].type", is(newApp2.getType())))
                .andExpect(jsonPath("$.content[1].price", is(newApp2.getPrice())))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.pageable.pageNumber", is(0)));
    }

    @Test
    public void getAppsByNameAndType_MultipleAppsRegistered_ShouldReturnTwoAppsSameType() throws Exception {

        App newApp1 = appFactory.createApp(
                "new name 1",
                "new type 1",
                2.99);

        appRepository.save(newApp1);

        App newApp2 = appFactory.createApp(
                "new name 2",
                newApp1.getType(),
                2.99);

        appRepository.save(newApp2);

        mvc.perform(get("/apps/byNameAndType")
                        .param("type", newApp1.getType()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(newApp1.getId().intValue())))
                .andExpect(jsonPath("$.content[0].name", is(newApp1.getName())))
                .andExpect(jsonPath("$.content[0].type", is(newApp1.getType())))
                .andExpect(jsonPath("$.content[0].price", is(newApp1.getPrice())))
                .andExpect(jsonPath("$.content[1].id", is(newApp2.getId().intValue())))
                .andExpect(jsonPath("$.content[1].name", is(newApp2.getName())))
                .andExpect(jsonPath("$.content[1].type", is(newApp2.getType())))
                .andExpect(jsonPath("$.content[1].price", is(newApp2.getPrice())))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.pageable.pageNumber", is(0)));
    }

    @Test
    public void getAppsByTypeAndLowerPrice_MultipleAppsRegistered_ShouldReturnAppsAscendantOrderByPrice() throws Exception {
        App middlePriceApp = appFactory.createApp(
                "new name 1",
                "new type 1",
                0.99);

        appRepository.save(middlePriceApp);

        App highestPriceApp = appFactory.createApp(
                "new name 2",
                middlePriceApp.getType(),
                1.00);

        appRepository.save(highestPriceApp);

        App lowerPriceApp = appFactory.createApp(
                "new name 2",
                middlePriceApp.getType(),
                0.50);

        appRepository.save(lowerPriceApp);

        mvc.perform(get("/apps/byTypeAndLowerPrice")
                        .param("type", middlePriceApp.getType()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].price", is(lowerPriceApp.getPrice())))
                .andExpect(jsonPath("$.content[1].price", is(middlePriceApp.getPrice())))
                .andExpect(jsonPath("$.content[2].price", is(highestPriceApp.getPrice())))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.totalElements", is(3)))
                .andExpect(jsonPath("$.pageable.pageNumber", is(0)));
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}