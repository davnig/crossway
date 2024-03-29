package it.units.crossway.client.remote;

import feign.Response;
import it.units.crossway.client.model.dto.GameCreationIntent;
import it.units.crossway.client.model.dto.GameDto;
import it.units.crossway.client.model.dto.PlayerDto;
import it.units.crossway.client.model.dto.StonePlacementIntentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "api", url = "http://localhost:9111")
public interface Api {

    @RequestMapping(method = RequestMethod.GET, value = "/games/available")
    List<GameDto> getAllAvailableGames();

    @RequestMapping(method = RequestMethod.POST, value = "/players")
    PlayerDto addPlayer(@RequestBody PlayerDto playerDto);

    @RequestMapping(method = RequestMethod.DELETE, value = "/players/{player}")
    PlayerDto deletePlayerByNickname(@PathVariable String player);

    @RequestMapping(method = RequestMethod.POST, value = "/games")
    GameDto createGame(@RequestBody GameCreationIntent gameCreationIntent);

    @RequestMapping(method = RequestMethod.DELETE, value = "/games/{uuid}")
    Response deleteGameByUuid(@PathVariable String uuid);

    @RequestMapping(method = RequestMethod.POST, value = "/games/{uuid}/events/joining")
    GameDto joinGame(@PathVariable String uuid, @RequestBody PlayerDto playerDto);

    @RequestMapping(method = RequestMethod.POST, value = "/games/{uuid}/events/placement")
    Response placeStone(@PathVariable String uuid, @RequestBody StonePlacementIntentDto stonePlacementIntentDto);

    @RequestMapping(method = RequestMethod.POST, value = "/games/{uuid}/events/pie-rule")
    Response acceptPieRule(@PathVariable String uuid, @RequestBody PlayerDto playerDto);

}
