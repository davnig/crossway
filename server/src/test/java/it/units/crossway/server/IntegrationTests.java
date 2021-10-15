package it.units.crossway.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.units.crossway.server.model.dto.PlayerDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTests {

    @Autowired
    private MockMvc mvc;

    @Test
    void when_postPlayer_should_saveInDb() throws Exception {
        PlayerDto user = new PlayerDto("username");
        ObjectMapper om = new ObjectMapper();
        mvc.perform(post("/players")
                        .content(om.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
//                .andDo(print())
                .andExpect(jsonPath("$.nickname", is("username")));
    }

}
