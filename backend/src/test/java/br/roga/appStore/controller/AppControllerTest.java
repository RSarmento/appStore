package br.roga.appStore.controller;

import br.roga.appStore.factory.AppFactory;
import br.roga.appStore.repository.AppRepository;
import br.roga.appStore.domain.App;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc
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
                "new name 1",
                "new type 1",
                1.0);

        mvc.perform(post("/apps")
                        .content(asJsonString(newApp))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", isA(Integer.class)))
                .andExpect(jsonPath("$.name", is(newApp.getName())))
                .andExpect(jsonPath("$.type", is(newApp.getType())))
                .andExpect(jsonPath("$.price", is(newApp.getPrice())));
    }

    @Test
    public void createApp_NewInvalidApp_ShouldThrowError() throws Exception {
        App newApp = appFactory.createApp(
                null,
                null,
                1.0);

        mvc.perform(post("/apps")
                        .content(asJsonString(newApp))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", isA(Integer.class)))
                .andExpect(jsonPath("$.name", is(newApp.getName())))
                .andExpect(jsonPath("$.type", is(newApp.getType())))
                .andExpect(jsonPath("$.price", is(newApp.getPrice())));
    }

    @Test
    public void getByNameAndType_NoAppsRegistered_ShouldThrowError(){

    }

    @Test
    public void getByNameAndType_OneAppRegistered_ShouldReturnOneApp() throws Exception {

        App newApp = appFactory.createApp(
                "new name 2",
                "new type 2",
                2.99);

        appRepository.save(newApp);

        mvc.perform(get("/apps")
                        .param("name", newApp.getName())
                        .param("type", newApp.getType()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(newApp.getId())))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.totalElements", is(1)))
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