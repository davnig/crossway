package it.units.crossway.client.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "api", url = "http://localhost:8080")
public interface Api {

    @RequestMapping(method = RequestMethod.POST, value = "/players")
    PlayerDto addPlayer(@RequestBody PlayerDto playerDto);

}
